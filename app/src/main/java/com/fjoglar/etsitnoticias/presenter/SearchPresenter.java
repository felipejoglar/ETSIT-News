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

import com.fjoglar.etsitnoticias.data.entities.NewsItem;
import com.fjoglar.etsitnoticias.data.repository.NewsRepository;
import com.fjoglar.etsitnoticias.domain.UseCase;
import com.fjoglar.etsitnoticias.domain.UseCaseHandler;
import com.fjoglar.etsitnoticias.domain.usecase.DeleteBookmark;
import com.fjoglar.etsitnoticias.domain.usecase.GetSearch;
import com.fjoglar.etsitnoticias.domain.usecase.SaveBookmark;
import com.fjoglar.etsitnoticias.presenter.contracts.SearchContract;

import java.util.List;

public class SearchPresenter implements SearchContract.Presenter {

    private final SearchContract.View mSearchView;

    private final UseCaseHandler mUseCaseHandler;

    public SearchPresenter(@NonNull SearchContract.View searchView) {
        mSearchView = searchView;
        mUseCaseHandler = UseCaseHandler.getInstance();

        mSearchView.setPresenter(this);
    }

    @Override
    public void performSearch(String query) {
        mSearchView.showProgress();

        GetSearch getSearch = new GetSearch(NewsRepository.getInstance());
        mUseCaseHandler.execute(getSearch, new GetSearch.RequestValues(query),
                new UseCase.UseCaseCallback<GetSearch.ResponseValue>() {
                    @Override
                    public void onSuccess(GetSearch.ResponseValue response) {
                        mSearchView.showSearchedNews(response.getNewsItemSearchList());
                        mSearchView.hideProgress();
                        checkForErrors(response.getNewsItemSearchList());
                    }

                    @Override
                    public void onError() {
                        mSearchView.hideProgress();
                        mSearchView.showError();
                    }
                });
    }

    public void manageBookmark(NewsItem newsItem) {
        mSearchView.showProgress();
        if (newsItem.getBookmarked() == 0) {
            SaveBookmark saveBookmark = new SaveBookmark(NewsRepository.getInstance());
            mUseCaseHandler.execute(saveBookmark, new SaveBookmark.RequestValues(newsItem),
                    new UseCase.UseCaseCallback<SaveBookmark.ResponseValue>() {
                        @Override
                        public void onSuccess(SaveBookmark.ResponseValue response) {
                            mSearchView.hideProgress();
                            mSearchView.showMessage("Favorito guardado");
                        }

                        @Override
                        public void onError() {
                            mSearchView.hideProgress();
                        }
                    });
        } else {
            DeleteBookmark deleteBookmark = new DeleteBookmark(NewsRepository.getInstance());
            mUseCaseHandler.execute(deleteBookmark,
                    new DeleteBookmark.RequestValues(newsItem.getFormattedPubDate()),
                    new UseCase.UseCaseCallback<DeleteBookmark.ResponseValue>() {
                        @Override
                        public void onSuccess(DeleteBookmark.ResponseValue response) {
                            mSearchView.hideProgress();
                            mSearchView.showMessage("Favorito borrado");
                        }

                        @Override
                        public void onError() {
                            mSearchView.hideProgress();
                        }
                    });
        }
    }

    @Override
    public void start() {
        // Do nothing
    }

    private void checkForErrors(List<NewsItem> newsItemList) {
        if (newsItemList == null || newsItemList.size() == 0) {
            mSearchView.showError();
        }
    }

}
