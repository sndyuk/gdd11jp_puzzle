package com.sndyuk.puzzle.util;

import com.sndyuk.puzzle.parts.Command;
import com.sndyuk.puzzle.parts.DagCode;
import com.sndyuk.puzzle.parts.History;
import com.sndyuk.puzzle.util.db.HistoryCollection;

public class StoredHistory extends History {

    private final DagCode prevDagCode;
    private History prev;
    private HistoryCollection coll;
    
    public StoredHistory(HistoryCollection coll, DagCode dagCode, Command cmd, DagCode prevDagCode, byte depth) {
        super(dagCode, cmd, null, depth);
        this.prevDagCode = prevDagCode;
        this.coll = coll;
    }
    
    @Override
    public History prev() {
        if (prev != null || prevDagCode == null) {
            return prev;
        }
        return prev = coll.find(prevDagCode);
    }
}
