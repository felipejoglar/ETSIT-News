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
package com.fjoglar.etsitnews.view.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.fjoglar.etsitnews.R;
import com.fjoglar.etsitnews.executor.ThreadExecutor;
import com.fjoglar.etsitnews.model.entities.NewsItem;
import com.fjoglar.etsitnews.presenter.NewsListPresenter;
import com.fjoglar.etsitnews.presenter.NewsListPresenterImpl;
import com.fjoglar.etsitnews.threading.MainThreadImpl;

import java.util.List;

public class NewsListActivity extends AppCompatActivity implements NewsListPresenter.View {

    private final String LOG_TAG = "ETSIT News App";

    private NewsListPresenter mNewsListPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        mNewsListPresenter = new NewsListPresenterImpl(ThreadExecutor.getInstance(),
                MainThreadImpl.getInstance(), this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNewsListPresenter.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showNews(List<NewsItem> newsItemList) {

    }

    @Override
    public void showError(String message) {

    }
}
