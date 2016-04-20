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

import com.fjoglar.etsitnews.domain.executor.Executor;
import com.fjoglar.etsitnews.domain.executor.MainThread;
import com.fjoglar.etsitnews.domain.interactor.DeleteBookmarksInteractor;
import com.fjoglar.etsitnews.domain.interactor.DeleteBookmarksInteractorImpl;
import com.fjoglar.etsitnews.domain.interactor.GetBookmarkByDateInteractor;
import com.fjoglar.etsitnews.domain.interactor.GetBookmarkByDateInteractorImpl;
import com.fjoglar.etsitnews.domain.interactor.GetNewsItemByDateInteractor;
import com.fjoglar.etsitnews.domain.interactor.GetNewsItemByDateInteractorImpl;
import com.fjoglar.etsitnews.domain.interactor.SaveBookmarksInteractor;
import com.fjoglar.etsitnews.domain.interactor.SaveBookmarksInteractorImpl;
import com.fjoglar.etsitnews.model.entities.NewsItem;
import com.fjoglar.etsitnews.presenter.base.BasePresenter;
import com.fjoglar.etsitnews.model.repository.NewsRepositoryImpl;

public class NewsDetailsPresenterImpl extends BasePresenter implements NewsDetailsPresenter,
        GetNewsItemByDateInteractor.Callback, GetBookmarkByDateInteractor.Callback,
        SaveBookmarksInteractor.Callback, DeleteBookmarksInteractor.Callback {

    private View mView;
    private long mDate;
    private String mSource;

    public NewsDetailsPresenterImpl(Executor executor, MainThread mainThread,
                                    View view, long date, String source) {
        super(executor, mainThread);
        mView = view;
        mDate = date;
        mSource = source;
    }

    @Override
    public void resume() {
        getNewsItemByDate(mDate, mSource);
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
    public void getNewsItemByDate(long date, String source) {
        mView.showProgress();
        if (source.equals("NEWS")) {
            GetNewsItemByDateInteractor getNewsItemByDateInteractor =
                    new GetNewsItemByDateInteractorImpl(mExecutor,
                            mMainThread, NewsRepositoryImpl.getInstance(), this, date);
            getNewsItemByDateInteractor.execute();
        } else if (source.equals("BOOKMARKS")) {
            GetBookmarkByDateInteractor getBookmarkByDateInteractor =
                    new GetBookmarkByDateInteractorImpl(mExecutor,
                            mMainThread, NewsRepositoryImpl.getInstance(), this, date);
            getBookmarkByDateInteractor.execute();
        }
    }

    @Override
    public void manageBookmark(NewsItem newsItem) {
        mView.showProgress();
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
    public void onBookmarkLoaded(NewsItem newsItem) {
        mView.showNewsItem(newsItem);
        mView.hideProgress();
    }

    @Override
    public void onBookmarkSaved() {
        getNewsItemByDate(mDate, mSource);
        mView.hideProgress();
        mView.showError("Favorito guardado");
    }

    @Override
    public void onBookmarkDeleted() {
        getNewsItemByDate(mDate, mSource);
        mView.hideProgress();
        mView.showError("Favorito borrado");
    }
}
