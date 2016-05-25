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
import com.fjoglar.etsitnews.domain.usecase.GetAllNews;
import com.fjoglar.etsitnews.domain.usecase.GetFilteredNews;
import com.fjoglar.etsitnews.domain.usecase.UpdateNews;
import com.fjoglar.etsitnews.model.entities.Category;
import com.fjoglar.etsitnews.model.entities.NewsItem;
import com.fjoglar.etsitnews.model.repository.NewsRepository;
import com.fjoglar.etsitnews.model.repository.datasource.NewsSharedPreferences;
import com.fjoglar.etsitnews.presenter.contracts.NewsListContract;
import com.fjoglar.etsitnews.utils.CategoryUtils;
import com.fjoglar.etsitnews.utils.DateUtils;

import java.util.List;

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

        if (CategoryUtils.areAllCategoriesActive()) {
            GetAllNews getAllNews = new GetAllNews(NewsRepository.getInstance());
            mUseCaseHandler.execute(getAllNews, new GetAllNews.RequestValues(),
                    new UseCase.UseCaseCallback<GetAllNews.ResponseValue>() {
                        @Override
                        public void onSuccess(GetAllNews.ResponseValue response) {
                            mNewsListView.showNews(response.getNewsItemList());
                            mNewsListView.hideProgress();
                            checkForErrors(response.getNewsItemList());
                            updateIfNeeded();
                        }

                        @Override
                        public void onError() {
                            mNewsListView.hideProgress();
                            mNewsListView.showError();
                            updateIfNeeded();
                        }
                    });
        } else {
            GetFilteredNews getFilteredNews = new GetFilteredNews(NewsRepository.getInstance());
            mUseCaseHandler.execute(getFilteredNews,
                    new GetFilteredNews.RequestValues(CategoryUtils.getActiveCategories()),
                    new UseCase.UseCaseCallback<GetFilteredNews.ResponseValue>() {
                        @Override
                        public void onSuccess(GetFilteredNews.ResponseValue response) {
                            mNewsListView.showNews(response.getNewsItemFilteredList());
                            mNewsListView.hideProgress();
                            checkForErrors(response.getNewsItemFilteredList());
                            updateIfNeeded();
                        }

                        @Override
                        public void onError() {
                            mNewsListView.hideProgress();
                            mNewsListView.showError();
                            updateIfNeeded();
                        }
                    });
        }


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
                        showLastUpdateTime();
                    }

                    @Override
                    public void onError() {
                        mNewsListView.hideProgress();
                    }
                });
    }

    @Override
    public void filterItemClicked(List<Category> categoryList, int position) {
        CategoryUtils.updateCategoryFilterStatus(categoryList.get(position));
        getNews();
        mNewsListView.updateFilterList();
    }

    @Override
    public void start() {
        getNews();
        showLastUpdateTime();
    }

    private void updateIfNeeded() {
        if (mFirstLoad) {
            updateNews();
            mFirstLoad = false;
        }
    }

    private void checkForErrors(List<NewsItem> newsItemList) {
        if (newsItemList == null || newsItemList.size() == 0) {
            mNewsListView.showError();
        }
    }

    private void showLastUpdateTime() {
        NewsSharedPreferences newsSharedPreferences = NewsSharedPreferences.getInstance();
        long lastUpdateTimeinMillis = newsSharedPreferences.get(
                newsSharedPreferences.getStringFromResId(R.string.pref_last_updated_key), 0L);

        mNewsListView.showLastUpdateTime(DateUtils.formatLastUpdateTime(lastUpdateTimeinMillis));
    }
}
