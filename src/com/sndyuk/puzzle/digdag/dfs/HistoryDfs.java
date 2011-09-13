package com.sndyuk.puzzle.digdag.dfs;

import java.util.BitSet;

import com.sndyuk.puzzle.parts.Command;
import com.sndyuk.puzzle.parts.History;

class HistoryDfs extends History {

    public HistoryDfs(BitSet dagCode, Command cmd, History prev, int depth) {
        super(dagCode, cmd, prev, depth);
        // TODO Auto-generated constructor stub
    }
//    private HistoryDfs[] used;
//    private HistoryDfs next;
//
//    HistoryDfs(String dagCode, Command cmd, HistoryDfs prev, int depth) {
//        super(dagCode, cmd, prev, depth);
//        this.used = new HistoryDfs[Command.LENGTH];
//        if (prev != null) {
//            prev.next = this;
//        }
//    }
//
//    public void use(HistoryDfs his) {
//        this.used[his.cmd.id] = his;
//        this.next = his;
//    }
//
//    public HistoryDfs isUsed(Command cmd) {
//        return this.used[cmd.id];
//    }
//
//    public HistoryDfs next() {
//        return next;
//    }
//
//    @Override
//    public String toString() {
//        return "cmd:" + cmd;
//    }
}
