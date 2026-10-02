package com.example.powergrid.engine;

import com.example.powergrid.model.Board;
import com.example.powergrid.model.Level;
import com.example.powergrid.model.TileType;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class LevelLoader {

    /** Resolves a filename listed in the index to an open stream, or null if missing. */
    public interface StreamOpener {
        InputStream open(String name) throws IOException;
    }

    private final Gson gson = new Gson();

    /** Parse a single level. {@code sourceName} appears only in error messages. */
    public Level load(InputStream in, String sourceName) throws IOException, LevelFormatException {
        LevelDto dto;
        try (InputStream stream = in;
             Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            dto = gson.fromJson(reader, LevelDto.class);
        } catch (JsonSyntaxException e) {
            throw new LevelFormatException(sourceName + ": malformed JSON — " + e.getMessage(), e);
        }
        return toLevel(dto, sourceName);
    }

    /**
     * Reads an index (a JSON array of filenames) and loads each level through
     * {@code opener}. Results are sorted by level id.
     */
    public List<Level> loadAll(InputStream indexStream, StreamOpener opener)
            throws IOException, LevelFormatException {

        List<String> names;
        try (InputStream stream = indexStream;
             Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            names = gson.fromJson(reader, new TypeToken<List<String>>() {}.getType());
        } catch (JsonSyntaxException e) {
            throw new LevelFormatException("Level index is not valid JSON: " + e.getMessage(), e);
        }

        if (names == null || names.isEmpty()) {
            throw new LevelFormatException("Level index is empty or not a JSON array of filenames.");
        }

        List<Level> out = new ArrayList<>(names.size());
        for (String name : names) {
            InputStream in = opener.open(name);
            if (in == null) {
                throw new LevelFormatException("Level index references a missing file: " + name);
            }
            out.add(load(in, name));
        }
        out.sort(Comparator.comparingInt(Level::getId));
        return out;
    }

    // ---- validation -------------------------------------------------------

    private Level toLevel(LevelDto dto, String src) throws LevelFormatException {
        if (dto == null) {
            throw new LevelFormatException(src + ": file is empty.");
        }
        if (dto.id <= 0) {
            throw new LevelFormatException(src + ": 'id' must be a positive integer.");
        }
        if (dto.rows <= 0 || dto.cols <= 0) {
            throw new LevelFormatException(src + ": 'rows' and 'cols' must both be positive.");
        }
        if (dto.tiles == null || dto.tiles.isEmpty()) {
            throw new LevelFormatException(src + ": 'tiles' must list at least one tile.");
        }

        Board probe = new Board(dto.rows, dto.cols);   // used only for inBounds()
        boolean[][] seen = new boolean[dto.rows][dto.cols];
        List<Level.TileSpec> specs = new ArrayList<>(dto.tiles.size());
        int sources = 0, buildings = 0;

        for (int i = 0; i < dto.tiles.size(); i++) {
            TileDto t = dto.tiles.get(i);
            String where = src + ": tiles[" + i + "]";

            if (t == null) {
                throw new LevelFormatException(where + " is null.");
            }
            if (t.r == null || t.c == null) {
                throw new LevelFormatException(where + " is missing 'r' or 'c'.");
            }
            if (!probe.inBounds(t.r, t.c)) {
                throw new LevelFormatException(where + " is out of bounds at ("
                        + t.r + "," + t.c + ") for a " + dto.rows + "x" + dto.cols + " grid.");
            }
            if (seen[t.r][t.c]) {
                throw new LevelFormatException(where + " duplicates the tile at ("
                        + t.r + "," + t.c + ").");
            }

            TileType type;
            try {
                type = TileType.valueOf(
                        t.type == null ? "" : t.type.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                throw new LevelFormatException(where + " has unknown type '" + t.type + "'.");
            }

            seen[t.r][t.c] = true;
            int rotation = t.rotation == null ? 0 : t.rotation;
            boolean locked = t.locked != null && t.locked;

            if (type.isSource()) sources++;
            if (type.isBuilding()) buildings++;

            specs.add(new Level.TileSpec(t.r, t.c, type, rotation, locked));
        }

        if (sources != 1) {
            throw new LevelFormatException(
                    src + ": expected exactly one SOURCE tile, found " + sources + ".");
        }
        if (buildings == 0) {
            throw new LevelFormatException(src + ": needs at least one BUILDING tile.");
        }

        return new Level(dto.id, dto.name, dto.rows, dto.cols, dto.parMoves, specs);
    }

    // ---- DTOs (Gson maps into these; boxed types detect missing fields) ----

    private static final class LevelDto {
        int id;
        String name;
        int rows, cols;
        int parMoves;
        List<TileDto> tiles;
    }

    private static final class TileDto {
        Integer r, c;
        String type;
        Integer rotation;
        Boolean locked;
    }
}