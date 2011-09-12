package com.sndyuk.puzzle.digdag;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import com.sndyuk.puzzle.digdag.bfs.DigDagBfs;
import com.sndyuk.puzzle.parts.Command;
import com.sndyuk.puzzle.parts.History;

public class DigDagBFSTask extends RecursiveTask<String> {

    private static final long serialVersionUID = 1L;

    private final DigDagBfs digdagA;
    private final DigDagBfs digdagB;

    public DigDagBFSTask(DigDagBfs digdagA, DigDagBfs digdagB) {
        this.digdagA = digdagA;
        this.digdagB = digdagB;
    }

    @Override
    public String compute() {

    	int depthA = 0;
    	int depthB = 0;
    	
        for (;;) {

            DigDagInnerTask taskA = null;
            if (depthA <= depthB) {
                if (digdagA.hasNext()) {
                    taskA = new DigDagInnerTask(digdagA);
                    taskA.fork();
                }            	
            }
            DigDagInnerTask taskB = null;
            if (depthB <= depthA) {
                if (digdagB.hasNext()) {
                    taskB = new DigDagInnerTask(digdagB);
                    taskB.fork();
                }
            }

            List<History> hisListA = null;
            List<History> hisListB = null;

            if (taskA != null) {
                hisListA = taskA.join();
            }
            if (taskB != null) {
                hisListB = taskB.join();
            }

            // ------ reach goal
            if (digdagA.getGoal() != null) {
                System.err.println("goal! A");
                return getResultA(digdagA.getGoal());
                
            } else if (digdagB.getGoal() != null) {
                System.err.println("goal! B");
                return getResultB(digdagB.getGoal());
            }
            
            // ------ A meets B
            if (hisListA != null) {
                for (History his : hisListA) {
                    History matchHis = digdagB.getHistory(his.dagCode);
                    if (matchHis != null) {
                        return getResult(his, matchHis);
                    }
                }
            }
            // ------ B meets A
            if (hisListB != null) {
                for (History his : hisListB) {
                    History matchHis = digdagA.getHistory(his.dagCode);
                    if (matchHis != null) {
                        return getResult(matchHis, his);
                    }
                }
            }
            
            // ------- no way >_<
            if (hisListA == null && hisListB == null) {
                return null;
            }
            
            if (digdagA.currHis != null) {
            	depthA = digdagA.currHis.depth;
            } else {
            	depthA = Integer.MAX_VALUE;
            }
            if (digdagB.currHis != null) {
            	depthB = digdagB.currHis.depth;
            } else {
            	depthB = Integer.MAX_VALUE;
            }
        }
    }

    private String getResult(History hisA, History hisB) {
        return getResultA(hisA) + getResultB(hisB);
    }

    private String getResultA(History his) {
        StringBuilder cmdSb = new StringBuilder();
        for (Iterator<Command> cmdIterator = History.Utils.createCommandIterator(his); cmdIterator
                .hasNext();) {
            cmdSb.append(cmdIterator.next());
        }
        return cmdSb.reverse().toString();
    }

    private String getResultB(History his) {
        StringBuilder cmdSb = new StringBuilder();
        for (Iterator<Command> cmdIterator = History.Utils.createCommandIterator(his); cmdIterator
                .hasNext();) {
            cmdSb.append(Command.getOpposite(cmdIterator.next()));
        }
        return cmdSb.toString();
    }

    private static class DigDagInnerTask extends RecursiveTask<List<History>> {

        private static final long serialVersionUID = 1L;

        private final DigDag digdag;

        DigDagInnerTask(DigDag digdag) {
            this.digdag = digdag;
        }

        @Override
        protected List<History> compute() {
            return digdag.next();
        }
    }
}
