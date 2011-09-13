package com.sndyuk.puzzle.util.db;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

import com.sndyuk.puzzle.parts.History;

public class MemBase extends HistoryBase {

    private final int initialCapacity;
    
    public MemBase(int initialCapacity) {
        this.initialCapacity = initialCapacity;
    }

    @Override
    public HistoryCollection getCollection(String name) {
        return new MemCollection(name);
    }

    class MemCollection extends HistoryCollection {

        private HashMap<BitSet, History> sByDagCodeMap;

        protected MemCollection(String name) {
            super(name);
            sByDagCodeMap = new HashMap<>(initialCapacity, 0.9f);
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
        public History find(BitSet dagCode) {
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

        private void remove(BitSet dagCode) {
            sByDagCodeMap.remove(dagCode);
            return;
        }

        @Override
        public void remove(History history) {
            remove(history.dagCode);
        }

        @Override
        public void removeAll() {
            sByDagCodeMap.clear();
            return;
        }
    }
}
