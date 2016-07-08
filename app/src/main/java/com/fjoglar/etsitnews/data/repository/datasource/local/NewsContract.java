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
package com.fjoglar.etsitnews.data.repository.datasource.local;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Define table and columns names for the database.
 */
public class NewsContract {

    public static final String CONTENT_AUTHORITY = "com.fjoglar.etsitnews";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_NEWS = "news";
    public static final String PATH_BOOKMARKS = "bookmarks";

    // Class that defines the table contents.
    public static final class NewsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_NEWS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY
                        + "/" + PATH_NEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY
                        + "/" + PATH_NEWS;

        // Table name.
        public static final String TABLE_NAME = "news";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_LINK = "link";
        public static final String COLUMN_CATEGORY = "category";
        // Publication date in milliseconds.
        public static final String COLUMN_PUB_DATE = "pub_date";
        public static final String COLUMN_ATTACHMENTS = "attachments";
        public static final String COLUMN_IS_BOOKMARKED = "is_bookmarked";

        public static Uri buildNewsWithDate(long date) {
            return ContentUris.withAppendedId(CONTENT_URI, date);
        }
    }

    // Class that defines the table contents.
    public static final class BookmarksEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BOOKMARKS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY
                        + "/" + PATH_BOOKMARKS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY
                        + "/" + PATH_BOOKMARKS;

        // Table name.
        public static final String TABLE_NAME = "bookmarks";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_LINK = "link";
        public static final String COLUMN_CATEGORY = "category";
        // Publication date in milliseconds.
        public static final String COLUMN_PUB_DATE = "pub_date";
        public static final String COLUMN_ATTACHMENTS = "attachments";
        public static final String COLUMN_IS_BOOKMARKED = "is_bookmarked";

        public static Uri buildBookmarksWithDate(long date) {
            return ContentUris.withAppendedId(CONTENT_URI, date);
        }
    }
}
