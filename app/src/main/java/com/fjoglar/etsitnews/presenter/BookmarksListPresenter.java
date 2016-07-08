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
import com.fjoglar.etsitnews.domain.usecase.GetBookmarks;
import com.fjoglar.etsitnews.domain.usecase.GetFilteredBookmarks;
import com.fjoglar.etsitnews.data.entities.Category;
import com.fjoglar.etsitnews.data.entities.NewsItem;
import com.fjoglar.etsitnews.data.repository.NewsRepository;
import com.fjoglar.etsitnews.data.repository.datasource.NewsSharedPreferences;
import com.fjoglar.etsitnews.presenter.contracts.BookmarksListContract;
import com.fjoglar.etsitnews.utils.CategoryUtils;
import com.fjoglar.etsitnews.utils.DateUtils;

import java.util.List;

public class BookmarksListPresenter implements BookmarksListContract.Presenter {

    private final BookmarksListContract.View mBookmarksListView;

    private final UseCaseHandler mUseCaseHandler;

    public BookmarksListPresenter(@NonNull BookmarksListContract.View bookmarksListView) {
        mBookmarksListView = bookmarksListView;
        mUseCaseHandler = UseCaseHandler.getInstance();

        mBookmarksListView.setPresenter(this);
    }

    @Override
    public void getBookmarks() {
        mBookmarksListView.showProgress();

        if (CategoryUtils.areAllCategoriesActive()) {
            GetBookmarks getBookmarks = new GetBookmarks(NewsRepository.getInstance());
            mUseCaseHandler.execute(getBookmarks, new GetBookmarks.RequestValues(),
                    new UseCase.UseCaseCallback<GetBookmarks.ResponseValue>() {
                        @Override
                        public void onSuccess(GetBookmarks.ResponseValue response) {
                            mBookmarksListView.showNews(response.getNewsItemList());
                            mBookmarksListView.hideProgress();
                            checkForErrors(response.getNewsItemList());
                        }

                        @Override
                        public void onError() {
                            mBookmarksListView.hideProgress();
                            mBookmarksListView.showError();
                        }
                    });
        } else {
            GetFilteredBookmarks getFilteredNews = new GetFilteredBookmarks(NewsRepository.getInstance());
            mUseCaseHandler.execute(getFilteredNews,
                    new GetFilteredBookmarks.RequestValues(CategoryUtils.getActiveCategories()),
                    new UseCase.UseCaseCallback<GetFilteredBookmarks.ResponseValue>() {
                        @Override
                        public void onSuccess(GetFilteredBookmarks.ResponseValue response) {
                            mBookmarksListView.showNews(response.getNewsItemFilteredList());
                            mBookmarksListView.hideProgress();
                            checkForErrors(response.getNewsItemFilteredList());
                        }

                        @Override
                        public void onError() {
                            mBookmarksListView.hideProgress();
                            mBookmarksListView.showError();
                        }
                    });
        }
    }

    @Override
    public void filterItemClicked(List<Category> categoryList, int position) {
        CategoryUtils.updateCategoryFilterStatus(categoryList.get(position));
        getBookmarks();
        mBookmarksListView.updateFilterList();
    }

    @Override
    public void start() {
        getBookmarks();
        showLastUpdateTime();
    }

    private void checkForErrors(List<NewsItem> newsItemList) {
        if (newsItemList == null || newsItemList.size() == 0) {
            mBookmarksListView.showError();
        }
    }

    private void showLastUpdateTime() {
        NewsSharedPreferences newsSharedPreferences = NewsSharedPreferences.getInstance();
        long lastUpdateTimeinMillis = newsSharedPreferences.get(
                newsSharedPreferences.getStringFromResId(R.string.pref_last_updated_key), 0L);

        mBookmarksListView.showLastUpdateTime(DateUtils.formatLastUpdateTime(lastUpdateTimeinMillis));
    }

}
