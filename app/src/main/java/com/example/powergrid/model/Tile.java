package com.example.powergrid.model;

public class Tile {
    private final int row, col;
    private final TileType type;
    private int rotation;            // 0..3 quarter-turns clockwise from base
    private boolean powered;
    private boolean locked;

    public Tile(int row, int col, TileType type, int rotation, boolean locked) {
        this.row = row;
        this.col = col;
        this.type = type;
        this.rotation = Math.floorMod(rotation, 4);
        this.locked = locked;
    }

    public int getRow()        { return row; }
    public int getCol()        { return col; }
    public TileType getType()  { return type; }
    public int getRotation()   { return rotation; }
    public boolean isPowered() { return powered; }
    public boolean isLocked()  { return locked; }

    public void setRotation(int r)    { this.rotation = Math.floorMod(r, 4); }
    public void setPowered(boolean p) { this.powered = p; }
    public void setLocked(boolean l)  { this.locked = l; }

    /** Open sides after applying this tile's current rotation. */
    public int openSides() {
        return Sides.rotate(type.baseMask, rotation);
    }

    /** True if the player can turn this tile. */
    public boolean isRotatable() {
        return !locked && type.isRotatable();
    }
}