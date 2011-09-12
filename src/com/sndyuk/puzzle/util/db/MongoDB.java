//package com.sndyuk.puzzle.util.db;
//
//import java.net.UnknownHostException;
//import java.util.ArrayList;
//import java.util.List;
//
//import com.mongodb.BasicDBObject;
//import com.mongodb.DB;
//import com.mongodb.DBObject;
//import com.mongodb.Mongo;
//import com.mongodb.MongoException;
//import com.sndyuk.puzzle.parts.Command;
//import com.sndyuk.puzzle.parts.History;
//import com.sndyuk.puzzle.util.StoredHistory;
//
//public class MongoDB extends HistoryBase {
//
//	private static DB DB;
//    private static Mongo M;
//    
//    static {
//        try {
//            M = new Mongo();
//        } catch (UnknownHostException | MongoException e) {
//            throw new RuntimeException(e);
//        }
//    }
//    
//
//	@Override
//	public HistoryCollection getCollection(String name) {
//		return new MongoDBCollection(name);
//	}
//	
//    class MongoDBCollection extends HistoryCollection {
//
//		private com.mongodb.DBCollection coll;
//
//        protected MongoDBCollection(String name) {
//			super(name);
//			
//			M.dropDatabase(DBNAME);
//            DB = M.getDB(DBNAME);
//			coll = DB.getCollection(name);
//			coll.createIndex(new BasicDBObject(STOREKEY_DAGCODE, 1));
//		}
//
//		@Override
//		public void insert(History his, long index) {
//			throw new UnsupportedOperationException();
//		}
//
//		@Override
//		public History findAndRemoveByIndex(long index) {
//			throw new UnsupportedOperationException();
//		}
//
//		@Override
//		public History find(String dagCode) {
//			BasicDBObject query = new BasicDBObject();
//	        query.put(STOREKEY_DAGCODE, dagCode);
//	        DBObject dbObj = coll.findOne(query);
//	        if (dbObj == null) {
//	            return null;
//	        }
//	        return createHistory(dbObj);
//		}
//
//		@Override
//		public void insertAll(List<History> histories) {
//		    if (histories != null && histories.size() > 0) {
//	            List<DBObject> list = new ArrayList<>();
//
//	            for (History his : histories) {
//	                list.add(createDBObject(his));
//	            }
//	            coll.insert(list);
//	        }
//		}
//		
//		@Override
//		public void close() {
//		}
//
//	    private BasicDBObject createDBObject(History his) {
//	        BasicDBObject dbObj = new BasicDBObject();
//	        
//	        dbObj.put(STOREKEY_DAGCODE, his.dagCode);
//
////	        StringBuilder cmdSb = new StringBuilder();
////	        for (Iterator<Command> cmdIterator = History.Utils.createCommandIterator(his); cmdIterator.hasNext();) {
////	            cmdSb.append(cmdIterator.next());
////	        }
//	        dbObj.put(STOREKEY_CMD, his.cmd != null ? his.cmd.toString() : null);
//	        dbObj.put(STOREKEY_DEPTH, his.depth);
//	        if (his.prev() != null) {
//	            dbObj.put(STOREKEY_PREV_DAGCODE, his.prev().dagCode);
//	        } else {
//	            dbObj.put(STOREKEY_PREV_DAGCODE, null);
//	        }
//	        return dbObj;
//	    }
//
//	    private StoredHistory createHistory(DBObject dbObj) {
//	        String cmdStr = (String)dbObj.get(STOREKEY_CMD);
//	        Command cmd = cmdStr != null && cmdStr.length() == 1 ? Command.valueOf(cmdStr.charAt(0)) : null;
//	        StoredHistory his = new StoredHistory(
//	                this,
//	                (String)dbObj.get(STOREKEY_DAGCODE),
//	                cmd,
//	                (String)dbObj.get(STOREKEY_PREV_DAGCODE),
//	                ((Integer)dbObj.get(STOREKEY_DEPTH)).byteValue());
//	        return his;
//	    }
//
//        @Override
//        public void removeInsert(History history) {
//            throw new UnsupportedOperationException();
//        }
//
//        @Override
//        public void remove(History history) {
//            throw new UnsupportedOperationException();
//        }
//
//        @Override
//        public void removeAll() {
//            throw new UnsupportedOperationException();            
//        }
//    }
//}
