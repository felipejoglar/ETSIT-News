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

import android.content.Context;

import androidx.annotation.NonNull;

import com.fjoglar.etsitnoticias.data.entities.NewsItem;

import java.util.List;

public interface NewsDataSource {

    interface LoadNewsCallback {

        void onNewsLoaded(List<NewsItem> newsItemList);

        void onDataNotAvailable();
    }

    interface GetNewsItemCallback {

        void onNewsItemLoaded(NewsItem newsItem);

        void onDataNotAvailable();
    }

    interface UpdateNewsCallback {

        void onUpdateFinished(Boolean isUpdated);

    }

    void saveNews(@NonNull List<NewsItem> newsItemList);

    void saveBookmark(@NonNull NewsItem newsItem);

    void getAllNews(@NonNull LoadNewsCallback callback);

    void getFilteredNews(List<String> filterKeys, @NonNull LoadNewsCallback callback);

    void getFilteredBookmarks(List<String> filterKeys, @NonNull LoadNewsCallback callback);

    void getSearchNews(String query, @NonNull LoadNewsCallback callback);

    void getBookmarkedNews(@NonNull LoadNewsCallback callback);

    void getNewsItemByDate(long date, @NonNull GetNewsItemCallback callback);

    void getBookmarkByDate(long date, @NonNull GetNewsItemCallback callback);

    void updateNews(@NonNull UpdateNewsCallback callback);

    void updateNewsItemIsBookmarkedStatusByDate(boolean status, long date);

    void deleteBookmarkByDate(long date);

    void setContext(@NonNull Context context);
}
