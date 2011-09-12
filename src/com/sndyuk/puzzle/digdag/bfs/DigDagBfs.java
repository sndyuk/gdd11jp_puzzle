package com.sndyuk.puzzle.digdag.bfs;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import com.sndyuk.puzzle.digdag.DigDag;
import com.sndyuk.puzzle.digdag.TiredException;
import com.sndyuk.puzzle.parts.Board;
import com.sndyuk.puzzle.parts.Command;
import com.sndyuk.puzzle.parts.History;
import com.sndyuk.puzzle.parts.Panel;

public final class DigDagBfs extends DigDag {

	// ---
	private long digCnt = 0L;
	public HistoryBfs currHis;
	private HistoryBfs topRank;
	private History goal;
	private int cacheAllUntil;
	private int allCacheCntDown;

	// ---
	private final String goalId;
	private final int maxDepth;
	private final int maxCacheSizeByDepth;
	private final int aveWH2;
	// ---
	private LinkedList<HistoryBfs> queue;
	private Queue<HistoryBfs> tmpQueue;
	private List<HistoryBfs> removeCache;
	
	public DigDagBfs(Board start, boolean asc) {
		super(start);
		
		this.aveWH2 = ((board.panels.length + board.panels[0].length) / 2) * 2;
		if (aveWH2 <= Board.Utils.getBlockCnt(start, 0, board.panels[0].length, 0, board.panels.length)) {
			this.maxCacheSizeByDepth = TRESHOLD;	
		} else {
			this.maxCacheSizeByDepth = TRESHOLD / 10;
		}
		this.goalId = Board.Utils.createFinalForm(start).createUniqueId();
		// this.maxDepth = getAllDistance() + (size * 6); //take it to
		this.maxDepth = 180;
//		int cSize = size
//				- (Board.Utils.getBlockCnt(board, 0, board.panels[0].length, 0,
//						board.panels.length) * 2);
//		this.allCacheCntDown = cSize <= 20 ? 18 : cSize <= 24 ? 20 : 14;
		this.allCacheCntDown = 13;
		this.cacheAllUntil = 5;

		this.queue = new LinkedList<>();
		this.tmpQueue = new PriorityQueue<>();
		this.removeCache = new ArrayList<>(TRESHOLD);
		this.currHis = new HistoryBfs(dag.createUniqueId(), null, null, 0);
		currHis.asc = asc;
		this.topRank = currHis;
		this.topRank.rank = Integer.MAX_VALUE;

		this.queue.offer(currHis);
	}

	@Override
	public History getGoal() {
		return goal;
	}

	@Override
	protected List<History> dig() {
		if (currHis.depth > maxDepth) {
			throw new TiredException();
		}

		digCnt++;
		dag.rollback(currHis);

		if (queue.isEmpty()) {

			// go to next depth
			if (tmpQueue.size() > 0) {
				if (allCacheCntDown >= 0) {
					allCacheCntDown--;
				} else {
					if (--cacheAllUntil < 0) {
						for (HistoryBfs his : removeCache) {
							removeHistory(his);
						}
						cacheAllUntil = 13;
					}
				}

//				if (allCacheCntDown == 0) {
//					queue = new LinkedList<>();
//					for (int i = 0, x = tmpQueue.size() - 1000; i < x; i++) {
//						removeHistory(tmpQueue.poll());
//					}
//					for (int i = 0, x = tmpQueue.size(); i < x; i++) {
//						queue.add(tmpQueue.poll());
//					}
//				} else {
					queue = new LinkedList<>(tmpQueue);
//				}
				HistoryBfs depthTop = queue.getLast();
				topRank = depthTop;

				System.out.println(digCnt + " : " + currHis.depth + " :rank "
						+ topRank.rank + " :" + this.hashCode());
				System.out.println("direction asc: " + topRank.asc);
				System.out.println(board.cmdablePanel.code.getCharMapString());
				System.out.println(Board.Utils.toString(board, topRank.dagCode));
				
				removeCache.addAll(queue);
				if (allCacheCntDown <= 0) {
					tmpQueue = new PriorityQueue<>(maxCacheSizeByDepth + 1);
				} else {
					tmpQueue = new LinkedList<>();
				}
			} else {
				throw new TiredException();
			}
		}

		// set current
		currHis = queue.poll();
		dag.build(currHis);

		Command[] cmds = dag.getAvailableCommands(currHis);
		List<History> hisList = new ArrayList<>(2);
		next(cmds, hisList);
		return hisList;
	}

	private void next(Command[] cmds, List<History> hisList) {

		// remove current cmd in cmds.
		Command cmd = null;
		for (int i = 0; i < cmds.length; i++) {
			if (cmds[i] != null) {
				cmd = cmds[i];
				cmds[i] = null;
				break;
			}
		}
		if (cmd == null) {
			return;
		}

		Panel targetPanel = selectNextTarget(); // null -> 1 -> 2 -> 3 .... 0
		Panel p = forward(cmd); // and set new currHis instance.
		if (goalId.equals(currHis.dagCode)) {
			goal = currHis; // found
		}

		int rank = calcRank(cmd, cmds, targetPanel, p);
		currHis.rank = rank;
		
		if (allCacheCntDown <= 0) {

			if (getHistory(currHis.dagCode) == null
//					|| checkLoopR(currHis) == -1
//					|| checkLoop(currHis) == -1
				) {

				if (isWrongWay(currHis, aveWH2) <= 0) {
					addHistory(currHis);
					tmpQueue.add((HistoryBfs) currHis);
					if (currHis.depth >= cacheAllUntil
							&& tmpQueue.size() > maxCacheSizeByDepth) {
						removeHistory(tmpQueue.poll());
					}
				}
			}
		} else if (getHistory(currHis.dagCode) == null) {

			HistoryBfs his = (HistoryBfs) currHis;
			tmpQueue.add(his);
			addHistory(currHis);
		}

		hisList.add(currHis);

		rollback(currHis.depth - 1); // and set new currHis instance.

		next(cmds, hisList);
	}
	
	private int isWrongWay(HistoryBfs his, int backCnt) {
		HistoryBfs back = his;
		for (int i = 0; i < backCnt; i++) {
			if (back == null) {
				return -1;
			}
			back = (HistoryBfs)back.prev();
		}
		if (back == null) {
			return -1;
		}
		return his.rank - back.rank - aveWH2;
	}
	
	private int getAllDistance() {

		int d = Board.Utils.getAllDistance(board);
		Panel cmdPanel = board.cmdablePanel;
		return d
				- (Board.Utils.getDistance(board, cmdPanel, cmdPanel.getTY(),
						cmdPanel.getTX()));
	}

	private Panel selectNextTarget() {

		int tarningPoint = board.cmdablePanel.code.index();
		Panel p = selectNextTarget(null, currHis.asc);
		if (!currHis.asc && (p.code.index() <= tarningPoint)) {
			currHis.asc = true;
			p = selectNextTarget(null, currHis.asc);
		}
		return p;
	}

	
	private int calcRank(Command cmd, Command[] cmds, Panel targetPanel, Panel p) {

		// close:0 < far
		int rank = size * 4;
		// base point ----- check distance
		rank += getAllDistance() * 4; // 4 -> 5
		rank -= cntFixedPanelTo(targetPanel, currHis.asc) * 2;

		int cpd = getCmdPanelDistance(targetPanel);

		if (cpd <= 1) {

			int chd = getHomeDistance(targetPanel);

			if (chd <= 0) {

				int cyd = getCmdYDistance(targetPanel);

				if (cyd == 0) {

					rank -= (getCmdXDistance(targetPanel) * 4); // far is better

				} else {

					rank -= cyd * 4; // far is better
				}
			} else {

				rank += chd * 3; // close is better

			}
		} else {

			rank += cpd * 2; // close is better

		}

		// penalty ----- if release already fixed panel
		if (p.code.compareTo(targetPanel.code) != 0) {

			if (!(currHis.asc ^ p.code.compareTo(targetPanel.code) < 0)
					&& p.isFixedBeforeMove()) {

				rank += 5;

			} else if (p.isFixedBeforeMove()) {

				rank += 3;
			}
		}

		// penalty ------ if move far
		if (isMoveFar(p)) {
			rank += 2;
		}

		// bonus ----- if repaired unfixed panel
		if (targetPanel.isFix()) {

			rank -= 3;
		}

		// bonus ----- ordered code
		rank -= cntAlignedNums(targetPanel, currHis.asc) * 2;

		return rank;
	}

	// arg: target panel must always point to max of fixed panels
	private int cntFixedPanelTo(Panel targetPanel, boolean asc) {

		return asc ? targetPanel.code.index() : size - targetPanel.code.index();
	}

	private int getCmdPanelDistance(Panel targetPanel) {

		return Board.Utils.getDistance(board, targetPanel,
				board.cmdablePanel.getY(), board.cmdablePanel.getX());
	}

	private int getHomeDistance(Panel targetPanel) {

		return Board.Utils.getDistance(board,
				board.panels[targetPanel.getTY()][targetPanel.getTX()],
				board.cmdablePanel.getY(), board.cmdablePanel.getX());
	}

	private int getCmdYDistance(Panel targetPanel) {

		return board.panels.length
				- Math.abs(targetPanel.getY() - board.cmdablePanel.getY());
	}

	private int getCmdXDistance(Panel targetPanel) {

		return board.panels.length
				- Math.abs(targetPanel.getX() - board.cmdablePanel.getX());
	}

	private boolean isMoveFar(Panel p) {

		int a = Board.Utils.getDistance(board, p, p.getTY(), p.getTX());
		int b = Board.Utils.getDistance(board, board.cmdablePanel, p.getTY(),
				p.getTX());
		return a > b;
	}

	private int cntAlignedNums(Panel targetPanel, boolean asc) {
		// FIXME change score if 
		Panel[][] panels = board.panels;

		int cnt = 0;
		
		// Horizontal
		int currCdIndex = panels[0][0].code.index();
		for (int y = 0; y < panels.length; y++) {
			for (int x = 0; x < panels[0].length; x++) {
				Panel p = panels[y][x];
				if (++currCdIndex == p.code.index()) {
					cnt++;
				} else {
					currCdIndex = p.code.index();
				}
			}
		}

		// Vertical
		currCdIndex = panels[0][0].code.index();
		for (int x = 0; x < panels[0].length; x++) {
			for (int y = 0; y < panels.length; y++) {
				Panel p = panels[y][x];
				if (++currCdIndex == p.code.index()) {
					cnt++;
				} else {
					currCdIndex = p.code.index();
				}
			}
		}
		
		return cnt;
	}

	private Panel forward(Command cmd) {

		Panel p = dag.forward(cmd);
		currHis = new HistoryBfs(dag.createUniqueId(), cmd, currHis,
				currHis.depth + 1);
		return p;
	}

	private Panel rollback(int depth) {

		Panel p = null;
		for (int i = currHis.depth - 1; i >= 0; i--) {
			p = dag.back(currHis.cmd);
			currHis = (HistoryBfs) currHis.prev();
			if (i == depth) {
				break;
			}
		}
		return p;
	}

	@Override
	public void close() {
	}
}
