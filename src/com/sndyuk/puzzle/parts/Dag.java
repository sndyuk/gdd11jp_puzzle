package com.sndyuk.puzzle.parts;
import java.util.ArrayList;
import java.util.List;

public final class Dag {

    private final Board board;
    
    public Dag(Board board) {
        this.board = board;
    }
    
    public Command[] getAvailableCommands(History his) {
        Command[] tmpCmds = getAvailableCommands(board);
        List<Command> availableCmds = new ArrayList<>(tmpCmds.length);
        for (Command cmd : tmpCmds) {
            if (his.cmd != null && his.cmd.isReturn(cmd)) {
                continue;
            }
            availableCmds.add(cmd);
        }
        if (availableCmds.size() <= 0) {
            return null;
        }
        return availableCmds.toArray(new Command[availableCmds.size() - 1]);
    }
    
    private static Command[] getAvailableCommands(Board board) {
        List<Command> cmds = new ArrayList<>();
        Panel panel = board.cmdablePanel;
        if (isMovable(board.getUp(panel))) {
            cmds.add(Command.U);
        }
        if (isMovable(board.getRight(panel))) {
            cmds.add(Command.R);
        }
        if (isMovable(board.getDown(panel))) {
            cmds.add(Command.D);
        }
        if (isMovable(board.getLeft(panel))) {
            cmds.add(Command.L);
        }
        return cmds.toArray(new Command[cmds.size() - 1]);
    }

    private static boolean isMovable(Panel panel) {
        return panel != null && panel.isChangeable();
    }

    public Panel forward(Command cmd) {
        return cmd.forward(board);
    }

    public Panel back(Command cmd) {
        return cmd.back(board);
    }
    
    public String createUniqueId() {
        return board.createUniqueId();
    }

    public void build(History his) {
        int i = his.depth;
        if (i <= 0) {
            return;
        }
        Command[] cmds = new Command[i];
        cmds[--i] = his.cmd;
        while (i > 0) {
            his = his.prev();
            cmds[--i] = his.cmd;
        }
        for (Command cmd : cmds) {
            forward(cmd);
        }
    }

    public void rollback(History his) {
        if (his.cmd == null) {
            return;
        }
        back(his.cmd);
        while ((his = his.prev()) != null && his.cmd != null) {
            back(his.cmd);
        }
    }
}
