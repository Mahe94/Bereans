package com.example.mahe.bereans;

import android.provider.BaseColumns;

/**
 * Created by mahe on 20/6/15.
 */
public final class ChatContract {

    public ChatContract() {}

    public static abstract class NameEntry implements BaseColumns {
        public static final String TABLE_NAME = "ChatEntry";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_MESSAGE = "message";
    }

}

