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

import com.fjoglar.etsitnews.executor.Executor;
import com.fjoglar.etsitnews.executor.MainThread;
import com.fjoglar.etsitnews.interactor.DeleteBookmarksInteractor;
import com.fjoglar.etsitnews.interactor.DeleteBookmarksInteractorImpl;
import com.fjoglar.etsitnews.interactor.GetNewsItemByDateInteractor;
import com.fjoglar.etsitnews.interactor.GetNewsItemByDateInteractorImpl;
import com.fjoglar.etsitnews.interactor.SaveBookmarksInteractor;
import com.fjoglar.etsitnews.interactor.SaveBookmarksInteractorImpl;
import com.fjoglar.etsitnews.model.entities.NewsItem;
import com.fjoglar.etsitnews.presenter.base.BasePresenter;
import com.fjoglar.etsitnews.repository.NewsRepositoryImpl;

public class NewsDetailsPresenterImpl extends BasePresenter implements NewsDetailsPresenter,
        GetNewsItemByDateInteractor.Callback, SaveBookmarksInteractor.Callback,
        DeleteBookmarksInteractor.Callback {

    private View mView;
    private long mDate;

    public NewsDetailsPresenterImpl(Executor executor, MainThread mainThread,
                                    View view, long date) {
        super(executor, mainThread);
        mView = view;
        mDate = date;
    }

    @Override
    public void resume() {
        getNewsItemByDate(mDate);
    }

    @Override
    public void pause() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void destroy() {
        mView = null;
    }

    @Override
    public void onError(String message) {
    }

    @Override
    public void getNewsItemByDate(long date) {
        mView.showProgress();
        GetNewsItemByDateInteractor getNewsItemByDateInteractor =
                new GetNewsItemByDateInteractorImpl(mExecutor,
                        mMainThread, NewsRepositoryImpl.getInstance(), this, date);
        getNewsItemByDateInteractor.execute();
    }

    @Override
    public void manageBookmark(NewsItem newsItem) {
        if (newsItem.getBookmarked() == 0) {
            SaveBookmarksInteractor saveBookmarksInteractor =
                    new SaveBookmarksInteractorImpl(mExecutor,
                            mMainThread, NewsRepositoryImpl.getInstance(), this, newsItem);
            saveBookmarksInteractor.execute();
        } else {
            DeleteBookmarksInteractor deleteBookmarksInteractor =
                    new DeleteBookmarksInteractorImpl(mExecutor,
                            mMainThread, NewsRepositoryImpl.getInstance(), this, newsItem);
            deleteBookmarksInteractor.execute();
        }
    }

    @Override
    public void onNewsItemLoaded(NewsItem newsItem) {
        mView.showNewsItem(newsItem);
        mView.hideProgress();
    }

    @Override
    public void onBookmarkSaved() {
        mView.showError("Favorito guardado");
    }

    @Override
    public void onBookmarkDeleted() {
        mView.showError("Favorito borrado");
    }
}
