package com.leo.cse.util;

import com.leo.cse.frontend.ui.ThemeData;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

public class Dialogs {
	public static <T> T showSelectionDialog(String title,
											T[] selections,
											T initialSelection,
											ListCellRenderer<T> listRender) {
		final JList<T> list = new JList<>(selections);
		if (listRender != null) {
			list.setBackground(ThemeData.getBackgroundColor());
			list.setCellRenderer(listRender);
		}

		final JScrollPane scrollPane = new JScrollPane();
		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.add(scrollPane);
		scrollPane.getViewport().add(list);

		list.setSelectedValue(initialSelection, true);

		final boolean[] isDoubleClickPerformed = new boolean[1];

		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 1) {
					isDoubleClickPerformed[0] = true;
					SwingUtilities.windowForComponent(list).dispose();
				}
			}
		});

		final int option = JOptionPane.showConfirmDialog(
				null,
				panel,
				title,
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE
		);

		if (isDoubleClickPerformed[0] || option == JOptionPane.OK_OPTION) {
			return list.getSelectedValue();
		}
		return null;
	}

	public static <T> T showSelectionDialog(String title, T[] selections, T initialSelection) {
		return showSelectionDialog(title, selections, initialSelection, null);
	}

	public static File openFileChooser(
			Component parent,
			String title,
			FileFilter[] filters,
			int currentFilter,
			File currentDirectory,
			boolean allowAllFilesFilter,
			boolean openOrSave) {
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		final boolean noFilters = (filters == null || filters.length == 0);
		fileChooser.setAcceptAllFileFilterUsed(allowAllFilesFilter || noFilters);

		if (filters != null) {
			for (FileFilter filter : filters) {
				fileChooser.addChoosableFileFilter(filter);
			}
			fileChooser.setFileFilter(filters[currentFilter]);
		}

		fileChooser.setDialogTitle(title);
		fileChooser.setCurrentDirectory(currentDirectory);

		final int state = openOrSave
				? fileChooser.showSaveDialog(parent)
				: fileChooser.showOpenDialog(parent);

		if (state == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		}
		return null;
	}

	public static File openFileChooser(
			Component parent,
			String title,
			FileFilter filter,
			File currentDirectory,
			boolean allowAllFilesFilter,
			boolean openOrSave) {
		final FileFilter[] filters = { filter };
		return openFileChooser(parent,
				title,
				filters,
				0,
				currentDirectory,
				allowAllFilesFilter, openOrSave);
	}
}
