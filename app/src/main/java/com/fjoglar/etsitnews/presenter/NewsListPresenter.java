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

import com.fjoglar.etsitnews.domain.UseCase;
import com.fjoglar.etsitnews.domain.UseCaseHandler;
import com.fjoglar.etsitnews.domain.usecase.GetNews;
import com.fjoglar.etsitnews.model.repository.NewsRepository;
import com.fjoglar.etsitnews.presenter.contracts.NewsListContract;

public class NewsListPresenter implements NewsListContract.Presenter {

    private final NewsListContract.View mNewsListView;
    private final UseCaseHandler mUseCaseHandler;

    public NewsListPresenter(@NonNull NewsListContract.View newsListView) {
        mNewsListView = newsListView;
        mUseCaseHandler = UseCaseHandler.getInstance();

        mNewsListView.setPresenter(this);
    }

    @Override
    public void getNews() {
        mNewsListView.showProgress();

        GetNews getNews = new GetNews(NewsRepository.getInstance());

        mUseCaseHandler.execute(getNews, new GetNews.RequestValues(true),
                new UseCase.UseCaseCallback<GetNews.ResponseValue>() {
                    @Override
                    public void onSuccess(GetNews.ResponseValue response) {
                        mNewsListView.showNews(response.getNewsItemList());
                        mNewsListView.hideProgress();
                    }

                    @Override
                    public void onError(Error error) {
                        mNewsListView.hideProgress();
                    }
                });
    }

    @Override
    public void start() {
        getNews();
    }
}
