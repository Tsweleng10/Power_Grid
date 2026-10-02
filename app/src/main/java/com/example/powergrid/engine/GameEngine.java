package com.example.powergrid.engine;

import com.example.powergrid.model.Board;
import com.example.powergrid.model.Level;
import com.example.powergrid.model.Tile;

public class GameEngine {

    private final Level level;
    private Board board;
    private int moves;
    private boolean won;

    public GameEngine(Level level) {
        this.level = level;
        restart();
    }

    /** Rebuilds the board from the level spec and resets the move counter. */
    public void restart() {
        board = level.buildBoard();
        moves = 0;
        PowerFlow.compute(board);
        won = board.allBuildingsPowered();
    }

    /**
     * Rotates the tile at (row, col) 90 degrees clockwise and re-runs power flow.
     *
     * @return true if the move was applied. False for out-of-bounds taps, locked
     *         tiles, tiles whose rotation is a no-op, and moves after the win.
     */
    public boolean rotate(int row, int col) {
        if (won) return false;

        Tile tile = board.getTile(row, col);
        if (tile == null || !tile.isRotatable()) return false;

        tile.setRotation(tile.getRotation() + 1);
        moves++;

        PowerFlow.compute(board);
        won = board.allBuildingsPowered();
        return true;
    }

    public Board getBoard() { return board; }
    public Level getLevel() { return level; }
    public int getMoves()   { return moves; }
    public boolean isWon()  { return won; }

    /** 0-3 stars from parMoves. Returns 0 if the level defines no par or is unsolved. */
    public int getStars() {
        int par = level.getParMoves();
        if (par <= 0 || !won) return 0;
        if (moves <= par) return 3;
        if (moves <= par * 2) return 2;
        return 1;
    }
}