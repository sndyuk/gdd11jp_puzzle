//package com.sndyuk.puzzle.util.db;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.HashMap;
//import java.util.List;
//
//import com.sndyuk.puzzle.parts.Command;
//import com.sndyuk.puzzle.parts.History;
//import com.sndyuk.puzzle.util.StoredHistory;
//
//public class RDB extends HistoryBase {
//
//	static {
//		try {
//			Class.forName("com.mysql.jdbc.Driver");
//		} catch (ClassNotFoundException e) {
//			throw new RuntimeException(e);
//		}	
//	}
//
//	private final int memUse;
//	private HistoryCollection historyColl;
//	
//	public RDB(HistoryCollection coll, int memUse) {
//		this.memUse = memUse;
//		historyColl = coll;
//	}
//	
//	@Override
//	public HistoryCollection getCollection(String name) {
//		return new RDBCollection(name);
//	}
//	
//	class RDBCollection extends HistoryCollection {
//		
//		private HashMap<String, History> sByDagCodeMap;
//		private Connection conn;
//		private final String name;
//		private PreparedStatement insertStmt;
//		private PreparedStatement selectStmtByDagCode;
//		private PreparedStatement selectStmtByIndex;
//		private PreparedStatement removeStmtByIndex;
//		private PreparedStatement removeStmtByDagCode;
//		
//		protected RDBCollection(String name) {
//			super(name);
//			this.name = name;
//                        historyColl = historyColl == null ? this : historyColl;
//                        if (memUse > 0) {
//                            // 13230000
//                                sByDagCodeMap = new HashMap<>(100000);
//                        }
//		}
//
//		private void create() {
//			Statement stmt = null;
//			try {
//				stmt = conn.createStatement();
//				stmt.executeUpdate("create table " + name + "("
//						+ STOREKEY_DAGCODE + " VARCHAR(40) PRIMARY KEY,"
//						+ STOREKEY_CMD + "  VARCHAR(1),"
//						+ STOREKEY_DEPTH + " INT,"
//						+ STOREKEY_PREV_DAGCODE + " VARCHAR(40),"
//						+ STOREKEY_SORT + " BIGINT "
//						+ ") type=InnoDB");
//				
//				stmt.executeUpdate("CREATE INDEX "+ STOREKEY_SORT + " ON " + name + "(" + STOREKEY_SORT + ")");
//				
//			} catch (SQLException e) {
//				throw new RuntimeException(e);
//			} finally {
//				close(stmt);
//			}
//		}
//
//		@Override
//		public void insert(History his, long index) {
//			pre(1);
//			if (sByDagCodeMap != null) {
//				sByDagCodeMap.put(his.dagCode, his);
//				return;
//			}
//			try {
//				insertStmt.setString(1, his.dagCode);
//				insertStmt.setString(2, his.cmd != null ? his.cmd.toString() : null);
//				insertStmt.setInt(3, his.depth);
//				if (his.prev() != null) {
//					insertStmt.setString(4, his.prev().dagCode);
//	            } else {
//	            	insertStmt.setString(4, null);
//	            }
//				insertStmt.setLong(5, index);
//				
//				insertStmt.executeUpdate();
//			} catch (SQLException e) {
//				throw new RuntimeException(e);
//			}
//		}
//		
//		private void pre(int id) {
//			if (sByDagCodeMap != null) {
//			    return;
////				if (memUse > sByDagCodeMap.size()) {
////					return;
////				}
////				else {
////					List<History> lis = new ArrayList<>(sByDagCodeMap.values());
////					sByDagCodeMap = null;
////					insertAll(lis);
////				}
//			}
//			if (conn == null) {
//		                 try {
//	                                conn = DriverManager.getConnection("jdbc:mysql://localhost/digdag", "root", "root");
//	                                conn.setAutoCommit(true);
//	                                drop();
//	                                create();
//
//	                        } catch (SQLException e) {
//	                                throw new RuntimeException(e);
//	                        }
//			}
//			
//			try {
//				if (insertStmt == null || insertStmt.isClosed()) {
//					insertStmt = conn.prepareStatement("INSERT INTO " + name + " ("
//							+ STOREKEY_DAGCODE + ","
//							+ STOREKEY_CMD + ","
//							+ STOREKEY_DEPTH + ","
//							+ STOREKEY_PREV_DAGCODE + ","
//							+ STOREKEY_SORT
//							+ ") VALUES (?, ?, ?, ?, ?)");
//				}
////				else if (id != 1) {
////					close(insertStmt);
////				}
//				if (selectStmtByIndex == null || selectStmtByIndex.isClosed()) {
//					selectStmtByIndex = conn.prepareStatement("SELECT * FROM "
//							+ name
//							+ " WHERE "
//							+ STOREKEY_SORT
//							+ " = ?");					
//				}
////				else if (id != 2) {
////					close(selectStmtByIndex);
////				}
//				if (removeStmtByIndex == null || removeStmtByIndex.isClosed()) {
//					removeStmtByIndex = conn.prepareStatement("DELETE FROM "
//							+ name
//							+ " WHERE "
//							+ STOREKEY_SORT
//							+ " = ?");
//				} 
////				else if (id != 3) {
////					close(removeStmtByIndex);
////				}
//				if (selectStmtByDagCode == null || selectStmtByDagCode.isClosed()) {
//					selectStmtByDagCode = conn.prepareStatement("SELECT * FROM "
//							+ name
//							+ " WHERE "
//							+ STOREKEY_DAGCODE
//							+ " = ?");
//				}
////				else if (id != 4) {
////					close(selectStmtByDagCode);
////				}
//	                            if (removeStmtByDagCode == null || removeStmtByDagCode.isClosed()) {
//	                                removeStmtByDagCode = conn.prepareStatement("DELETE FROM "
//                                                        + name
//                                                        + " WHERE "
//                                                        + STOREKEY_DAGCODE
//                                                        + " = ?");
//                                } 
////	                            else if (id != 5) {
////                                  close(removeStmtByDagCode);
////                          }
//
//
//			} catch (SQLException e) {
//				throw new RuntimeException(e);
//			}
//		}
//		
//		@Override
//		public void close() {
//			try {
//				close(insertStmt);
//				close(selectStmtByDagCode);
//				close(selectStmtByIndex);
//				close(removeStmtByIndex);
//				close(removeStmtByDagCode);
//				drop();
//				conn.close();
//			} catch (SQLException e) {
//				throw new RuntimeException(e);
//			}
//		}
//
//		private void drop() {
//			Statement stmt = null;
//			try {
//				stmt = conn.createStatement();
//				stmt.executeUpdate("DROP TABLE " + name);
//			} catch (Exception e) {
//				// ignore
//			} finally {
//				close(stmt);
//			}
//		}
//		
//		@Override
//		public History findAndRemoveByIndex(long index) {
//			pre(2);
//			ResultSet rset = null;
//			try {
//				selectStmtByIndex.setLong(1, index);
//				rset = selectStmtByIndex.executeQuery();
//				while (rset.next()) {
//					History his = toHistory(rset);
//					remove(index);
//					return his;
//				}
//				return null;
//			} catch (SQLException e) {
//				throw new RuntimeException(e);
//			} finally {
//				close(rset);
//			}
//		}
//		
//		private void remove(long index) {
//			pre(3);
//			try {
//				removeStmtByIndex.setLong(1, index);
//				removeStmtByIndex.execute();
//			} catch (SQLException e) {
//				throw new RuntimeException(e);
//			}
//		}
//
//		private History toHistory(ResultSet rset) {
//			
//			try {
//				  String cmdStr = rset.getString(2);
//            Command cmd = cmdStr != null && cmdStr.length() == 1 ? Command.valueOf(cmdStr.charAt(0)) : null;
//            
//			History his = new StoredHistory(
//					historyColl,
//					rset.getString(1),
//					cmd,
//					rset.getString(4),
//					rset.getByte(3)
//					);
//			
//			return his;
//			
//			} catch (SQLException e) {
//				throw new RuntimeException(e);
//			}
//		}
//		
//		private void close(Statement o) {
//			try {
//				if (o != null) {
//					o.close();
//				}
//			} catch (Exception e) {
//				throw new RuntimeException(e);
//			}
//		}
//
//		private void close(ResultSet o) {
//			try {
//				if (o != null) {
//					o.close();
//				}
//			} catch (Exception e) {
//				throw new RuntimeException(e);
//			}
//		}
//
//		@Override
//		public History find(String dagCode) {
//			pre(4);
//			if (sByDagCodeMap != null) {
//				return sByDagCodeMap.get(dagCode);
//			}
//			ResultSet rset = null;
//			try {
//				selectStmtByDagCode.setString(1, dagCode);
//				rset = selectStmtByDagCode.executeQuery();
//				while (rset.next()) {
//					return toHistory(rset);
//				}
//				return null;
//			} catch (SQLException e) {
//				throw new RuntimeException(e);
//			} finally {
//				close(rset);
//			}
//		}
//
//		@Override
//		public void insertAll(List<History> histories) {
//			for (History his : histories) {
//				insert(his, 0L);
//			}
//		}
//	              
//                
//                @Override
//                public void removeInsert(History history) {
//                    remove(history.dagCode);
//                    insert(history, 0);
//                }
//                
//                private void remove(String dagCode) {
//                    pre(5);
//                    if (sByDagCodeMap != null) {
//                        sByDagCodeMap.remove(dagCode);
//                        return;
//                    }
//                    try {
//                            removeStmtByDagCode.setString(1, dagCode);
//                            removeStmtByDagCode.execute();
//                    } catch (SQLException e) {
//                            throw new RuntimeException(e);
//                    }      
//                }
//
//                @Override
//                public void remove(History history) {
//                    remove(history.dagCode);
//                }
//
//                @Override
//                public void removeAll() {
//                    pre(6);
//                    if (sByDagCodeMap != null) {
//                        sByDagCodeMap.clear();
//                        return;
//                    }
//                    drop();
//                    create();
//                }
//    }
//}
