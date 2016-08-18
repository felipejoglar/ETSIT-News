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
package com.fjoglar.etsitnoticias.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fjoglar.etsitnoticias.data.entities.NewsItem;
import com.fjoglar.etsitnoticias.data.entities.NewsRss;
import com.fjoglar.etsitnoticias.data.repository.datasource.local.NewsContract;
import com.fjoglar.etsitnoticias.data.repository.datasource.remote.NewsRssServiceAPI;
import com.fjoglar.etsitnoticias.data.repository.search.SearchDatabase;
import com.fjoglar.etsitnoticias.utils.DateUtils;
import com.fjoglar.etsitnoticias.utils.FormatTextUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class NewsRepository implements NewsDataSource {

    private final String LOG_TAG = NewsRepository.class.getSimpleName();

    private static volatile NewsDataSource INSTANCE;

    private final String API_URL = "http://www.tel.uva.es/";
    private final String DOC_ANNOUNCEMENTS = API_URL + "tablon/avisos.htm";
    private final String DOC_TEAM = API_URL + "informacion/equipo.htm";

    private Context mContext;
    private Document mDocAnnouncement;
    private Document mDocTeam;

    private NewsRepository() {
        // private constructor
    }

    @Override
    public void saveNews(@NonNull List<NewsItem> newsItemList) {
        Vector<ContentValues> cVVector = new Vector<>();

        try {
            mDocAnnouncement = Jsoup.connect(DOC_ANNOUNCEMENTS).get();
            mDocTeam = Jsoup.connect(DOC_TEAM).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (NewsItem newsItem : newsItemList) {
            ContentValues contentValues = new ContentValues();
            String description = newsItem.getDescription();
            long date = DateUtils.StringToDate(newsItem.getPubDate()).getTime();
            // Description field cannot be null.
            if (description == null)
                description = "";

            contentValues.put(NewsContract.NewsEntry.COLUMN_TITLE,
                    FormatTextUtils.formatText(newsItem.getTitle()));
            contentValues.put(NewsContract.NewsEntry.COLUMN_DESCRIPTION,
                    FormatTextUtils.formatText(description));
            contentValues.put(NewsContract.NewsEntry.COLUMN_LINK,
                    newsItem.getLink());
            contentValues.put(NewsContract.NewsEntry.COLUMN_CATEGORY,
                    newsItem.getCategory());
            contentValues.put(NewsContract.NewsEntry.COLUMN_PUB_DATE,
                    date);
            contentValues.put(NewsContract.NewsEntry.COLUMN_ATTACHMENTS,
                    getAttachments(newsItem.getLink()));
            contentValues.put(NewsContract.NewsEntry.COLUMN_IS_BOOKMARKED,
                    isNewsItemBookmarked(date));

            cVVector.add(contentValues);
        }

        if (cVVector.size() > 0) {
            // Delete previous data.
            mContext.getContentResolver().delete(NewsContract.NewsEntry.CONTENT_URI, null, null);
            // Insert new data.
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            mContext.getContentResolver()
                    .bulkInsert(NewsContract.NewsEntry.CONTENT_URI, cvArray);
        }

        // Free resources.
        mDocAnnouncement = null;
        mDocTeam = null;
    }

    @Override
    public void saveBookmark(NewsItem newsItem) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(NewsContract.NewsEntry.COLUMN_TITLE,
                newsItem.getTitle());
        contentValues.put(NewsContract.NewsEntry.COLUMN_DESCRIPTION,
                newsItem.getDescription());
        contentValues.put(NewsContract.NewsEntry.COLUMN_LINK,
                newsItem.getLink());
        contentValues.put(NewsContract.NewsEntry.COLUMN_CATEGORY,
                newsItem.getCategory());
        contentValues.put(NewsContract.NewsEntry.COLUMN_PUB_DATE,
                newsItem.getFormattedPubDate());
        contentValues.put(NewsContract.NewsEntry.COLUMN_ATTACHMENTS,
                newsItem.getAttachments());
        contentValues.put(NewsContract.NewsEntry.COLUMN_IS_BOOKMARKED,
                1);

        mContext.getContentResolver()
                .insert(NewsContract.BookmarksEntry.CONTENT_URI, contentValues);
    }

    @Override
    public void getAllNews(@NonNull LoadNewsCallback callback) {
        List<NewsItem> result = new ArrayList<>();
        Cursor cursor;

        cursor = mContext.getContentResolver().query(NewsContract.NewsEntry.CONTENT_URI,
                null,
                null,
                null,
                NewsContract.NewsEntry.COLUMN_PUB_DATE + " DESC");

        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                result.add(cursorRowToNewsItem(cursor));
            }
            cursor.close();
        }

        if (result.size() != 0) {
            callback.onNewsLoaded(result);
        } else {
            callback.onDataNotAvailable();
        }

    }

    @Override
    public void getFilteredNews(List<String> filterKeys, @NonNull LoadNewsCallback callback) {
        List<NewsItem> newsItemList = new ArrayList<>();
        Cursor cursor;

        cursor = mContext.getContentResolver().query(NewsContract.NewsEntry.CONTENT_URI,
                null,
                null,
                null,
                NewsContract.NewsEntry.COLUMN_PUB_DATE + " DESC");

        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                newsItemList.add(cursorRowToNewsItem(cursor));
            }
            cursor.close();
        }

        List<NewsItem> filteredNewsItemList = filterByCategory(newsItemList, filterKeys);

        if (filteredNewsItemList.size() != 0) {
            callback.onNewsLoaded(filteredNewsItemList);
        } else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void getFilteredBookmarks(List<String> filterKeys, @NonNull LoadNewsCallback callback) {
        List<NewsItem> bookmarksList = new ArrayList<>();
        Cursor cursor;

        cursor = mContext.getContentResolver().query(NewsContract.BookmarksEntry.CONTENT_URI,
                null,
                null,
                null,
                NewsContract.BookmarksEntry._ID + " DESC");

        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                bookmarksList.add(cursorRowToNewsItem(cursor));
            }
            cursor.close();
        }

        List<NewsItem> filteredBookmarksList = filterByCategory(bookmarksList, filterKeys);

        if (filteredBookmarksList.size() != 0) {
            callback.onNewsLoaded(filteredBookmarksList);
        } else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void getSearchNews(String query, @NonNull LoadNewsCallback callback) {
        List<NewsItem> newsItemList = new ArrayList<>();
        Cursor cursor;

        cursor = mContext.getContentResolver().query(NewsContract.NewsEntry.CONTENT_URI,
                null,
                null,
                null,
                NewsContract.NewsEntry.COLUMN_PUB_DATE + " DESC");

        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                newsItemList.add(cursorRowToNewsItem(cursor));
            }
            cursor.close();
        }

        List<NewsItem> searchNewsItemList = performSearch(query, newsItemList);

        if (searchNewsItemList.size() != 0) {
            callback.onNewsLoaded(searchNewsItemList);
        } else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void getBookmarkedNews(@NonNull LoadNewsCallback callback) {
        List<NewsItem> result = new ArrayList<>();
        Cursor cursor;

        cursor = mContext.getContentResolver().query(NewsContract.BookmarksEntry.CONTENT_URI,
                null,
                null,
                null,
                NewsContract.BookmarksEntry._ID + " DESC");

        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                result.add(cursorRowToNewsItem(cursor));
            }
            cursor.close();
        }

        if (result.size() != 0) {
            callback.onNewsLoaded(result);
        } else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void getNewsItemByDate(long date, @NonNull GetNewsItemCallback callback) {
        NewsItem result = null;
        Cursor cursor;

        cursor = mContext.getContentResolver().query(NewsContract.NewsEntry.buildNewsWithDate(date),
                null,
                null,
                null,
                null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = cursorRowToNewsItem(cursor);
            cursor.close();
        }

        if (result != null) {
            callback.onNewsItemLoaded(result);
        } else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void getBookmarkByDate(long date, @NonNull GetNewsItemCallback callback) {
        NewsItem result = null;
        Cursor cursor;

        cursor = mContext.getContentResolver()
                .query(NewsContract.BookmarksEntry.buildBookmarksWithDate(date),
                        null,
                        null,
                        null,
                        null);

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                result = cursorRowToNewsItem(cursor);
            }
            cursor.close();
        }

        if (result != null) {
            callback.onNewsItemLoaded(result);
        } else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void updateNews(@NonNull UpdateNewsCallback callback) {
        List<NewsItem> result = null;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        NewsRssServiceAPI newsRssServiceAPI = retrofit.create(NewsRssServiceAPI.class);

        // As we are already in a background thread so we make a Retrofit
        // synchronous request.
        Call<NewsRss> call = newsRssServiceAPI.loadNewsRss();
        try {
            result = call.execute().body().getChannel().getItemList();
        } catch (IOException e) {
            Log.d(LOG_TAG, e.getLocalizedMessage());
        }

        // Now insert all news into the Database.
        if (result != null) {
            this.saveNews(result);
            callback.onUpdateFinished(true);
        } else {
            callback.onUpdateFinished(false);
        }

    }

    @Override
    public void updateNewsItemIsBookmarkedStatusByDate(boolean status, long date) {
        int isBookmarked = (status) ? 1 : 0;
        ContentValues contentValues = new ContentValues();
        contentValues.put(NewsContract.NewsEntry.COLUMN_IS_BOOKMARKED, isBookmarked);
        mContext.getContentResolver().update(NewsContract.NewsEntry.buildNewsWithDate(date),
                contentValues,
                null,
                null);
    }

    @Override
    public void deleteBookmarkByDate(long date) {
        mContext.getContentResolver()
                .delete(NewsContract.BookmarksEntry.buildBookmarksWithDate(date)
                        , null
                        , null);
    }

    @Override
    public void setContext(@NonNull Context mContext) {
        this.mContext = mContext;
    }

    private NewsItem cursorRowToNewsItem(Cursor cursor) {
        NewsItem item = new NewsItem();

        item.setTitle(cursor
                .getString(cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_TITLE)));
        item.setDescription(cursor
                .getString(cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_DESCRIPTION)));
        item.setLink(cursor
                .getString(cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_LINK)));
        item.setCategory(cursor
                .getString(cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_CATEGORY)));
        item.setFormattedPubDate(cursor
                .getLong(cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_PUB_DATE)));
        item.setAttachments(cursor
                .getString(cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_ATTACHMENTS)));
        item.setBookmarked(cursor
                .getInt(cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_IS_BOOKMARKED)));

        return item;
    }

    private NewsItem searchCursorRowToNewsItem(Cursor cursor) {
        NewsItem item = new NewsItem();

        item.setTitle(cursor
                .getString(cursor.getColumnIndex(SearchDatabase.COL_TITLE)));
        item.setDescription(cursor
                .getString(cursor.getColumnIndex(SearchDatabase.COL_DESCRIPTION)));
        item.setCategory(cursor
                .getString(cursor.getColumnIndex(SearchDatabase.COL_CATEGORY)));
        item.setFormattedPubDate(cursor
                .getLong(cursor.getColumnIndex(SearchDatabase.COL_DATE)));

        return item;
    }

    private String getAttachments(String url) {
        Document doc = null;
        String attachments = "";
        String[] urlParts = url.split("#");

        if (urlParts[0].equals(DOC_ANNOUNCEMENTS)) {
            doc = mDocAnnouncement;
        } else if (urlParts[0].equals(DOC_TEAM)) {
            doc = mDocTeam;
        }

        if (doc != null) {
            Element content = doc.getElementById(urlParts[1]);
            if (content != null) {
                Elements links = content.getElementsByTag("a");
                if (links != null) {
                    for (Element link : links) {
                        attachments = attachments
                                + link.attr("abs:href") + "___"
                                + link.text() + "___";
                    }
                }
            }
        }

        return attachments;
    }

    private int isNewsItemBookmarked(long date) {
        final int[] isBookmarked = {0};

        this.getBookmarkByDate(date, new GetNewsItemCallback() {
            @Override
            public void onNewsItemLoaded(NewsItem newsItem) {
                isBookmarked[0] = newsItem.getBookmarked();
            }

            @Override
            public void onDataNotAvailable() {}
        });

        return isBookmarked[0];
    }

    private List<NewsItem> filterByCategory(List<NewsItem> newsItemList, List<String> filterKeys) {
        List<NewsItem> filteredNewsItemList = new ArrayList<>();
        for (NewsItem newsItem : newsItemList) {
            for (String filterKey : filterKeys) {
                if (newsItem.getCategory().equals(filterKey)) {
                    filteredNewsItemList.add(newsItem);
                }
            }
        }
        return filteredNewsItemList;
    }

    private List<NewsItem> performSearch(String query, List<NewsItem> newsItemList) {
        SearchDatabase db = new SearchDatabase(mContext);

        Cursor searchCursor = db.searchNewsItems(query, newsItemList);

        List<NewsItem> searchNewsItemList = new ArrayList<>();

        if (searchCursor != null && searchCursor.getCount() > 0) {
            for (searchCursor.moveToFirst(); !searchCursor.isAfterLast(); searchCursor.moveToNext()) {
                searchNewsItemList.add(searchCursorRowToNewsItem(searchCursor));
            }
            searchCursor.close();
        }

        return searchNewsItemList;
    }

    public static NewsDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NewsRepository();
        }
        return INSTANCE;
    }

}