package com.sndyuk.puzzle.parts;

import java.util.BitSet;

public final class DagCode {

    private final long[] words;
    private final int hash;

    DagCode(Panel[][] panels) {
        int size = panels.length * panels[0].length;
        byte[] barr = new byte[size > 0x00ff ? size * 4 : size];
        
        int i = 0;
        for (int y = 0; y < panels.length; y++) {
            for (int x = 0; x < panels[y].length; x++) {
                char h = panels[y][x].code.hash;
                if (h > 0x00FF) {
                    barr[i++] = (byte)(h >>> 24);
                    barr[i++] = (byte)(h >>> 16);
                    barr[i++] = (byte)(h >>> 8);
                    barr[i++] = (byte)(h);
                } else {
                    barr[i++] = (byte) h;
                }
            }
        }
        BitSet bs = BitSet.valueOf(barr);
        this.words = bs.toLongArray();
        this.hash = hash(bs.hashCode());
    }
    
    /**
     * Applies a supplemental hash function to a given hashCode, which
     * defends against poor quality hash functions.  This is critical
     * because HashMap uses power-of-two length hash tables, that
     * otherwise encounter collisions for hashCodes that do not differ
     * in lower bits. Note: Null keys always map to hash 0, thus index 0.
     */
    static int hash(int h) {
        // This function ensures that hashCodes that differ only by
        // constant multiples at each bit position have a bounded
        // number of collisions (approximately 8 at default load factor).
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
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
}
