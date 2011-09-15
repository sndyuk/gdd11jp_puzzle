package com.sndyuk.puzzle.util.db;

import java.util.List;

import com.sndyuk.puzzle.parts.DagCode;
import com.sndyuk.puzzle.parts.History;

public class MemBase extends HistoryBase {

    private final int capacity;
    
    public MemBase() {
        this.capacity = 0xFFFFFF;
    }

    @Override
    public HistoryCollection getCollection(String name) {
        return new MemCollection(name);
    }

    class MemCollection extends HistoryCollection {

        private HistoryMap sByDagCodeMap;

        protected MemCollection(String name) {
            super(name);
            sByDagCodeMap = new HistoryMap(capacity);
        }

        @Override
        public void insert(History his, long index) {
            sByDagCodeMap.put(his.dagCode, his);
        }

        @Override
        public void close() {
            sByDagCodeMap = null;
        }

        @Override
        public History findAndRemoveByIndex(long index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public History find(DagCode dagCode) {
            return sByDagCodeMap.get(dagCode);
        }

        @Override
        public void insertAll(List<History> histories) {
            for (History his : histories) {
                insert(his, 0L);
            }
        }

        @Override
        public void removeInsert(History history) {
            remove(history.dagCode);
            insert(history, 0);
        }

        private void remove(DagCode dagCode) {
            sByDagCodeMap.remove(dagCode);
            return;
        }

        @Override
        public void remove(History history) {
            remove(history.dagCode);
        }

        @Override
        public void removeAll() {
            sByDagCodeMap = null;
            sByDagCodeMap = new HistoryMap(capacity);
            return;
        }
    }
}
