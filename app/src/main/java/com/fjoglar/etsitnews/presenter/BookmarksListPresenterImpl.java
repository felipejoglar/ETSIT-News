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
import com.fjoglar.etsitnews.interactor.GetBookmarksInteractor;
import com.fjoglar.etsitnews.interactor.GetBookmarksInteractorImpl;
import com.fjoglar.etsitnews.model.entities.NewsItem;
import com.fjoglar.etsitnews.presenter.base.BasePresenter;
import com.fjoglar.etsitnews.repository.NewsRepositoryImpl;

import java.util.List;

public class BookmarksListPresenterImpl extends BasePresenter implements BookmarksListPresenter,
        GetBookmarksInteractor.Callback {

    private View mView;

    public BookmarksListPresenterImpl(Executor executor, MainThread mainThread,
                                      View view) {
        super(executor, mainThread);
        mView = view;
    }

    @Override
    public void resume() {
        getBookmarks();
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
    public void getBookmarks() {
        GetBookmarksInteractor getBookmarksInteractor = new GetBookmarksInteractorImpl(mExecutor, mMainThread,
                NewsRepositoryImpl.getInstance(), this);
        getBookmarksInteractor.execute();
    }

    @Override
    public void onBookmarksRetrieved(List<NewsItem> itemList) {
        if (itemList == null || itemList.size() == 0)
            mView.showError("No hay favoritos guardados");

        mView.showNews(itemList);
    }

}
