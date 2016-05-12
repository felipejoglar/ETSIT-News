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
package com.fjoglar.etsitnews.presenter;

import android.support.annotation.NonNull;

import com.fjoglar.etsitnews.R;
import com.fjoglar.etsitnews.domain.UseCase;
import com.fjoglar.etsitnews.domain.UseCaseHandler;
import com.fjoglar.etsitnews.domain.usecase.GetNews;
import com.fjoglar.etsitnews.domain.usecase.UpdateNews;
import com.fjoglar.etsitnews.model.repository.NewsRepository;
import com.fjoglar.etsitnews.model.repository.datasource.NewsSharedPreferences;
import com.fjoglar.etsitnews.presenter.contracts.NewsListContract;

public class NewsListPresenter implements NewsListContract.Presenter {

    private final NewsListContract.View mNewsListView;
    private final UseCaseHandler mUseCaseHandler;

    private boolean mFirstLoad = true;

    public NewsListPresenter(@NonNull NewsListContract.View newsListView) {
        mNewsListView = newsListView;
        mUseCaseHandler = UseCaseHandler.getInstance();

        mNewsListView.setPresenter(this);
    }

    @Override
    public void getNews() {
        mNewsListView.showProgress();

        GetNews getNews = new GetNews(NewsRepository.getInstance());
        mUseCaseHandler.execute(getNews, new GetNews.RequestValues(),
                new UseCase.UseCaseCallback<GetNews.ResponseValue>() {
                    @Override
                    public void onSuccess(GetNews.ResponseValue response) {
                        mNewsListView.showNews(response.getNewsItemList());
                        mNewsListView.hideProgress();
                        updateIfNeeded();
                    }

                    @Override
                    public void onError() {
                        mNewsListView.hideProgress();
                        updateIfNeeded();
                    }
                });
    }

    @Override
    public void updateNews() {
        mNewsListView.showProgress();

        UpdateNews updateNews = new UpdateNews(NewsRepository.getInstance());
        mUseCaseHandler.execute(updateNews, new UpdateNews.RequestValues(),
                new UseCase.UseCaseCallback<UpdateNews.ResponseValue>() {
                    @Override
                    public void onSuccess(UpdateNews.ResponseValue response) {
                        getNews();
                        mNewsListView.hideProgress();

                        // Update last updated time in SharedPreferences.
                        NewsSharedPreferences newsSharedPreferences = NewsSharedPreferences.getInstance();
                        newsSharedPreferences.put(
                                newsSharedPreferences.getStringFromResId(R.string.pref_last_updated_key),
                                System.currentTimeMillis());
                    }

                    @Override
                    public void onError() {
                        mNewsListView.hideProgress();
                    }
                });
    }

    @Override
    public void start() {
        getNews();
    }

    private void updateIfNeeded() {
        if (mFirstLoad) {
            updateNews();
            mFirstLoad = false;
        }
    }
}
