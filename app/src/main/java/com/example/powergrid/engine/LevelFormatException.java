package com.example.powergrid.engine;

/** Thrown when a level JSON file is missing, malformed, or fails validation. */
public class LevelFormatException extends Exception {
    public LevelFormatException(String message) {
        super(message);
    }

    public LevelFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}