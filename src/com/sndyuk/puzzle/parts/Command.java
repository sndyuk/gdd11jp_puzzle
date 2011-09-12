package com.sndyuk.puzzle.parts;

public abstract class Command {

    public static final int LENGTH = 4;

    public final int id;

    private Command(int id) {
        this.id = id;
    }

    abstract Panel forward(Board board);

    abstract Panel back(Board board);

    abstract boolean isReturn(Command cmd);

    public static Command getNext(Command cmd) {
        return ARR[cmd.id >= LENGTH ? 0 : cmd.id];
    }

    protected void swap(Board board, Panel a, Panel b) {
        board.panels[a.y][a.x] = b;
        board.panels[b.y][b.x] = a;
        int ty = a.y;
        int tx = a.x;
        a.y = b.y;
        a.x = b.x;
        b.y = ty;
        b.x = tx;

        if (a.y == a.ty && a.x == a.tx) {
            a.fix = true;
        } else {
            if (a.fix) {
                a.fixedBefore = true;
            } else {
                a.fixedBefore = false;
            }
            a.fix = false;

        }
        if (b.y == b.ty && b.x == b.tx) {
            b.fix = true;
        } else {
            if (b.fix) {
                b.fixedBefore = true;
            } else {
                b.fixedBefore = false;
            }
            b.fix = false;
        }
    }

    static final Command U = new Command(0) {

        @Override
        Panel forward(Board board) {
            Panel p = board.getUp(board.cmdablePanel);
            swap(board, p, board.cmdablePanel);
            return p;
        }

        @Override
        Panel back(Board board) {
            Panel p = board.getDown(board.cmdablePanel);
            swap(board, p, board.cmdablePanel);
            return p;
        }

        @Override
        boolean isReturn(Command cmd) {
            return cmd == D;
        }

        @Override
        public String toString() {
            return "U";
        }
    };

    static final Command R = new Command(1) {
        @Override
        Panel forward(Board board) {
            Panel p = board.getRight(board.cmdablePanel);
            swap(board, p, board.cmdablePanel);
            return p;
        }

        @Override
        Panel back(Board board) {
            Panel p = board.getLeft(board.cmdablePanel);
            swap(board, p, board.cmdablePanel);
            return p;
        }

        @Override
        boolean isReturn(Command cmd) {
            return cmd == L;
        }

        @Override
        public String toString() {
            return "R";
        }
    };

    static final Command D = new Command(2) {

        @Override
        Panel forward(Board board) {
            Panel p = board.getDown(board.cmdablePanel);
            swap(board, p, board.cmdablePanel);
            return p;
        }

        @Override
        Panel back(Board board) {
            Panel p = board.getUp(board.cmdablePanel);
            swap(board, p, board.cmdablePanel);
            return p;
        }

        @Override
        boolean isReturn(Command cmd) {
            return cmd == U;
        }

        @Override
        public String toString() {
            return "D";
        }
    };

    static final Command L = new Command(3) {

        @Override
        Panel forward(Board board) {
            Panel p = board.getLeft(board.cmdablePanel);
            swap(board, p, board.cmdablePanel);
            return p;
        }

        @Override
        Panel back(Board board) {
            Panel p = board.getRight(board.cmdablePanel);
            swap(board, p, board.cmdablePanel);
            return p;
        }

        @Override
        boolean isReturn(Command cmd) {
            return cmd == R;
        }

        @Override
        public String toString() {
            return "L";
        }
    };

    private static final Command[] ARR = new Command[] { U, R, D, L };
    private static final char[] CARR = new char[] { 'U', 'R', 'D', 'L' };

    public static Command valueOf(char c) {
        for (int i = 0; i < CARR.length; i++) {
            if (CARR[i] == c) {
                return ARR[i];
            }
        }
        return null;
    }

    public static Command getOpposite(Command cmd) {
        return ARR[(cmd.id + 2) % 4];
    }
}
