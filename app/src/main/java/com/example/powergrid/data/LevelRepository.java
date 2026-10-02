package com.example.powergrid.data;

import android.content.Context;
import com.example.powergrid.engine.LevelFormatException;
import com.example.powergrid.engine.LevelLoader;
import com.example.powergrid.model.Level;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class LevelRepository {

    private static final String DIR = "levels";
    private static final String INDEX = DIR + "/index.json";

    private final Context context;
    private final LevelLoader loader = new LevelLoader();
    private List<Level> cache;

    public LevelRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    /** All levels, ordered by id. Parsed once, then cached. */
    public List<Level> getLevels() throws IOException, LevelFormatException {
        if (cache == null) {
            try (InputStream index = context.getAssets().open(INDEX)) {
                cache = Collections.unmodifiableList(
                        loader.loadAll(index, name -> context.getAssets().open(DIR + "/" + name)));
            }
        }
        return cache;
    }

    /** @throws IllegalArgumentException if no level has this id. */
    public Level getLevel(int id) throws IOException, LevelFormatException {
        for (Level level : getLevels()) {
            if (level.getId() == id) return level;
        }
        throw new IllegalArgumentException("No level with id " + id);
    }

    /** @return the id of the level after {@code currentId}, or -1 if this is the last one. */
    public int getNextLevelId(int currentId) throws IOException, LevelFormatException {
        for (Level level : getLevels()) {
            if (level.getId() > currentId) return level.getId();
        }
        return -1;
    }

    /** @return the id of the first level, or -1 if there are none. */
    public int getFirstLevelId() throws IOException, LevelFormatException {
        List<Level> levels = getLevels();
        return levels.isEmpty() ? -1 : levels.get(0).getId();
    }
}