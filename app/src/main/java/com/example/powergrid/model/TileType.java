package com.example.powergrid.model;

public enum TileType {
    EMPTY     (0,                                     false),
    STRAIGHT  (Sides.N | Sides.S,                     true),
    CORNER    (Sides.N | Sides.E,                     true),
    T_JUNCTION(Sides.N | Sides.E | Sides.W,           true),
    CROSS     (Sides.N | Sides.E | Sides.S | Sides.W, false),
    SOURCE    (Sides.N | Sides.E | Sides.S | Sides.W, false),
    BUILDING  (Sides.N,                               true);

    public final int baseMask;
    private final boolean rotatable;

    TileType(int baseMask, boolean rotatable) {
        this.baseMask = baseMask;
        this.rotatable = rotatable;
    }

    /** False for 4-fold-symmetric tiles and EMPTY, where a quarter turn is a no-op. */
    public boolean isRotatable() { return rotatable; }
    public boolean isSource()    { return this == SOURCE; }
    public boolean isBuilding()  { return this == BUILDING; }
}