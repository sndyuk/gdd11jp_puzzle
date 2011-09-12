package com.sndyuk.puzzle.util.db;



public abstract class HistoryBase {

    public static final String DBNAME = "gddpuzzle";

	public abstract HistoryCollection getCollection(String name);
}
