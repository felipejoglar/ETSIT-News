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

import android.util.Log;

import com.fjoglar.etsitnews.model.entities.NewsItem;
import com.fjoglar.etsitnews.model.entities.NewsRss;
import com.fjoglar.etsitnews.repository.datasource.NewsRssServiceAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.SimpleXmlConverterFactory;

public class NewsRepositoryImpl implements NewsRepository {

    private final String LOG_TAG = NewsRepositoryImpl.class.getSimpleName();

    private static volatile NewsRepository sNewsRepository;
    private final String API_URL = "http://www.tel.uva.es/";

    private List<NewsItem> mResult;

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
    public List<NewsItem> getAllNews() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        NewsRssServiceAPI newsRssServiceAPI = retrofit.create(NewsRssServiceAPI.class);

        Call<NewsRss> call = newsRssServiceAPI.loadNewsRss();
        call.enqueue(new Callback<NewsRss>() {
            @Override
            public void onResponse(Response<NewsRss> response) {
                mResult = response.body().getChannel().getItemList();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(LOG_TAG, t.getLocalizedMessage());
                mResult = null;
            }
        });

        return mResult;
    }
}
