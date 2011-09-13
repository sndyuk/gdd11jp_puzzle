package com.sndyuk.puzzle.util.db;

import java.util.List;

import com.sndyuk.puzzle.parts.DagCode;
import com.sndyuk.puzzle.parts.History;

public abstract class HistoryCollection {

	protected static final String STOREKEY_DAGCODE = "dc";
	protected static final String STOREKEY_CMD = "cmd";
	protected static final String STOREKEY_DEPTH = "dp";
	protected static final String STOREKEY_PREV_DAGCODE = "pvdc";
	protected static final String STOREKEY_SORT = "queno";
	
	protected HistoryCollection(String name) {
	}
	
	public abstract void insert(History his, long index);
	public abstract History findAndRemoveByIndex(long index);
	public abstract void close();
	public abstract History find(DagCode dagCode);
	public abstract void insertAll(List<History> histories);
	public abstract void removeInsert(History history);
	public abstract void remove(History history);
	public abstract void removeAll();
}
