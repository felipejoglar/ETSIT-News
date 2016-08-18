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
package com.fjoglar.etsitnoticias.data.repository.datasource.local;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.fjoglar.etsitnoticias.data.repository.datasource.local.NewsContract.BookmarksEntry;
import com.fjoglar.etsitnoticias.data.repository.datasource.local.NewsContract.NewsEntry;

public class NewsProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private NewsDbHelper mNewsDbHelper;

    private static final int NEWS = 100;
    private static final int NEWS_ITEM_BY_DATE = 101;
    private static final int BOOKMARKS = 200;
    private static final int BOOKMARKS_ITEM_BY_DATE = 201;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = NewsContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, NewsContract.PATH_NEWS, NEWS);
        matcher.addURI(authority, NewsContract.PATH_NEWS + "/#", NEWS_ITEM_BY_DATE);
        matcher.addURI(authority, NewsContract.PATH_BOOKMARKS, BOOKMARKS);
        matcher.addURI(authority, NewsContract.PATH_BOOKMARKS + "/#", BOOKMARKS_ITEM_BY_DATE);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mNewsDbHelper = new NewsDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case NEWS:
                return NewsEntry.CONTENT_TYPE;
            case NEWS_ITEM_BY_DATE:
                return NewsEntry.CONTENT_ITEM_TYPE;
            case BOOKMARKS:
                return BookmarksEntry.CONTENT_TYPE;
            case BOOKMARKS_ITEM_BY_DATE:
                return BookmarksEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // Query all entries in news table.
            case NEWS: {
                retCursor = mNewsDbHelper.getReadableDatabase().query(
                        NewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // Query a news item by date.
            case NEWS_ITEM_BY_DATE: {
                selection = NewsEntry.COLUMN_PUB_DATE + " = " + uri.getLastPathSegment();
                retCursor = mNewsDbHelper.getReadableDatabase().query(
                        NewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case BOOKMARKS: {
                retCursor = mNewsDbHelper.getReadableDatabase().query(
                        BookmarksEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // Query a bookmark item by date.
            case BOOKMARKS_ITEM_BY_DATE: {
                selection = BookmarksEntry.COLUMN_PUB_DATE + " = " + uri.getLastPathSegment();
                retCursor = mNewsDbHelper.getReadableDatabase().query(
                        BookmarksEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mNewsDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case NEWS: {
                long _id = db.insert(NewsEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = NewsEntry.buildNewsWithDate(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case BOOKMARKS: {
                long _id = db.insert(BookmarksEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = BookmarksEntry.buildBookmarksWithDate(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mNewsDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // This deletes all rows in the table.
        if (null == selection) selection = "1";
        switch (match) {
            case NEWS:
                rowsDeleted = db.delete(NewsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOKMARKS:
                rowsDeleted = db.delete(BookmarksEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOKMARKS_ITEM_BY_DATE:
                selection = BookmarksEntry.COLUMN_PUB_DATE + " = " + uri.getLastPathSegment();
                rowsDeleted = db.delete(BookmarksEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mNewsDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case NEWS:
                rowsUpdated = db.update(NewsEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case NEWS_ITEM_BY_DATE:
                selection = NewsEntry.COLUMN_PUB_DATE + " = " + uri.getLastPathSegment();
                rowsUpdated = db.update(NewsEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case BOOKMARKS:
                rowsUpdated = db.update(BookmarksEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mNewsDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NEWS:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(NewsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
