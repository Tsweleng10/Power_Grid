package com.example.powergrid.model;

/** Direction bitmasks and grid-navigation helpers shared by tiles and the engine. */
public final class Sides {
    public static final int N = 1, E = 2, S = 4, W = 8;

    /** All four arrays are indexed by direction 0..3 == N, E, S, W. */
    public static final int[] BIT      = { N, E, S, W };
    public static final int[] DR       = { -1, 0, 1, 0 };
    public static final int[] DC       = {  0, 1, 0, -1 };
    public static final int[] OPPOSITE = { S, W, N, E };

    private Sides() {}

    /** Rotate a 4-bit side mask clockwise by {@code quarterTurns} (negatives wrap). */
    public static int rotate(int mask, int quarterTurns) {
        int r = Math.floorMod(quarterTurns, 4);
        if (r == 0) return mask & 0xF;
        return ((mask << r) | (mask >> (4 - r))) & 0xF;
    }
}