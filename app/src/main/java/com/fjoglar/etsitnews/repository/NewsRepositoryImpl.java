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
package com.fjoglar.etsitnews.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.fjoglar.etsitnews.model.entities.NewsItem;
import com.fjoglar.etsitnews.model.entities.NewsRss;
import com.fjoglar.etsitnews.repository.datasource.NewsRssServiceAPI;
import com.fjoglar.etsitnews.repository.datasource.database.NewsContract;
import com.fjoglar.etsitnews.utils.DateUtils;
import com.fjoglar.etsitnews.utils.FormatTextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class NewsRepositoryImpl implements NewsRepository {

    private final String LOG_TAG = NewsRepositoryImpl.class.getSimpleName();

    private Context mContext;

    private static volatile NewsRepository sNewsRepository;
    private final String API_URL = "http://www.tel.uva.es/";

    private NewsRepositoryImpl() {
        // private constructor
    }

    // Singleton
    public static NewsRepository getInstance() {
        if (sNewsRepository == null) {
            sNewsRepository = new NewsRepositoryImpl();
        }
        return sNewsRepository;
    }

    @Override
    public void updateNews() {
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

        // Now insert all news into the Data Base.
        if (result != null) {
            this.insertNews(result);
        }
    }

    @Override
    public List<NewsItem> getAllNews() {
        List<NewsItem> result = new ArrayList<>();
        Cursor cursor;

        cursor = mContext.getContentResolver().query(NewsContract.NewsEntry.CONTENT_URI,
                null,
                null,
                null,
                NewsContract.NewsEntry.COLUMN_PUB_DATE + " DESC");

        if (cursor != null) {
            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                result.add(cursorRowToNewsItem(cursor));
            }
        }

        return result;
    }

    @Override
    public NewsItem getNewsItemById(int id) {
        NewsItem result = null;
        Cursor cursor;

        cursor = mContext.getContentResolver().query(NewsContract.NewsEntry.buildNewsWithId(id),
                null,
                null,
                null,
                null);

        if (cursor != null) {
            cursor.moveToFirst();
            result = cursorRowToNewsItem(cursor);
        }

        return result;
    }

    @Override
    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    private void insertNews(List<NewsItem> newsItemList) {
        Vector<ContentValues> cVVector = new Vector<>();
        for (NewsItem newsItem : newsItemList) {
            ContentValues contentValues = new ContentValues();
            String description = newsItem.getDescription();
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
                    DateUtils.StringToDate(newsItem.getPubDate()).getTime());

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
    }

    private NewsItem cursorRowToNewsItem(Cursor cursor){
        NewsItem item = new NewsItem();

        item.setTitle(cursor.getString(cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_TITLE)));
        item.setDescription(cursor.getString(cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_DESCRIPTION)));
        item.setLink(cursor.getString(cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_LINK)));
        item.setCategory(cursor.getString(cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_CATEGORY)));
        item.setFormattedPubDate(cursor.getInt(cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_PUB_DATE)));

        return item;
    }

}
