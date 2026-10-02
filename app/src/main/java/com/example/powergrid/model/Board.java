package com.example.powergrid.model;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private final int rows, cols;
    private final Tile[][] grid;

    public Board(int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Board must be at least 1x1, got " + rows + "x" + cols);
        }
        this.rows = rows;
        this.cols = cols;
        this.grid = new Tile[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = new Tile(r, c, TileType.EMPTY, 0, false);
            }
        }
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }

    public boolean inBounds(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    /** @return the tile, or {@code null} if out of bounds. */
    public Tile getTile(int r, int c) {
        return inBounds(r, c) ? grid[r][c] : null;
    }

    public void setTile(Tile tile) {
        if (!inBounds(tile.getRow(), tile.getCol())) {
            throw new IllegalArgumentException(
                    "Tile out of bounds at (" + tile.getRow() + "," + tile.getCol() + ")");
        }
        grid[tile.getRow()][tile.getCol()] = tile;
    }

    public void clearPower() {
        for (Tile[] row : grid) {
            for (Tile t : row) t.setPowered(false);
        }
    }

    /** @return the first SOURCE tile, or {@code null} if the board has none. */
    public Tile findSource() {
        for (Tile[] row : grid) {
            for (Tile t : row) {
                if (t.getType().isSource()) return t;
            }
        }
        return null;
    }

    public List<Tile> findBuildings() {
        List<Tile> out = new ArrayList<>();
        for (Tile[] row : grid) {
            for (Tile t : row) {
                if (t.getType().isBuilding()) out.add(t);
            }
        }
        return out;
    }

    /**
     * Win condition. Requires at least one building — an empty board is NOT a win,
     * so the engine never locks input on a malformed level.
     */
    public boolean allBuildingsPowered() {
        boolean found = false;
        for (Tile[] row : grid) {
            for (Tile t : row) {
                if (!t.getType().isBuilding()) continue;
                found = true;
                if (!t.isPowered()) return false;
            }
        }
        return found;
    }
}