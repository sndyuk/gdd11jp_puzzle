package com.sndyuk.puzzle.parts;

import java.util.BitSet;

public class DagCode {

    private long[] words;
    private final int hash;

    DagCode(Panel[][] panels) {
        byte[] barr = new byte[panels.length * panels[0].length];
        int i = 0;
        for (int y = 0; y < panels.length; y++) {
            for (int x = 0; x < panels[y].length; x++) {
                char h = panels[y][x].code.hash;
                if (h > 0x00FF) {
                    throw new AssertionError();
                }
                barr[i++] = (byte) panels[y][x].code.hash;
            }
        }
        BitSet bs = BitSet.valueOf(barr);
        this.words = bs.toLongArray();
        this.hash = bs.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        DagCode dc = (DagCode) obj;
        if (words.length != dc.words.length)
            return false;

        // Check words in use by both BitSets
        for (int i = 0; i < words.length; i++)
            if (words[i] != dc.words[i])
                return false;

        return true;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    // @Override
    // public int compareTo(DagCode o) {
    // return o.hash - hash;
    // }

}
