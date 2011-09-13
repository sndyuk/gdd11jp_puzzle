package com.sndyuk.puzzle.digdag;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sndyuk.puzzle.parts.Board;
import com.sndyuk.puzzle.parts.Code;
import com.sndyuk.puzzle.parts.Dag;
import com.sndyuk.puzzle.parts.DagCode;
import com.sndyuk.puzzle.parts.History;
import com.sndyuk.puzzle.parts.Panel;
import com.sndyuk.puzzle.util.db.HistoryBase;
import com.sndyuk.puzzle.util.db.HistoryCollection;
import com.sndyuk.puzzle.util.db.MemBase;

public abstract class DigDag implements Iterable<List<History>>, Iterator<List<History>>, AutoCloseable {

	public static final int TRESHOLD = 77777;
	
    private static int suffixNo = 0;

    public final HistoryCollection coll;
    public final String collectionId;

    protected final int size;
    protected final Dag dag;
    protected final HistoryBase db;
    protected final Board board;

    public DigDag(Board start) {
        this.dag = new Dag(start);
        this.board = start;
        this.size = start.panels.length * start.panels[0].length;
        this.collectionId = HistoryBase.DBNAME + (++suffixNo);
        this.db = new MemBase(TRESHOLD / 13);
        this.coll = db.getCollection(collectionId);
    }

    @Override
    public final boolean hasNext() {
        return getGoal() == null;
    }

    @Override
    public final List<History> next() {
        List<History> his = dig();
        return his;
    }

    protected abstract List<History> dig();

    public abstract History getGoal();

    protected History getHistory(DagCode dagCode) {
        return coll.find(dagCode);
    }

    protected void addHistories(List<History> histories) {
        coll.insertAll(histories);
    }

    protected void addHistory(History history) {
        coll.insert(history, 0);
    }

    protected void changeHistory(History history) {
        coll.removeInsert(history);
    }

    protected void removeHistory(History history) {
        coll.remove(history);
    }

    protected void removeAllHistory() {
        coll.removeAll();
    }


    protected Panel selectNextTarget(Code prev, boolean asc) {
        if (prev == null) {
            prev = board.cmdablePanel.code;
        }
        Code currCode = asc ? prev.next() : prev.previous();
        Panel[][] panels = board.panels;
        for (int y = 0; y < panels.length; y++) {
            for (int x = 0; x < panels[0].length; x++) {
                Panel p = panels[y][x];
                if (p.code == currCode) {
                    if (Board.Utils.getDistance(board, p, p.getTY(), p.getTX()) != 0) {
                        return p;
                    } else {
                        if (currCode == board.cmdablePanel.code) {
                            // finish
                            return null;
                        }
                        return selectNextTarget(currCode, asc);
                    }
                }
            }
        }
        return selectNextTarget(currCode, asc); // found block'='
    }
    
    protected int checkLoopR(History start) {
        History curr = start.prev();

        for (int i = start.depth - 1; i >= 0; i--) {
            if (start.dagCode.equals(curr.dagCode)) {
                return i;
            }
            curr = curr.prev();
        }
        return -1;
    }

    protected void removeAllHistory(History his, int backCnt) {
        while ((his = his.prev()) != null) {
            removeHistory(his);
        }
    }

    @Override
    public Iterator<List<History>> iterator() {
        return this;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    // LRUDLRUDLRUD is loop
    protected int checkLoop(History his) {
        if (his.depth < 8) {
            return -1;
        }
        History start = his;
        List<History> tmp = new ArrayList<>();

        History tmpHis = his;
        for (int i = his.depth - 1; i >= 0; i--) {
            if (tmp.size() > 30) { // no idea it's about
                break;
            }
            if (tmp.size() >= 4 && tmpHis.cmd == start.cmd) {
                if (tmp.size() * 2 <= his.depth) {
                    // History = DDDLDDD->RUDL->RUDL->RUDL
                    // tmp = L RUDL same() false
                    // tmp = DL RUDL same() false
                    // tmp = UDL RUDL same() false
                    // tmp = RUDL RUDL same() true
                    if (same(tmp, his, i - (tmp.size()) + 1)) {
                        return his.depth - (tmp.size() * 2);
                    }
                }
            }
            tmp.add(tmpHis);
            tmpHis = tmpHis.prev();
        }
        return -1;
    }

    private boolean same(List<History> a, History his, int startPos) {
        if (startPos < 0) {
            return false;
        }
        // System.out.println("same()");
        //
        //
        // for (int i = 0; i < a.size(); i++) {
        // System.out.print(a.get(i).cmd);
        // }
        // System.out.println();
        //
        History hisB = his;
        for (int i = 0, l = his.depth - startPos - a.size(); i < l; i++) {
            hisB = hisB.prev();
        }

        History hisA = null;
        for (int i = 0; i < a.size(); i++) {
            hisA = a.get(i);
            // System.out.print(hisB.cmd);
            if (hisA.cmd != hisB.cmd) {
                return false;
            }
            hisB = hisB.prev();
        }
        return true;
    }

    public static final class Utils {
        private Utils() {
        }
    }
}
