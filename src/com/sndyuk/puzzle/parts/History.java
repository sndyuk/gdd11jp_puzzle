package com.sndyuk.puzzle.parts;
import java.util.Iterator;


public abstract class History {
    public final int depth;
    public final String dagCode;
    public final Command cmd;
    private History prev;

    public History(String dagCode, Command cmd, History prev, int depth) {
        this.dagCode = dagCode;
        this.cmd = cmd;
        this.prev = prev;
        this.depth = depth;
    }

    public History prev() {
        return prev;
    }

    @Override
    public String toString() {
        return "prev:" + prev.cmd + ", curr:" + cmd;
    }

    private Iterator<Command> createCommandIterator() {
        return new Iterator<Command>() {
            
            private boolean first = true;
            private History curr = prev();
            
            @Override
            public boolean hasNext() {
                return curr != null && curr.cmd != null;
            }

            @Override
            public Command next() {
                Command ret;
                if (first){
                    ret  = cmd;
                    first = false;
                } else {
                    ret = curr.cmd;
                    curr = curr.prev();
                }
                return ret;
            }
            @Override public void remove() {throw new UnsupportedOperationException();}
        };
    }
    
    public static final class Utils {
        private Utils() {}
        
        public static Iterator<Command> createCommandIterator(History his) {
            return his.createCommandIterator();
        }
    }
}
