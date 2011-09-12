//package com.sndyuk.puzzle.util;
//
//import java.util.Collection;
//import java.util.Iterator;
//import java.util.Queue;
//
//import com.sndyuk.puzzle.digdag.DigDag;
//import com.sndyuk.puzzle.parts.History;
//import com.sndyuk.puzzle.util.db.HistoryCollection;
//import com.sndyuk.puzzle.util.db.RDB;
//
//public class StoredQueue implements Queue<History> {
//
//    private RDB db;
//    private HistoryCollection coll;
//
//    private long storeIndexFirst = 0;
//    private long storeIndexLast = 0;
//    
//    
//    public StoredQueue(DigDag digdag, int memCapacity) {
//        if (memCapacity <= 0) {
//            throw new AssertionError();
//        }
//        this.db = new RDB(digdag.coll, -1);
//        this.coll = db.getCollection(digdag.collectionId + "que");
//    }
//
//	public void close() {
//		coll.close();
//	}
//	
//    @Override
//    public boolean offer(History e) {
//        if (storeIndexLast == Long.MAX_VALUE) {
//            throw new AssertionError();
//        }
//        return storeHistory(e);
//    }
//
//    @Override
//    public History poll() {
//        return loadAndRemoveHistory();
//    }
//
//    private History loadAndRemoveHistory() {
//        return coll.findAndRemoveByIndex(storeIndexFirst++);
//    }
//
//    private boolean storeHistory(History e) {
//    	coll.insert(e, storeIndexLast++);
//        return true;
//    }
//
//    @Override
//    public boolean isEmpty() {
//        return storeIndexLast == storeIndexFirst;
//    }
//
//    @Override
//    public int size() {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public boolean contains(Object o) {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public Iterator<History> iterator() {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public Object[] toArray() {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public <T> T[] toArray(T[] a) {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public boolean remove(Object o) {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public boolean containsAll(Collection<?> c) {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public boolean addAll(Collection<? extends History> c) {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public boolean removeAll(Collection<?> c) {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public boolean retainAll(Collection<?> c) {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public void clear() {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public History remove() {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public History element() {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public History peek() {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public boolean add(History e) {
//        throw new UnsupportedOperationException();
//    }
//}
