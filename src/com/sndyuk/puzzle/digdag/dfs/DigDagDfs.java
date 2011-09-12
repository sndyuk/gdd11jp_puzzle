package com.sndyuk.puzzle.digdag.dfs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sndyuk.puzzle.digdag.DigDag;
import com.sndyuk.puzzle.digdag.TiredException;
import com.sndyuk.puzzle.parts.Board;
import com.sndyuk.puzzle.parts.Command;
import com.sndyuk.puzzle.parts.History;
import com.sndyuk.puzzle.parts.Panel;

public class DigDagDfs extends DigDag {

	private final String goalId;
	private final int maxDepth;
	private final Board board;
	private final Panel cmdPanel;

	private final int f;
	private HistoryDfs currHis;
	private final int[] minDistanceByDepth;
	private History goal;
	private long digCnt = 0L;
	private Panel currPanel;

	private final int maxHisSize;
	private int hisSize;

	public DigDagDfs(Board start) {
		super(start);
		this.board = start;
		this.cmdPanel = start.cmdablePanel;
		this.f = (start.panels.length) * (start.panels[0].length);
		maxDepth = Board.Utils.getAllDistance(start) + f;
		minDistanceByDepth = new int[maxDepth];
		for (int i = 0; i < minDistanceByDepth.length; i++) {
			minDistanceByDepth[i] = add(0, maxDepth - i);
		}
		goalId = Board.Utils.createFinalForm(start).createUniqueId();
		currHis = new HistoryDfs(dag.createUniqueId(), null, null, 0);
		currPanel = selectNextTarget(cmdPanel.code.end(), true);
		maxHisSize = 10000000;
	}

	private int add(int res, int i) {
		if (i <= 0) {
			return res;
		}
		return add(res + i, --i);
	}

	@Override
	public History getGoal() {
		return goal;
	}

	@Override
	protected List<History> dig() {
		digCnt++;
		if (digCnt % 1000000 == 0) {
			System.out.println(digCnt + ":" + currHis.depth);
			 StringBuilder cmdSb = new StringBuilder();
			 for (Iterator<Command> cmdIterator =
			 History.Utils.createCommandIterator(currHis);
			 cmdIterator.hasNext();) {
			 cmdSb.append(cmdIterator.next());
			 }
			 cmdSb = cmdSb.reverse();
			 System.out.println(cmdSb.toString());
		}
		if (hisSize > maxHisSize) {
			throw new TiredException();
		}
		currPanel = selectNextTarget(cmdPanel.code.end(), true);

		Command cmd = calc(currPanel);

		if (cmd == null) {
			// back
			rollback(currHis.depth - 1);
			return toList(currHis);
		}

		forward(cmd);
		if (goalId.equals(currHis.dagCode)) {
			System.out.println(digCnt + ": finish");
			goal = currHis; // found
			return toList(currHis);
		}

		int loopIndex = -1;
		History his = getHistory(currHis.dagCode);
		if (his != null && currHis.depth > his.depth) {
			// select shorter way
			changeHistory(currHis);
			currHis = (HistoryDfs) his;
			return toList(currHis);
		} else if (currHis.depth >= maxDepth) {
			rollback(1);
		} else if (getAllDistance() > minDistanceByDepth[currHis.depth]) {
			rollback(1);
		}
		// else if ((loopIndex = checkLoopA()) != -1) {
		// rollback(loopIndex);
		// }
		else if ((loopIndex = checkLoop(currHis)) != -1) {
			rollback(loopIndex);
		} else {
			hisSize++;
			addHistory(currHis);
		}
		return toList(currHis);
	}

	private List<History> toList(History his) {
		List<History> lis = new ArrayList<>(1);
		lis.add(his);
		return lis;
	}

	private int getAllDistance() {
		int d = Board.Utils.getAllDistance(board) + f;
		Panel cmdPanel = board.cmdablePanel;
		return d
				- (Board.Utils.getDistance(board, cmdPanel, cmdPanel.getTY(),
						cmdPanel.getTX()));
	}

	private Command calc(Panel targetPanel) {
		List<Command> orderedCmds = new ArrayList<>(4);
		Command[] tmpCmds = dag.getAvailableCommands(currHis);
		if (tmpCmds == null) {
			return null;
		}
		for (Command cmd : tmpCmds) {
			if (currHis.isUsed(cmd) != null) {
				continue;
			}
			orderedCmds.add(cmd);
		}
		if (orderedCmds.size() == 0) {
			if (currHis.depth == 0) {
				// root node
				return Command.getNext(currHis.next().cmd);
			}
			return null;
		}
		return selectShort(targetPanel, orderedCmds);
	}

	private void forward(Command cmd) {
		dag.forward(cmd);
		HistoryDfs his = new HistoryDfs(dag.createUniqueId(), cmd, currHis,
				currHis.depth + 1);
		currHis.use(his);
		currHis = his;
	}

	private void rollback(int depth) {
		for (int i = currHis.depth - 1; i >= 0; i--) {
			dag.back(currHis.cmd);
			removeHistory(currHis);
			hisSize--;
			currHis = (HistoryDfs) currHis.prev();
			if (i == depth) {
				break;
			}
		}
	}

	private Command selectShort(Panel targetPanel, List<Command> orderedCmds) {

		Command select = null;
		int prevHomeDistance = Integer.MAX_VALUE;
		int prevCmdDistance = Integer.MAX_VALUE;
		int prevYDistance = Integer.MAX_VALUE;
		for (int i = 0; i < orderedCmds.size(); i++) {
			Command cmd = orderedCmds.get(i);
			Panel p = dag.forward(cmd);
			if (p.code.compareTo(targetPanel.code) < 0 && p.isFixedBeforeMove()) {
				dag.back(cmd);
				if (orderedCmds.size() == 0) {
					return cmd;
				}
				continue;
			}
			int cmdDistance = Board.Utils.getDistance(board, targetPanel,
					cmdPanel.getY(), cmdPanel.getX());
			int homeDistance = Board.Utils.getDistance(board,
					board.panels[targetPanel.getTY()][targetPanel.getTX()],
					cmdPanel.getY(), cmdPanel.getX());
			int yDistance = Math.abs(targetPanel.getY() - cmdPanel.getY());
			if (cmdDistance < prevCmdDistance
					|| (cmdDistance == prevCmdDistance && homeDistance < prevHomeDistance)
					|| (cmdDistance == prevCmdDistance
							&& homeDistance == prevHomeDistance && yDistance > prevYDistance)) {
				select = cmd;
				prevCmdDistance = cmdDistance;
				prevHomeDistance = homeDistance;
				prevYDistance = yDistance;
			}
			dag.back(cmd);
		}
		return select;
	}

	@Override
	public void close() {
	}
}
