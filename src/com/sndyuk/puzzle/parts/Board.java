package com.sndyuk.puzzle.parts;
import java.util.Arrays;


public final class Board {

    public final Panel[][] panels;
    public final Panel cmdablePanel;
    
    private Board(Code[][] board) {
        Panel tmp = null;
        panels = new Panel[board.length][];
        for (int y = 0; y < board.length; y++) {
            panels[y] = new Panel[board[y].length];
            for (int x = 0; x < board[0].length; x++) {
                panels[y][x] = new Panel(board[y][x], x, y, board[0].length, board.length);
                if (panels[y][x].isCommandable()) {
                    tmp = panels[y][x];
                }
            }
        }
        cmdablePanel = tmp;
    }

    public Panel getUp(Panel panel) {
        return get(panel.x, panel.y - 1);
    }

    public Panel getRight(Panel panel) {
        return get(panel.x + 1, panel.y);
    }

    public Panel getDown(Panel panel) {
        return get(panel.x, panel.y + 1);
    }

    public Panel getLeft(Panel panel) {
        return get(panel.x - 1, panel.y);
    }

    private Panel get(int x, int y) {
        if (x < 0 || x > panels[0].length - 1) {
            return null;
        }
        if (y < 0 || y > panels.length - 1) {
            return null;
        }
        return panels[y][x];
    }
    
    public String createUniqueId() {

        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < panels.length; y++) {
            for (int x = 0; x < panels[y].length; x++) {
                sb.append(panels[y][x].code.hash);
            }
        }
        return sb.toString();
    }
    
    public String toString() {

        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < panels.length; y++) {
        	sb.append("\n");
            for (int x = 0; x < panels[y].length; x++) {
                sb.append(panels[y][x].code);
            }
        }
        sb.append("\n");
        
        return sb.toString();
    }

    public static final class Utils {
        private Utils() {
        }

        public static int getAllDistance(Board board) {
            Panel[][] panels = board.panels;
            int distance = 0;

            for (int y = 0; y < panels.length; y++) {
                for (int x = 0; x < panels[0].length; x++) {
                    Panel p = panels[y][x];
                    distance += getDistance(board, p, p.ty, p.tx);
                }
            }
            return distance;
        }

        public static int getDistance(Board board, Panel p, int y, int x) {
            int xd = p.x - x;
            int yd = p.y - y;
            if (xd == 0 && yd == 0) {
                return 0;
            }
            int xFrom = 0, xTo = 0, yFrom = 0, yTo = 0;
            if (p.x > x) {
                xFrom = x;
                xTo= p.x;
            } else {
                xFrom = p.x;
                xTo= x;
            }
            if (p.y > y) {
                yFrom = y;
                yTo= p.y;
            } else {
                yFrom = p.y;
                yTo= y;
            }
            return Math.abs(p.x - x) + Math.abs(p.y - y) + getBlockCnt(board, xFrom, xTo, yFrom, yTo);
        }
        
        public static int getBlockCnt(Board board, int xFrom, int xTo, int yFrom, int yTo) {
            int blockCnt = 0;
            Panel[][] panels = board.panels;
            for (int y = yFrom; y < yTo; y++) {
                for (int x = xFrom; x < xTo; x++) {
                    Panel p = panels[y][x];
                    if (p.code.isBlock()) {
                        blockCnt++;
                    }
                }
            }
            return blockCnt;
        }
        
        public static Board createBoard(int w, int h, char[] bs, CodeFactory cf) {

            Code[] codes = new Code[bs.length];
            for (int i = 0; i < bs.length; i++) {
                codes[i] = cf.getCode(bs[i]);
            }
            return makeBoard(w, h, codes);
        }

        private static Board makeBoard(int w, int h, Code[] bs) {
            Code[][] board = new Code[h][w];

            int bsi = 0;
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    board[y][x] = bs[bsi++];
                }
            }

            
            return new Board(board);
        }

        public static Code[] toFinalCodeArr(Board board) {
            
            Panel[][] panels = board.panels;
            Code[] copy = new Code[panels.length * panels[0].length];
            for (int y = 0, index = 0; y < panels.length; y++) {
                for (int x = 0; x < panels[0].length; x++) {
                    Code c = panels[y][x].code;
                    if (c.isBlock()) {
                        // set stub
                        c = c.indexOf(index);
                    }
                    copy[index++] = c;
                }
            }
            
            Arrays.sort(copy);

            // Change stub to block
            for (int y = 0, index = 0; y < panels.length; y++) {
                for (int x = 0; x < panels[0].length; x++) {
                    Code c = panels[y][x].code;
                    if (c.isBlock()) {
                        copy[index] = c;
                    }
                    index++;
                }
            }
            
            return copy;
        }
        
        public static char[] toCharArr(Code[] codes) {
            char[] copy = new char[codes.length];
            for (int i = 0; i < codes.length; i++) {
                copy[i] = codes[i].c;
            }
            return copy;
        }
        
        public static Board createFinalForm(Board board) {

            Panel[][] panels = board.panels;
            Code[] copy = toFinalCodeArr(board);
            return makeBoard(panels[0].length, panels.length, copy);
        }

		public static String toString(Board board, String dagCode) {
			int x = board.panels[0].length;
			StringBuilder sb = new StringBuilder(dagCode.length());
			int i = 0;
			for (char c : dagCode.toCharArray()) {
				sb.append(c);
				i++;
				if (i % x == 0) {
					sb.append('\n');
				}
			}
			return sb.toString();
		}
        
//        public static long getMaxDirection(Board board) {
//            long max = fact(board.panels.length * board.panels[0].length) / 2;
//            if (max < 0) {
//                return Long.MAX_VALUE;
//            }
//            return max;
//        }
//        
//        private static long fact(long n) {
//            if (n == 0)
//                return 1;
//            return n * fact(n - 1);
//        }
    }
}
