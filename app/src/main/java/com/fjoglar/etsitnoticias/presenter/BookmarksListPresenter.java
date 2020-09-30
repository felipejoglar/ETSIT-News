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

import androidx.annotation.NonNull;

import com.fjoglar.etsitnoticias.R;
import com.fjoglar.etsitnoticias.data.entities.Category;
import com.fjoglar.etsitnoticias.data.entities.NewsItem;
import com.fjoglar.etsitnoticias.data.repository.NewsRepository;
import com.fjoglar.etsitnoticias.data.repository.datasource.NewsSharedPreferences;
import com.fjoglar.etsitnoticias.domain.UseCase;
import com.fjoglar.etsitnoticias.domain.UseCaseHandler;
import com.fjoglar.etsitnoticias.domain.usecase.DeleteBookmark;
import com.fjoglar.etsitnoticias.domain.usecase.GetBookmarks;
import com.fjoglar.etsitnoticias.domain.usecase.GetFilteredBookmarks;
import com.fjoglar.etsitnoticias.domain.usecase.SaveBookmark;
import com.fjoglar.etsitnoticias.presenter.contracts.BookmarksListContract;
import com.fjoglar.etsitnoticias.utils.CategoryUtils;
import com.fjoglar.etsitnoticias.utils.DateUtils;

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
    public void manageBookmark(NewsItem newsItem) {
        mBookmarksListView.showProgress();
        if (newsItem.getBookmarked() == 0) {
            SaveBookmark saveBookmark = new SaveBookmark(NewsRepository.getInstance());
            mUseCaseHandler.execute(saveBookmark, new SaveBookmark.RequestValues(newsItem),
                    new UseCase.UseCaseCallback<SaveBookmark.ResponseValue>() {
                        @Override
                        public void onSuccess(SaveBookmark.ResponseValue response) {
                            getBookmarks();
                            mBookmarksListView.hideProgress();
                            mBookmarksListView.showMessage("Favorito guardado");
                        }

                        @Override
                        public void onError() {
                            mBookmarksListView.hideProgress();
                        }
                    });
        } else {
            DeleteBookmark deleteBookmark = new DeleteBookmark(NewsRepository.getInstance());
            mUseCaseHandler.execute(deleteBookmark,
                    new DeleteBookmark.RequestValues(newsItem.getFormattedPubDate()),
                    new UseCase.UseCaseCallback<DeleteBookmark.ResponseValue>() {
                        @Override
                        public void onSuccess(DeleteBookmark.ResponseValue response) {
                            getBookmarks();
                            mBookmarksListView.hideProgress();
                            mBookmarksListView.showMessage("Favorito borrado");
                        }

                        @Override
                        public void onError() {
                            mBookmarksListView.hideProgress();
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
        long lastUpdateTimeinMillis = newsSharedPreferences.getLong(
                newsSharedPreferences.getStringFromResId(R.string.pref_last_updated_key), 0L);

        mBookmarksListView.showLastUpdateTime(DateUtils.formatLastUpdateTime(lastUpdateTimeinMillis));
    }

}
