/*
 * Copyright (C) 2016 Felipe Joglar Santos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fjoglar.etsitnews.repository.datasource.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fjoglar.etsitnews.repository.datasource.database.NewsContract.NewsEntry;

/**
 * Manages the local database.
 */
public class NewsDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "news.db";

    public NewsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_NEWS_TABLE = "CREATE TABLE " + NewsEntry.TABLE_NAME + " (" +
                NewsEntry._ID + " INTEGER PRIMARY KEY," +
                NewsEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                NewsEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                NewsEntry.COLUMN_LINK + " TEXT NOT NULL, " +
                NewsEntry.COLUMN_CATEGORY + " TEXT NOT NULL, " +
                NewsEntry.COLUMN_PUB_DATE + " INTEGER NOT NULL, " +
                NewsEntry.COLUMN_ATTACHMENTS + " TEXT" +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_NEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NewsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
