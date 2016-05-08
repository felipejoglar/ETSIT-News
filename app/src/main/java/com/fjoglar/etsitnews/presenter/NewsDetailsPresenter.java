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
import com.fjoglar.etsitnews.domain.usecase.DeleteBookmark;
import com.fjoglar.etsitnews.domain.usecase.GetBookmarkByDate;
import com.fjoglar.etsitnews.domain.usecase.GetNewsItemByDate;
import com.fjoglar.etsitnews.domain.usecase.SaveBookmark;
import com.fjoglar.etsitnews.model.entities.NewsItem;
import com.fjoglar.etsitnews.model.repository.NewsRepository;
import com.fjoglar.etsitnews.presenter.contracts.NewsDetailsContract;

public class NewsDetailsPresenter implements NewsDetailsContract.Presenter {

    private final NewsDetailsContract.View mNewsDetailsView;
    private final UseCaseHandler mUseCaseHandler;
    private final long mDate;
    private final String mSource;

    public NewsDetailsPresenter(@NonNull NewsDetailsContract.View newsDetailsView,
                                long date, String source) {

        mNewsDetailsView = newsDetailsView;
        mDate = date;
        mSource = source;
        mUseCaseHandler = UseCaseHandler.getInstance();

        mNewsDetailsView.setPresenter(this);
    }

    @Override
    public void getNewsItemByDate(long date, String source) {
        mNewsDetailsView.showProgress();
        if (source.equals("NEWS")) {
            GetNewsItemByDate getNewsItemByDate =
                    new GetNewsItemByDate(NewsRepository.getInstance());
            mUseCaseHandler.execute(getNewsItemByDate, new GetNewsItemByDate.RequestValues(date),
                    new UseCase.UseCaseCallback<GetNewsItemByDate.ResponseValue>() {
                        @Override
                        public void onSuccess(GetNewsItemByDate.ResponseValue response) {
                            mNewsDetailsView.showNewsItem(response.getNewsItem());
                            mNewsDetailsView.hideProgress();
                        }

                        @Override
                        public void onError() {
                            mNewsDetailsView.hideProgress();
                        }
                    });
        } else if (source.equals("BOOKMARKS")) {
            GetBookmarkByDate getBookmarkByDate =
                    new GetBookmarkByDate(NewsRepository.getInstance());
            mUseCaseHandler.execute(getBookmarkByDate, new GetBookmarkByDate.RequestValues(date),
                    new UseCase.UseCaseCallback<GetBookmarkByDate.ResponseValue>() {
                        @Override
                        public void onSuccess(GetBookmarkByDate.ResponseValue response) {
                            mNewsDetailsView.showNewsItem(response.getNewsItem());
                            mNewsDetailsView.hideProgress();
                        }

                        @Override
                        public void onError() {
                            mNewsDetailsView.showNewsItem(null);
                            mNewsDetailsView.hideProgress();
                        }
                    });
        }
    }

    @Override
    public void manageBookmark(NewsItem newsItem) {
        mNewsDetailsView.showProgress();
        if (newsItem.getBookmarked() == 0) {
            SaveBookmark saveBookmark = new SaveBookmark(NewsRepository.getInstance());
            mUseCaseHandler.execute(saveBookmark, new SaveBookmark.RequestValues(newsItem),
                    new UseCase.UseCaseCallback<SaveBookmark.ResponseValue>() {
                        @Override
                        public void onSuccess(SaveBookmark.ResponseValue response) {
                            getNewsItemByDate(mDate, mSource);
                            mNewsDetailsView.hideProgress();
                            mNewsDetailsView.showError("Favorito guardado");
                        }

                        @Override
                        public void onError() {
                            mNewsDetailsView.hideProgress();
                        }
                    });
        } else {
            DeleteBookmark deleteBookmark = new DeleteBookmark(NewsRepository.getInstance());
            mUseCaseHandler.execute(deleteBookmark,
                    new DeleteBookmark.RequestValues(newsItem.getFormattedPubDate()),
                    new UseCase.UseCaseCallback<DeleteBookmark.ResponseValue>() {
                        @Override
                        public void onSuccess(DeleteBookmark.ResponseValue response) {
                            getNewsItemByDate(mDate, mSource);
                            mNewsDetailsView.hideProgress();
                            mNewsDetailsView.showError("Favorito borrado");
                        }

                        @Override
                        public void onError() {
                            mNewsDetailsView.hideProgress();
                        }
                    });
        }
    }
    @Override
    public void start() {
        getNewsItemByDate(mDate, mSource);
    }
}
