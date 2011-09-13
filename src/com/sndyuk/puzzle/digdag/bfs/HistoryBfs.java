package com.sndyuk.puzzle.digdag.bfs;

import com.sndyuk.puzzle.parts.Command;
import com.sndyuk.puzzle.parts.DagCode;
import com.sndyuk.puzzle.parts.History;

public final class HistoryBfs extends History implements Comparable<HistoryBfs> {

	protected int rank;
	protected boolean asc;

	public HistoryBfs(DagCode dagCode, Command cmd, HistoryBfs prev, int depth) {
		super(dagCode, cmd, prev, depth);
		if (prev != null) {
			asc = prev.asc;
		}
	}

	@Override
	public String toString() {
		return "prev:" + prev().cmd + ", curr:" + cmd;
	}

	@Override
	public int compareTo(HistoryBfs o) {
		return o.rank - this.rank;
	}
}
