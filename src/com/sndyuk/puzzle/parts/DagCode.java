package com.sndyuk.puzzle.parts;

import java.util.BitSet;

public class DagCode {

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
                barr[i++] = (byte)panels[y][x].code.hash;
            }
        }
        hash = BitSet.valueOf(barr).hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		return ((DagCode)obj).hash == hash;
	}
	
//	@Override
//	public int compareTo(DagCode o) {
//		return o.hash - hash;
//	}

}
