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
import com.fjoglar.etsitnews.model.entities.NewsItem;
import com.fjoglar.etsitnews.presenter.base.BasePresenter;

public class NewsDetailsPresenterImpl extends BasePresenter implements NewsDetailsPresenter {

    private View mView;

    public NewsDetailsPresenterImpl(Executor executor, MainThread mainThread,
                                    View view) {
        super(executor, mainThread);
        mView = view;
    }

    @Override
    public void resume() {
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
    public void getNewsItemById(NewsItem newsItem) {
        mView.showProgress();
        mView.showNewsItem(newsItem);
        mView.hideProgress();
    }
}
