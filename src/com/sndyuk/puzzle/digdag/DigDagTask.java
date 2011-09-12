package com.sndyuk.puzzle.digdag;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

import com.sndyuk.puzzle.digdag.bfs.DigDagBfs;
import com.sndyuk.puzzle.parts.Board;
import com.sndyuk.puzzle.parts.CodeFactory;

public class DigDagTask implements Callable<String> {

	private int w;
	private final int h;
	private final String puzzle;

	private final ForkJoinPool pool = new ForkJoinPool(2);

	public DigDagTask(int w, int h, String puzzle, ExecutorService service) {
		this.w = w;
		this.h = h;
		this.puzzle = puzzle;
	}

	@Override
	public String call() throws Exception {
		return solveByBFS(w, h, puzzle);
	}

	private String solveByBFS(int w, int h, String puzzle) {

		return solveByBFSDir2(w, h, puzzle, true); // threshold 2980(4800 up) - 300000(4990 up)
	}

	private String solveByBFSDir2(int w, int h, String puzzle, 
			boolean asc) {
		CodeFactory defaultCf = CodeFactory.getDefaultCodeFactory();
		CodeFactory reverseCf = new CodeFactory(puzzle.toCharArray());

		Board boardA = Board.Utils.createBoard(w, h, puzzle.toCharArray(),
				defaultCf);
		Board boardB = Board.Utils.createBoard(w, h,
				Board.Utils.toCharArr(Board.Utils.toFinalCodeArr(boardA)),
				reverseCf);

		try (DigDagBfs digdagA = new DigDagBfs(boardA, asc);
				DigDagBfs digdagB = new DigDagBfs(boardB, !asc)) {

			DigDagBFSTask task = new DigDagBFSTask(digdagA, digdagB);
			return pool.invoke(task);
		}
	}
}
