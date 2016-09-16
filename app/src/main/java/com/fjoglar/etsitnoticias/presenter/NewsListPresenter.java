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
package com.fjoglar.etsitnoticias.presenter;

import android.support.annotation.NonNull;

import com.fjoglar.etsitnoticias.R;
import com.fjoglar.etsitnoticias.domain.UseCase;
import com.fjoglar.etsitnoticias.domain.UseCaseHandler;
import com.fjoglar.etsitnoticias.domain.usecase.DeleteBookmark;
import com.fjoglar.etsitnoticias.domain.usecase.GetAllNews;
import com.fjoglar.etsitnoticias.domain.usecase.GetFilteredNews;
import com.fjoglar.etsitnoticias.domain.usecase.SaveBookmark;
import com.fjoglar.etsitnoticias.domain.usecase.UpdateNews;
import com.fjoglar.etsitnoticias.data.entities.Category;
import com.fjoglar.etsitnoticias.data.entities.NewsItem;
import com.fjoglar.etsitnoticias.data.repository.NewsRepository;
import com.fjoglar.etsitnoticias.data.repository.datasource.NewsSharedPreferences;
import com.fjoglar.etsitnoticias.presenter.contracts.NewsListContract;
import com.fjoglar.etsitnoticias.utils.CategoryUtils;
import com.fjoglar.etsitnoticias.utils.DateUtils;

import java.util.List;

public class NewsListPresenter implements NewsListContract.Presenter {

    private NewsListContract.View mNewsListView;
    private final UseCaseHandler mUseCaseHandler;
    private boolean mUpdateStatus;

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
                        }

                        @Override
                        public void onError() {
                            mNewsListView.hideProgress();
                            mNewsListView.showError();
                        }
                    });
        }
    }

    @Override
    public void updateNews() {
        mNewsListView.showUpdating();
        mUpdateStatus = true;

        UpdateNews updateNews = new UpdateNews(NewsRepository.getInstance());
        mUseCaseHandler.execute(updateNews, new UpdateNews.RequestValues(),
                new UseCase.UseCaseCallback<UpdateNews.ResponseValue>() {
                    @Override
                    public void onSuccess(UpdateNews.ResponseValue response) {
                        getNews();
                        mNewsListView.hideUpdating();
                        mUpdateStatus = false;

                        // Update last updated time in SharedPreferences.
                        NewsSharedPreferences newsSharedPreferences = NewsSharedPreferences.getInstance();
                        newsSharedPreferences.putLong(
                                newsSharedPreferences.getStringFromResId(R.string.pref_last_updated_key),
                                System.currentTimeMillis());
                        showLastUpdateTime();
                    }

                    @Override
                    public void onError() {
                        mNewsListView.hideUpdating();
                        mUpdateStatus = false;
                        mNewsListView.showNoInternetMsg();
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
    public void manageBookmark(NewsItem newsItem) {
        mNewsListView.showProgress();
        if (newsItem.getBookmarked() == 0) {
            SaveBookmark saveBookmark = new SaveBookmark(NewsRepository.getInstance());
            mUseCaseHandler.execute(saveBookmark, new SaveBookmark.RequestValues(newsItem),
                    new UseCase.UseCaseCallback<SaveBookmark.ResponseValue>() {
                        @Override
                        public void onSuccess(SaveBookmark.ResponseValue response) {
                            mNewsListView.hideProgress();
                            mNewsListView.showMessage("Favorito guardado");
                        }

                        @Override
                        public void onError() {
                            mNewsListView.hideProgress();
                        }
                    });
        } else {
            DeleteBookmark deleteBookmark = new DeleteBookmark(NewsRepository.getInstance());
            mUseCaseHandler.execute(deleteBookmark,
                    new DeleteBookmark.RequestValues(newsItem.getFormattedPubDate()),
                    new UseCase.UseCaseCallback<DeleteBookmark.ResponseValue>() {
                        @Override
                        public void onSuccess(DeleteBookmark.ResponseValue response) {
                            mNewsListView.hideProgress();
                            mNewsListView.showMessage("Favorito borrado");
                        }

                        @Override
                        public void onError() {
                            mNewsListView.hideProgress();
                        }
                    });
        }
    }

    @Override
    public void setView(NewsListContract.View newsListView) {
        mNewsListView = newsListView;
    }

    @Override
    public void start() {
        checkUpdatingStatus();
        showLastUpdateTime();
        getNews();
    }

    private void updateIfNeeded() {
        NewsSharedPreferences newsSharedPreferences = NewsSharedPreferences.getInstance();
        boolean firstStart = newsSharedPreferences.getBoolean(
                newsSharedPreferences.getStringFromResId(R.string.pref_first_start_key),
                true);
        if (firstStart) {
            updateNews();
            newsSharedPreferences.putBoolean(
                    newsSharedPreferences.getStringFromResId(R.string.pref_first_start_key),
                    false);
        }
    }

    private void checkForErrors(List<NewsItem> newsItemList) {
        if (newsItemList == null || newsItemList.size() == 0) {
            mNewsListView.showError();
        }
    }

    private void checkUpdatingStatus() {
        if (mUpdateStatus) {
            mNewsListView.showUpdating();
        }
    }

    private void showLastUpdateTime() {
        NewsSharedPreferences newsSharedPreferences = NewsSharedPreferences.getInstance();
        long lastUpdateTimeinMillis = newsSharedPreferences.getLong(
                newsSharedPreferences.getStringFromResId(R.string.pref_last_updated_key), 0L);

        mNewsListView.showLastUpdateTime(DateUtils.formatLastUpdateTime(lastUpdateTimeinMillis));
    }
}
