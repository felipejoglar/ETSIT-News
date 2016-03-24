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
import com.fjoglar.etsitnews.interactor.GetNewsItemByIdInteractor;
import com.fjoglar.etsitnews.interactor.GetNewsItemByIdInteractorImpl;
import com.fjoglar.etsitnews.model.entities.NewsItem;
import com.fjoglar.etsitnews.presenter.base.BasePresenter;
import com.fjoglar.etsitnews.repository.NewsRepositoryImpl;

public class NewsDetailsPresenterImpl extends BasePresenter implements NewsDetailsPresenter,
        GetNewsItemByIdInteractor.Callback {

    private View mView;
    private int mId;

    public NewsDetailsPresenterImpl(Executor executor, MainThread mainThread,
                                    View view, int id) {
        super(executor, mainThread);
        mView = view;
        mId = id;
    }

    @Override
    public void resume() {
        getNewsItemById(mId);
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
    public void getNewsItemById(int id) {
        mView.showProgress();
        GetNewsItemByIdInteractor getNewsItemByIdInteractor =
                new GetNewsItemByIdInteractorImpl(mExecutor,
                        mMainThread, NewsRepositoryImpl.getInstance(), this, id);
        getNewsItemByIdInteractor.execute();
    }

    @Override
    public void onNewsItemLoaded(NewsItem newsItem) {
        mView.showNewsItem(newsItem);
        mView.hideProgress();
    }
}
