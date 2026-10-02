package com.example.powergrid.engine;

import com.example.powergrid.model.Board;
import com.example.powergrid.model.Sides;
import com.example.powergrid.model.Tile;
import java.util.ArrayDeque;
import java.util.Deque;

public final class PowerFlow {

    private PowerFlow() {}

    /** Recomputes {@code powered} on every tile by BFS from the board's source. */
    public static void compute(Board board) {
        Tile source = board.findSource();
        if (source == null) {
            board.clearPower();
            return;
        }
        compute(board, source);
    }

    public static void compute(Board board, Tile source) {
        board.clearPower();
        source.setPowered(true);

        Deque<Tile> queue = new ArrayDeque<>();
        queue.add(source);

        while (!queue.isEmpty()) {
            Tile cur = queue.poll();
            int open = cur.openSides();

            for (int d = 0; d < 4; d++) {
                if ((open & Sides.BIT[d]) == 0) continue;

                int nr = cur.getRow() + Sides.DR[d];
                int nc = cur.getCol() + Sides.DC[d];
                Tile next = board.getTile(nr, nc);

                if (next == null || next.isPowered()) continue;
                // Both tiles must face each other.
                if ((next.openSides() & Sides.OPPOSITE[d]) == 0) continue;

                next.setPowered(true);
                queue.add(next);
            }
        }
    }
}