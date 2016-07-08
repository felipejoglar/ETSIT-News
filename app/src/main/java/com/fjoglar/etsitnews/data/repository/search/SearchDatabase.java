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
package com.fjoglar.etsitnews.data.repository.search;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import com.fjoglar.etsitnews.data.entities.NewsItem;

import java.util.List;

public class SearchDatabase {

    public static final String COL_TITLE = "TITLE";
    public static final String COL_DESCRIPTION = "DESCRIPTION";
    public static final String COL_DATE = "DATE";
    public static final String COL_CATEGORY = "CATEGORY";

    private static final String DATABASE_NAME = "search";
    private static final String FTS_VIRTUAL_TABLE = "FTSsearch";
    private static final int DATABASE_VERSION = 1;

    private final SearchOpenHelper mSearchOpenHelper;
    private final SQLiteDatabase mSqLiteDatabase;

    public SearchDatabase(Context context) {
        mSearchOpenHelper = new SearchOpenHelper(context);
        mSqLiteDatabase = mSearchOpenHelper.getWritableDatabase();
    }

    public Cursor searchNewsItems(String query, List<NewsItem> newsItemList) {
        if (mSqLiteDatabase != null) {
            loadNewsItems(newsItemList, mSqLiteDatabase);
            Cursor searchResult = getNewsItemMatches(query);
            mSearchOpenHelper.deleteTable(mSqLiteDatabase);
            mSqLiteDatabase.close();

            return searchResult;
        }

        return null;
    }

    private void loadNewsItems(List<NewsItem> newsItemList, SQLiteDatabase sqLiteDatabase) {
        for (NewsItem newsItem : newsItemList) {
            ContentValues cv = addNewItem(newsItem);
            sqLiteDatabase.insert(FTS_VIRTUAL_TABLE, null, cv);
        }
    }

    private ContentValues addNewItem(NewsItem newsItem) {
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, newsItem.getTitle());
        values.put(COL_DESCRIPTION, newsItem.getDescription());
        values.put(COL_DATE, newsItem.getFormattedPubDate());
        values.put(COL_CATEGORY, newsItem.getCategory());

        return values;
    }

    private Cursor getNewsItemMatches(String query) {
        String selection = FTS_VIRTUAL_TABLE + " MATCH ?";
        String[] selectionArgs = new String[]{appendWildcard(query)};

        return query(selection, selectionArgs);
    }

    private Cursor query(String selection, String[] selectionArgs) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(FTS_VIRTUAL_TABLE);

        Cursor cursor = builder.query(mSqLiteDatabase,
                null, selection, selectionArgs, null, null, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    private String appendWildcard(String query) {
        final StringBuilder builder = new StringBuilder();
        final String[] splits = query.split(" ");

        for (String split : splits)
            builder.append(split).append("*").append(" ");

        return builder.toString().trim();
    }

    /**
     * This creates/opens the database.
     */
    private static class SearchOpenHelper extends SQLiteOpenHelper {

        private static final String FTS_TABLE_CREATE =
                "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE
                        + " USING fts3 ("
                        + COL_TITLE + ", "
                        + COL_DESCRIPTION + ", "
                        + COL_DATE + ", "
                        + COL_CATEGORY + ");";

        public SearchOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(FTS_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
            onCreate(db);
        }

        public void deleteTable(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
            onCreate(db);
        }

    }

}
