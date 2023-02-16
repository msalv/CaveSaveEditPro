package com.leo.cse.backend.exe;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Generic PE EXE handling classes, used to patch games. The hope is that having
 * something this generic will reduce the chance of any faults going unseen. THE
 * FULL LIST OF ASSUMPTIONS: 1. Tools expect (but we don't) sections to be in
 * virtual order (this is my fault -- PEONS dev) 2. Tools and us expect .text to
 * be at FA 0x1000, and for .text/.rdata/.data to be linearized. If we can't
 * ensure this, we fail for safety. (The value is adjustible per-game, but
 * cannot be eliminated) 3. Old-Style Relocations and Line Numbers never happen.
 * If we encounter them, we fail for safety. 4. The executable is assumed to not
 * have relocation data. If it does, we ignore it - it's assumed we already know
 * and can handle it. 5. The resource chunk starts at its section and continues
 * to the end of the chunk. If this is not the case, then Xin probably started
 * cannibalizing it and we should just pretend it's not a resource section, but
 * we do NOT have to fail in this case so long as that is kept in mind.
 * 
 * @author 20kdc
 */
public class PEFile {
	// Always use headerPosition(x) before any sequence of operations,
	// and always keep in LITTLE_ENDIAN order
	private final ByteBuffer earlyHeaderBB;
	// The complete list of sections.
	private final List<Section> sections = new ArrayList<>();

	public PEFile(ByteBuffer source, int expectedTex) throws IOException {
		source.order(ByteOrder.LITTLE_ENDIAN);
		source.clear();

		// NOTE: The section headers in this are not to be trusted.
		// Also note, this implicitly defines the expectedTex.
		final byte[] earlyHeader = new byte[expectedTex];
		source.get(earlyHeader);

		earlyHeaderBB = ByteBuffer.wrap(earlyHeader);
		earlyHeaderBB.order(ByteOrder.LITTLE_ENDIAN);
		earlyHeaderBB.position(getNtHeaders());

		if (earlyHeaderBB.getInt() != 0x00004550) {
			throw new IOException("Not a PE file.");
		}

		earlyHeaderBB.getShort(); // We don't actually need to check the machine - if anything is wrong, see
									// opt.header magic

		final int sectionCount = earlyHeaderBB.getShort() & 0xFFFF;
		earlyHeaderBB.getInt();
		earlyHeaderBB.getInt(); // symtab address

		if (earlyHeaderBB.getInt() != 0) {
			throw new IOException(
					"This file was linked with a symbol table. Since we don't want to accidentally destroy it, you get this error instead.");
		}

		final int optHeadSize = earlyHeaderBB.getShort() & 0xFFFF;
		earlyHeaderBB.getShort(); // characteristics

		// -- optional header --
		int optHeadPoint = earlyHeaderBB.position();
		if (optHeadSize < 0x78) {
			throw new IOException("Optional header size is under 0x78 (RESOURCE table info)");
		}

		if (earlyHeaderBB.getShort() != 0x010B) {
			throw new IOException("Unknown optional header type");
		}

		// Check that size of headers is what we thought
		if (getOptionalHeaderInt(0x3C) != expectedTex) {
			throw new IOException("Size of headers must be as expected due to linearization fun");
		}

		// Everything verified - load up the image sections
		source.clear(); // Remove this line and things will fail...
		source.position(optHeadPoint + optHeadSize);

		for (int i = 0; i < sectionCount; i++) {
			final Section s = new Section();
			s.read(source);
			sections.add(s);
		}
		verifyVirtualIntegrity();
	}

	private void verifyVirtualIntegrity() throws IOException {
		sections.sort(Comparator.comparingInt(section -> section.virtualAddrRelative));

		// Sets the minimum RVA we can use. This works due to the virtual sorting stuff.
		int rvaHeaderFloor = 0;
		for (Section s : sections) {
			if (uCompare(s.virtualAddrRelative) < uCompare(rvaHeaderFloor)) {
				throw new IOException("Section RVA Overlap, " + s);
			}
			rvaHeaderFloor = s.virtualAddrRelative + s.virtualSize;
		}
	}

	public void headerPosition(int i) {
		earlyHeaderBB.clear();
		earlyHeaderBB.position(i);
	}

	/* Structure Finders, modifies earlyHeaderBB.position */

	public int getNtHeaders() {
		headerPosition(0x3C);
		return earlyHeaderBB.getInt();
	}

	/* Value IO, modifies earlyHeaderBB.position */

	public int getOptionalHeaderInt(int ofs) {
		// 4: Signature
		// 0x14: IMAGE_FILE_HEADER
		headerPosition(getNtHeaders() + 4 + 0x14 + ofs);
		return earlyHeaderBB.getInt();
	}

	public ByteBuffer setupRVAPoint(int rva) {
		for (Section s : sections) {
			if (uCompare(rva) >= uCompare(s.virtualAddrRelative)) {
				int rel = rva - s.virtualAddrRelative;
				if (uCompare(rel) < Math.max(uCompare(s.rawData.length), uCompare(s.virtualSize))) {
					ByteBuffer bb = ByteBuffer.wrap(s.rawData);
					bb.order(ByteOrder.LITTLE_ENDIAN);
					bb.position(rel);
					return bb;
				}
			}
		}
		return null;
	}

	/* High-Level Tools */

	// Get the current index of the resource section, or -1 if none/unknown.

	public int getResourcesIndex() {
		int idx = 0;
		int resourcesRVA = getOptionalHeaderInt(0x70);
		for (Section s : sections) {
			if (s.virtualAddrRelative == resourcesRVA)
				return idx;
			idx++;
		}
		return -1;
	}

	// Gets the current index of a given named section, or -1 if none/unknown.

	public int getSectionIndexByTag(String tag) {
		int idx = 0;
		for (Section s : sections) {
			if (s.decodeTag().equals(tag)) {
				return idx;
			}
			idx++;
		}
		return -1;
	}

	public Section getSectionAt(int rdataSecId) {
		return sections.get(rdataSecId);
	}

	public int getSectionCount() {
		return sections.size();
	}

	/**
	 * A section, loaded into memory. Yes, this means temporary higher memory use,
	 * but it's a 2MB EXE at max. and frankly this code has to be as readable as
	 * possible.
	 */
	public static class Section {
		private final static byte[] blankRawData = new byte[0];
		// "Linearization" : File Address == RVA
		// We try to preserve linearization due to Doukutsu Assembler & co.
		// This is forced on for 3 critical sections,
		// and if we detect linearization on anything else,
		// we try to keep it that way for consistency.
		public boolean metaLinearize;

		// These are the fields we support (read: we don't enforce these to be 0):
		// Tag (in Windows-1252)
		public final byte[] tag = new byte[8];
		public int virtualSize, virtualAddrRelative;

		// The "Raw Data" - Set position before use.
		public byte[] rawData = blankRawData;
		public int characteristics = 0xE0000040;

		public Section() {
		}

		public void read(ByteBuffer bb) throws IOException {
			// NOTE: This occurs on the actual file in order to extract all the yummy data.
			bb.get(tag);
			virtualSize = bb.getInt();
			virtualAddrRelative = bb.getInt();
			rawData = new byte[bb.getInt()];

			int rawDataPointer = bb.getInt();
			metaLinearize = rawDataPointer == virtualAddrRelative;

			final int saved = bb.position();
			bb.clear();
			bb.position(rawDataPointer);
			bb.get(rawData);

			bb.clear();
			bb.position(saved);

			bb.getInt();
			bb.getInt();

			if (bb.getShort() != 0) {
				throw new IOException("Relocations not allowed");
			}

			if (bb.getShort() != 0) {
				throw new IOException("Line numbers not allowed");
			}

			characteristics = bb.getInt();
		}

		public String decodeTag() {
			int maxL = 0;
			for (int i = 0; i < tag.length; i++) {
				if (tag[i] != 0) {
					maxL = i + 1;
				}
			}
			return new String(tag, 0, maxL, Charset.forName("Windows-1252"));
		}

		public String toString() {
			return String.format("%s : RVA %s VS %s : RDS %s : CH %s",
					decodeTag(),
					Integer.toHexString(virtualAddrRelative),
					Integer.toHexString(virtualSize),
					Integer.toHexString(rawData.length),
					Integer.toHexString(characteristics));
		}

		// -- Copied from the old ExeSec, these routines appear reliable so let's not
		// break anything --

		private void shiftDirTable(ByteBuffer data, int amt, int pointer) {
			// get the # of rsrc subdirs indexed by name
			int nEntry = data.getShort(pointer + 12);
			// get the # of rsrc subdirs indexed by id
			nEntry += data.getShort(pointer + 14);

			// read and shift entries
			int pos = pointer + 16;
			for (int i = 0; i < nEntry; i++) {
				rsrcShift(data, amt, pos + i * 8);
			}
		}

		private void rsrcShift(ByteBuffer data, int amt, int pointer) {
			int rva = data.getInt(pointer + 4);
			if ((rva & 0x80000000) != 0) { // if hi bit 1 points to another directory table
				shiftDirTable(data, amt, rva & 0x7FFFFFFF);
			} else {
				int oldVal = data.getInt(rva);
				data.putInt(rva, oldVal + amt);
			}
		}
	}

	public static int uCompare(int a) {
		// 0xFFFFFFFF (-1) becomes 0x7FFFFFFF (highest number)
		// 0x00000000 (0) becomes 0x80000000 (lowest number)
		return a ^ 0x80000000;
	}
}
