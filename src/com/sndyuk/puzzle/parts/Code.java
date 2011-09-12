package com.sndyuk.puzzle.parts;

public final class Code implements Comparable<Code> {

    protected final char hash;
	protected final char c;
	protected final int index;
	protected final CodeFactory cf;

	protected Code(char c, int index, CodeFactory cf) {
		this.hash = CodeFactory.getHash(index, c);
		this.c = c;
		this.index = index;
		this.cf = cf;
	}

	       public Code previous() {
	                if (index == 0) {
	                        return cf.end();
	                } else {
	                        return cf.getCode(cf.charMap[index - 1]);
	                }
	        }

	        public Code next() {
	                if (index == cf.charMap.length - 1) {
	                        return cf.getCode(cf.charMap[0]);
	                } else {
	                        return cf.getCode(cf.charMap[index + 1]);
	                }
	        }
	        
	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}

	@Override
	public int hashCode() {
		return index;
	}

	@Override
	public String toString() {
		return index + ":" + c;
	}

	@Override
	public int compareTo(Code o) {
		return this.index - o.index;
	}
	
	public boolean isZero() {
		return c == CodeFactory.ZERO;
	}

	public boolean isBlock() {
		return c == CodeFactory.BLOCK;
	}
	
	public Code indexOf(int index) {
		return cf.getCode(cf.charMap[index]);
	}

	public int index() {
		return index;
	}
	
    public Code end() {
        return cf.end();
    }
    public String getCharMapString() {
    	return new String(cf.charMap);
    }
}
