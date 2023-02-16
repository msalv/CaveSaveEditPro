package com.leo.cse.backend.mci;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.leo.cse.backend.profile.ProfileFlags;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameInfo {
    public final String name;
    public final String author;
    public final String exeName;
    public final int armsImageYStart;
    public final int armsImageSize;
    public final int fps;
    public final int graphicsDensity;
    public final String profileClass;
    public final int saveEvent;
    public final int saveFlagID;
    public final Set<String> specials;
    public final String[] mapNames;
    public final String[] songNames;
    public final String[] equipNames;
    public final String[] weaponNames;
    public final String[] itemNames;
    public final String[] warpNames;
    public final Map<Integer, String> warpLocNames;
    public final Map<Integer, String> flagDescriptions;
    public final Map<Integer, Point> entityOffsets;
    public final Map<Integer, Rectangle> entityFrames;
    public final String[] challengeNames;

    public GameInfo(String name, String author, String exeName, int armsImageYStart, int armsImageSize, int fps, int graphicsDensity, String profileClass, int saveEvent, int saveFlagID, Set<String> specials, String[] mapNames, String[] songNames, String[] equipNames, String[] weaponNames, String[] itemNames, String[] warpNames, Map<Integer, String> warpLocNames, Map<Integer, String> flagDescriptions, Map<Integer, Point> entityOffsets, Map<Integer, Rectangle> entityFrames, String[] challengeNames) {
        this.name = name;
        this.author = author;
        this.exeName = exeName;
        this.armsImageYStart = armsImageYStart;
        this.armsImageSize = armsImageSize;
        this.fps = fps;
        this.graphicsDensity = graphicsDensity;
        this.profileClass = profileClass;
        this.saveEvent = saveEvent;
        this.saveFlagID = saveFlagID;
        this.specials = specials;
        this.mapNames = (mapNames != null) ? mapNames : new String[0];
        this.songNames = (songNames != null) ? songNames : new String[0];
        this.equipNames = (equipNames != null) ? equipNames : new String[0];
        this.weaponNames = (weaponNames != null) ? weaponNames : new String[0];
        this.itemNames = (itemNames != null) ? itemNames : new String[0];
        this.warpNames = (warpNames != null) ? warpNames : new String[0];
        this.warpLocNames = sortedMap(warpLocNames);
        this.flagDescriptions = sortedMap(flagDescriptions);
        this.entityOffsets = entityOffsets;
        this.entityFrames = entityFrames;
        this.challengeNames = challengeNames;
    }

    public static GameInfo fromJSON(JsonReader reader) throws IOException {
        final Gson gson = new Gson();
        final TypeToken<Map<Integer, String>> mapTypeToken = new TypeToken<Map<Integer, String>>(){};

        String name = null;
        String author = null;
        String exeName = null;
        int armsImageYStart = 0;
        int armsImageSize = 0;
        int fps = 50;
        int graphicsResolution = 1;
        String profileClass = null;
        int saveEvent = 16;
        int saveFlagID = ProfileFlags.SAVED;
        final Set<String> specials = new HashSet<>();
        String[] mapNames = null;
        String[] songNames = null;
        String[] equipNames = null;
        String[] weaponNames = null;
        String[] itemNames = null;
        String[] warpNames = null;
        String[] challengeNames = null;
        final Map<Integer, String> warpLocNames = new HashMap<>();
        final Map<Integer, String> flagDescriptions = new HashMap<>();
        final Map<Integer, Point> entityOffsets = new HashMap<>();
        final Map<Integer, Rectangle> entityFrames = new HashMap<>();

        reader.beginObject();

        while (reader.hasNext()) {
            String key = reader.nextName();
            switch (key) {
                case "name":
                    name = reader.nextString();
                    break;
                case "author":
                    author = reader.nextString();
                    break;
                case "exeName":
                    exeName = reader.nextString();
                    break;
                case "armsImageYStart":
                    armsImageYStart = reader.nextInt();
                    break;
                case "armsImageSize":
                    armsImageSize = reader.nextInt();
                    break;
                case "fps":
                    fps = reader.nextInt();
                    break;
                case "graphicsResolution":
                    graphicsResolution = reader.nextInt();
                    break;
                case "profileClass":
                    profileClass = reader.nextString();
                    break;
                case "saveEvent":
                    saveEvent = reader.nextInt();
                    break;
                case "specials":
                    reader.beginArray();
                    while (reader.hasNext()) {
                        specials.add(reader.nextString());
                    }
                    reader.endArray();
                    break;
                case "mapNames":
                    mapNames = gson.fromJson(reader, String[].class);
                    break;
                case "songNames":
                    songNames = gson.fromJson(reader, String[].class);
                    break;
                case "equipNames":
                    equipNames = gson.fromJson(reader, String[].class);
                    break;
                case "weaponNames":
                    weaponNames = gson.fromJson(reader, String[].class);
                    break;
                case "itemNames":
                    itemNames = gson.fromJson(reader, String[].class);
                    break;
                case "warpNames":
                    warpNames = gson.fromJson(reader, String[].class);
                    break;
                case "warpLocNames":
                    warpLocNames.putAll(gson.fromJson(reader, mapTypeToken));
                    break;
                case "challengeNames":
                    challengeNames = gson.fromJson(reader, String[].class);
                    break;
                case "flagDescriptions":
                    flagDescriptions.putAll(gson.fromJson(reader, mapTypeToken));
                    break;
                case "saveFlagID":
                    saveFlagID = reader.nextInt();
                    break;
                case "entityOffsets":
                    reader.beginObject();
                    while (reader.hasNext()) {
                        final String field = reader.nextName();
                        final Point offset = readPoint(reader);
                        entityOffsets.put(Integer.parseInt(field, 10), offset);
                    }
                    reader.endObject();
                    break;
                case "entityFrames":
                    reader.beginObject();
                    while (reader.hasNext()) {
                        final String field = reader.nextName();
                        final Rectangle frame = readRectangle(reader);
                        entityFrames.put(Integer.parseInt(field, 10), frame);
                    }
                    reader.endObject();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();

        return new GameInfo(
                name,
                author,
                exeName,
                armsImageYStart,
                armsImageSize,
                fps,
                graphicsResolution,
                profileClass,
                saveEvent,
                saveFlagID,
                specials,
                mapNames,
                songNames,
                equipNames,
                weaponNames,
                itemNames,
                warpNames,
                warpLocNames,
                flagDescriptions,
                entityOffsets,
                entityFrames,
                challengeNames);
    }

    private static Point readPoint(JsonReader reader) throws IOException {
        int x = 0;
        int y = 0;

        reader.beginObject();
        while (reader.hasNext()) {
            final String field = reader.nextName();
            if (field.equals("x")) {
                x = reader.nextInt();
            } else if (field.equals("y")) {
                y = reader.nextInt();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return new Point(x, y);
    }

    private static Rectangle readRectangle(JsonReader reader) throws IOException {
        int x = 0;
        int y = 0;
        int width = 0;
        int height = 0;

        reader.beginObject();
        while (reader.hasNext()) {
            final String field = reader.nextName();
            switch (field) {
                case "x":
                    x = reader.nextInt();
                    break;
                case "y":
                    y = reader.nextInt();
                    break;
                case "width":
                    width = reader.nextInt();
                    break;
                case "height":
                    height = reader.nextInt();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();

        return new Rectangle(x, y, width, height);
    }

    private static <T> Map<Integer, T> sortedMap(Map<Integer, T> map) {
         final List<Integer> keys = new ArrayList<>(map.keySet());
         Collections.sort(keys);

         final Map<Integer, T> sorted = new LinkedHashMap<>();
         for (Integer key : keys) {
            sorted.put(key, map.get(key));
         }
         return sorted;
    }
}
