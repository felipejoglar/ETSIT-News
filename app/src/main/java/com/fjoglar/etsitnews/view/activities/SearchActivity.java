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

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fjoglar.etsitnews.R;
import com.fjoglar.etsitnews.model.entities.NewsItem;
import com.fjoglar.etsitnews.presenter.SearchPresenter;
import com.fjoglar.etsitnews.presenter.contracts.SearchContract;
import com.fjoglar.etsitnews.view.adapter.NewsListAdapter;
import com.fjoglar.etsitnews.view.navigation.Navigator;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SearchActivity extends AppCompatActivity
        implements SearchContract.View, NewsListAdapter.ItemClickListener {

    private SearchContract.Presenter mSearchPresenter;
    private Context mContext;
    private String mQuery;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recycler_news_list_search) RecyclerView recyclerNewsListSearch;
    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.empty_state) RelativeLayout emptyState;
    @BindView(R.id.empty_state_image) ImageView emptyStateImage;
    @BindView(R.id.empty_state_msg) TextView emptyStateMsg;
    @BindView(R.id.empty_state_msg_hint) TextView emptyStateMsgHint;
    @BindView(R.id.empty_state_button) Button emptyStateButton;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        unbinder = ButterKnife.bind(this);
        mContext = this;

        initializeActivity();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSearchPresenter.start();
        setUpRecyclerView();
        setUpToolbar();
        searchQuery();
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
        unbinder.unbind();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        setUpSearchView(menu.findItem(R.id.action_search));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    @Override
    public void itemClicked(long date) {
        Navigator.getInstance().navigateToNewsDetails(getContext(), date, "NEWS");
    }

    @Override
    public void showSearchedNews(List<NewsItem> newsItemList) {
        NewsListAdapter adapter = (NewsListAdapter) recyclerNewsListSearch.getAdapter();
        adapter.setNewsListAdapter(newsItemList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showError() {
        emptyState.setVisibility(View.VISIBLE);
        recyclerNewsListSearch.setVisibility(View.GONE);

        emptyStateImage.setImageDrawable(
                getResources().getDrawable(R.drawable.img_no_search));
        emptyStateMsg.setText(R.string.no_search_msg);
        emptyStateMsgHint.setText(getString(R.string.no_searh_msg_hint, mQuery));
        emptyStateButton.setVisibility(View.GONE);
    }

    @Override
    public void setPresenter(SearchContract.Presenter presenter) {
        mSearchPresenter = presenter;
    }

    private void searchQuery() {
        if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
            mQuery = getIntent().getStringExtra(SearchManager.QUERY);
            mSearchPresenter.performSearch(mQuery);
        }
    }

    private void initializeActivity() {
        mSearchPresenter = new SearchPresenter(this);
    }

    private void setUpRecyclerView() {
        final NewsListAdapter adapter = new NewsListAdapter(this);
        recyclerNewsListSearch.setAdapter(adapter);
        recyclerNewsListSearch.setLayoutManager(new LinearLayoutManager(getParent(),
                LinearLayoutManager.VERTICAL,
                false)
        );
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setTitle(R.string.search_hint);
    }

    private void setUpSearchView(MenuItem searchMenuItem) {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE );
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        TextView searchText = (TextView)
                searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchText.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
    }

    private Context getContext() {
        return mContext;
    }

}
