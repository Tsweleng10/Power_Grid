package com.example.powergrid.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Level {

    /** Immutable authored description of one tile. */
    public static final class TileSpec {
        public final int row, col, rotation;
        public final TileType type;
        public final boolean locked;

        public TileSpec(int row, int col, TileType type, int rotation, boolean locked) {
            this.row = row;
            this.col = col;
            this.type = type;
            this.rotation = rotation;
            this.locked = locked;
        }

        public Tile toTile() {
            return new Tile(row, col, type, rotation, locked);
        }
    }

    private final int id;
    private final String name;
    private final int rows, cols;
    private final int parMoves;          // 0 == unspecified
    private final List<TileSpec> tiles;

    public Level(int id, String name, int rows, int cols, int parMoves, List<TileSpec> tiles) {
        this.id = id;
        this.name = name == null ? "" : name;
        this.rows = rows;
        this.cols = cols;
        this.parMoves = parMoves;
        this.tiles = Collections.unmodifiableList(new ArrayList<>(tiles));
    }

    /** Fresh board with fresh tiles; omitted cells default to EMPTY. */
    public Board buildBoard() {
        Board board = new Board(rows, cols);
        for (TileSpec spec : tiles) board.setTile(spec.toTile());
        return board;
    }

    public int getId()               { return id; }
    public String getName()          { return name; }
    public int getRows()             { return rows; }
    public int getCols()             { return cols; }
    public int getParMoves()         { return parMoves; }
    public List<TileSpec> getTiles() { return tiles; }
}