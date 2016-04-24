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
import com.fjoglar.etsitnews.domain.usecase.GetBookmarks;
import com.fjoglar.etsitnews.model.repository.NewsRepositoryImpl;
import com.fjoglar.etsitnews.presenter.contracts.BookmarksListContract;

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

        GetBookmarks getBookmarks = new GetBookmarks(NewsRepositoryImpl.getInstance());
        mUseCaseHandler.execute(getBookmarks, new GetBookmarks.RequestValues(),
                new UseCase.UseCaseCallback<GetBookmarks.ResponseValue>() {
                    @Override
                    public void onSuccess(GetBookmarks.ResponseValue response) {
                        mBookmarksListView.showNews(response.getNewsItemList());
                        mBookmarksListView.hideProgress();
                    }

                    @Override
                    public void onError(Error error) {
                        mBookmarksListView.hideProgress();
                    }
                });
    }


    @Override
    public void start() {
        getBookmarks();
    }
}
