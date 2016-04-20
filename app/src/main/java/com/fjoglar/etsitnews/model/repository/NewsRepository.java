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
package com.fjoglar.etsitnews.model.repository;

import android.content.Context;

import com.fjoglar.etsitnews.model.entities.NewsItem;

import java.util.List;

public interface NewsRepository {

    void insertNews(List<NewsItem> newsItemList);

    List<NewsItem> getAllNews();

    NewsItem getNewsItemByDate(long date);

    void updateNews();

    void updateNewsItemIsBookmarkedStatusByDate(boolean status, long date);

    void insertBookmark(NewsItem newsItem);

    List<NewsItem> getBookmarkedNews();

    NewsItem getBookmarkByDate(long date);

    void deleteBookmarkByDate(long date);

    void setContext(Context context);
}
