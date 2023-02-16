package com.leo.cse.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class FlagsDataSet<T> {
    private final Map<Integer, T> flags = new HashMap<>(); // flagId -> flag
    private final Map<Integer, Integer> indices = new HashMap<>(); // flagId -> index in items
    private final Vector<T> items = new Vector<>();

    public int size() {
        return items.size();
    }

    public T get(int index) {
        return items.get(index);
    }

    public void clear() {
        flags.clear();
        indices.clear();
        items.clear();
    }

    public void add(T flag, int flagId) {
        final int index = items.size();
        items.add(flag);
        flags.put(flagId, flag);
        indices.put(flagId, index);
    }

    public Vector<T> getItems() {
        return items;
    }

    public boolean containsFlag(int flagId) {
        return flags.containsKey(flagId);
    }

    public T getFlagById(int flagId) {
        return flags.get(flagId);
    }

    public int indexOf(int flagId) {
        return indices.getOrDefault(flagId, -1);
    }

    public void set(int index, T newFlag, int flagId) {
        items.set(index, newFlag);
        flags.put(flagId, newFlag);
    }
}
