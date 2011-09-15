package com.sndyuk.puzzle.util.db;

import com.sndyuk.puzzle.parts.DagCode;
import com.sndyuk.puzzle.parts.History;

final class HistoryMap {

    private int size;

    private final Object[][] table;
    private final int capacity;

    private static final int I_KEY = 0;
    private static final int I_VALUE = 1;
    private static final int I_NEXT = 2;

    HistoryMap(int capacity) {
        this.table = new Object[capacity][];
        this.capacity = capacity;
    }

    /**
     * Returns index for hash code h.
     */
    private static int indexFor(int h, int length) {
        return h & (length - 1);
    }


    public History get(Object key) {
        int index = indexFor(key.hashCode(), capacity);
        for (Object[] real = table[index]; real != null && real.length > I_NEXT
                && (real = (Object[]) real[I_NEXT]) != null;) {
            if (real[I_KEY].equals(key)) {
                return (History) real[I_VALUE];
            }
        }
        return null;
    }

    public History put(DagCode key, History value) {
        int index = indexFor(key.hashCode(), capacity);
        for (Object[] real = table[index]; real != null && real.length > I_NEXT
                && (real = (Object[]) real[I_NEXT]) != null;) {
            if (real[I_KEY].equals(key)) {
                History oldValue = (History) real[I_VALUE];
                real[I_VALUE] = value;
                return oldValue;
            }
        }

        addEntry(index, key, value);
        return null;
    }

    private void addEntry(int index, DagCode key, History value) {
        Object[] real = table[index];
        table[index] = new Object[] { key, value, real };
        size++;
    }

    public History remove(DagCode key) {
        int index = indexFor(key.hashCode(), capacity);

        Object[] curr = null;
        Object[] prev = null;
        for (Object[] real = table[index]; real != null && real.length > I_NEXT
                && (real = (Object[]) real[I_NEXT]) != null;) {
            if (real[I_KEY].equals(key)) {
                curr = real;
            }
            prev = real;
        }
        if (curr == null) {
            return null;
        }
        if (prev == null) {
            table[index] = null;
        } else {
            prev[I_NEXT] = null;
        }
        size--;
        Object o = curr[I_VALUE];
        return (History) o;
    }

    public int size() {
        return size;
    }
}
