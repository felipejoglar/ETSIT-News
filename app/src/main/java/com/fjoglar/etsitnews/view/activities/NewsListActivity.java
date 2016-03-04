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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.fjoglar.etsitnews.R;
import com.fjoglar.etsitnews.executor.ThreadExecutor;
import com.fjoglar.etsitnews.model.entities.NewsItem;
import com.fjoglar.etsitnews.navigation.Navigator;
import com.fjoglar.etsitnews.presenter.NewsListPresenter;
import com.fjoglar.etsitnews.presenter.NewsListPresenterImpl;
import com.fjoglar.etsitnews.threading.MainThreadImpl;
import com.fjoglar.etsitnews.view.adapter.NewsListAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewsListActivity extends AppCompatActivity
        implements NewsListPresenter.View, NewsListAdapter.ItemClickListener {

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, NewsListActivity.class);
    }

    private NewsListPresenter mNewsListPresenter;
    private Context mContext;

    @Bind(R.id.recycler_news_list) RecyclerView recycler_news_list;
    @Bind(R.id.progress_bar) ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        mContext = this;
        this.initializeActivity();
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

    private void initializeActivity() {
        mNewsListPresenter = new NewsListPresenterImpl(ThreadExecutor.getInstance(),
                MainThreadImpl.getInstance(),
                this);
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        final NewsListAdapter adapter = new NewsListAdapter(this);
        recycler_news_list.setAdapter(adapter);
        recycler_news_list.setLayoutManager(new LinearLayoutManager(getParent(),
                        LinearLayoutManager.VERTICAL,
                        false)
        );
    }

    private Context getContext(){
        return mContext;
    }

    @Override
    public void itemClicked(NewsItem newsItem) {
        Navigator.getInstance().navigateToUserDetails(getContext(), newsItem);
    }

    @Override
    public void showProgress() {
        progress_bar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progress_bar.setVisibility(View.GONE);
    }

    @Override
    public void showNews(List<NewsItem> newsItemList) {
        NewsListAdapter adapter = (NewsListAdapter) recycler_news_list.getAdapter();
        adapter.setNewsListAdapter(newsItemList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showError(String message) {
    }

}
