package com.m2dl.helloandroid.rollingstone.model;

import android.provider.BaseColumns;

/**
 * Created by flemoal on 28/01/16.
 */
public final class Score {
    public Score() {
    }

    public static abstract class ScoreEntry implements BaseColumns {
        public static final String TABLE_NAME = "score";
        public static final String COLUMN_NAME_ENTRY_ID = "scoreid";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_SCORE_VALUE = "score_value";
    }
}
