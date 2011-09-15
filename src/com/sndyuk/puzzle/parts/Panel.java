package com.sndyuk.puzzle.parts;

public final class Panel {
    public final Code code;
    
    protected int x;
    protected int y;
    protected final int tx;
    protected final int ty;
    protected boolean fixedBefore = false;
    protected boolean fix = false;

    Panel(Code code, int x, int y, int w, int h) {
        this.code = code;
        this.x = x;
        this.y = y;

        if (code.isZero()) {
            ty = h - 1;
            tx = w - 1;
        } else if (code.isBlock()) {
            ty = y;
            tx = x;
        } else {
            ty = (int) Math.ceil(code.index / w);
            tx = code.index % w;
        }
        if (ty == y && tx == x) {
            fix = true;
        }
    }

    protected boolean isCommandable() {
        return code.isZero();
    }

    public boolean isChangeable() {
        return !code.isBlock();
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public int getTY() {
        return ty;
    }

    public int getTX() {
        return tx;
    }
    public boolean isFix() {
        return fix;
    }
    public boolean isFixedBeforeMove() {
        return fixedBefore;
    }
    @Override
    public String toString() {
        return x + " " + y + " [" + code.toString() + "]";
    }
}
