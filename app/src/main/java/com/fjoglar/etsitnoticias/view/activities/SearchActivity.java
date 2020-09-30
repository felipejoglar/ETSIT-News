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
package com.fjoglar.etsitnoticias.view.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.fjoglar.etsitnoticias.R;
import com.fjoglar.etsitnoticias.data.entities.NewsItem;
import com.fjoglar.etsitnoticias.presenter.SearchPresenter;
import com.fjoglar.etsitnoticias.presenter.contracts.SearchContract;
import com.fjoglar.etsitnoticias.view.adapter.NewsListAdapter;
import com.fjoglar.etsitnoticias.view.adapter.NewsListItemAnimator;
import com.fjoglar.etsitnoticias.view.navigation.Navigator;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SearchActivity extends AppCompatActivity
        implements SearchContract.View, NewsListAdapter.OnItemClickListener,
        NewsListAdapter.OnBookmarkClickListener {

    private static final String RECYCLER_VIEW_STATE = "recycler_view_state";

    private SearchContract.Presenter mSearchPresenter;
    private Context mContext;
    private String mQuery;
    private Parcelable mRecyclerNewsListSearchState;

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

        if (savedInstanceState != null) {
            mRecyclerNewsListSearchState = savedInstanceState.getParcelable(RECYCLER_VIEW_STATE);
        }
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
        mRecyclerNewsListSearchState = recyclerNewsListSearch.getLayoutManager().onSaveInstanceState();
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
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(RECYCLER_VIEW_STATE,
                recyclerNewsListSearch.getLayoutManager().onSaveInstanceState());
        super.onSaveInstanceState(outState);
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
    public void onItemClicked(long date) {
        Navigator.getInstance().navigateToNewsDetails(getContext(), date, "NEWS");
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    public void onBookmarkClicked(NewsItem newsItem) {
        mSearchPresenter.manageBookmark(newsItem);
    }

    @Override
    public void showSearchedNews(List<NewsItem> newsItemList) {
        emptyState.setVisibility(View.GONE);
        recyclerNewsListSearch.setVisibility(View.VISIBLE);

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
    public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setPresenter(SearchContract.Presenter presenter) {
        mSearchPresenter = presenter;
    }

    private void searchQuery() {
        if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
            mQuery = getIntent().getStringExtra(SearchManager.QUERY);
            toolbar.setTitle(getString(R.string.search_activity_title, mQuery));
            mSearchPresenter.performSearch(mQuery);
        }
    }

    private void initializeActivity() {
        mSearchPresenter = new SearchPresenter(this);
    }

    private void setUpRecyclerView() {
        final NewsListAdapter adapter = new NewsListAdapter(this, this);
        recyclerNewsListSearch.setAdapter(adapter);
        recyclerNewsListSearch.getLayoutManager().onRestoreInstanceState(mRecyclerNewsListSearchState);
        recyclerNewsListSearch.setHasFixedSize(true);
        recyclerNewsListSearch.setItemAnimator(new NewsListItemAnimator());
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setUpSearchView(MenuItem searchMenuItem) {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        // TextView searchText = (TextView)
        //         searchView.findViewById(androidx.appcompat.appcompat.R.id.search_src_text);
        // searchText.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
    }

    private Context getContext() {
        return mContext;
    }

}
