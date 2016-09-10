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
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fjoglar.etsitnoticias.R;
import com.fjoglar.etsitnoticias.data.entities.Category;
import com.fjoglar.etsitnoticias.data.entities.NewsItem;
import com.fjoglar.etsitnoticias.presenter.NewsListPresenter;
import com.fjoglar.etsitnoticias.presenter.PresenterHolder;
import com.fjoglar.etsitnoticias.presenter.contracts.NewsListContract;
import com.fjoglar.etsitnoticias.sync.EtsitSyncAdapter;
import com.fjoglar.etsitnoticias.utils.CategoryUtils;
import com.fjoglar.etsitnoticias.utils.NetUtils;
import com.fjoglar.etsitnoticias.view.adapter.FilterAdapter;
import com.fjoglar.etsitnoticias.view.adapter.NewsListAdapter;
import com.fjoglar.etsitnoticias.view.navigation.Navigator;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class NewsListActivity extends AppCompatActivity
        implements NewsListContract.View, NewsListAdapter.OnItemClickListener,
        NewsListAdapter.OnBookmarkClickListener, FilterAdapter.FilterItemClickListener,
        FilterAdapter.FilterItemCheckBoxClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String ACTIVITY_SOURCE = "NEWS";
    private static final String RECYCLER_VIEW_STATE = "recycler_view_state";

    private NewsListContract.Presenter mNewsListPresenter;
    private Context mContext;
    private Parcelable mRecyclerNewsListState;

    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.filter_list) RecyclerView filterList;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.swipe_to_refresh) SwipeRefreshLayout swipeToRefresh;
    @BindView(R.id.recycler_news_list) RecyclerView recyclerNewsList;
    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.empty_state) RelativeLayout emptyState;
    @BindView(R.id.empty_state_image) ImageView emptyStateImage;
    @BindView(R.id.empty_state_msg) TextView emptyStateMsg;
    @BindView(R.id.empty_state_msg_hint) TextView emptyStateMsgHint;
    @BindView(R.id.empty_state_button) Button emptyStateButton;

    private TextView lastTimeUpdated;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Back to main App Theme after launch screen.
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unbinder = ButterKnife.bind(this);
        lastTimeUpdated = (TextView) navigationView.getHeaderView(0)
                .findViewById(R.id.last_time_updated);

        mContext = this;
        initializeActivity();

        if (savedInstanceState != null) {
            mRecyclerNewsListState = savedInstanceState.getParcelable(RECYCLER_VIEW_STATE);
        }

        // Add the Account Required by the SyncAdapter Framework.
        EtsitSyncAdapter.initializeSyncAdapter(getContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNewsListPresenter.start();
        setUpRecyclerView();
        setUpToolbar();
        setUpNavigationDrawer();
        setUpFilterDrawer();
    }

    @Override
    protected void onPause() {
        mRecyclerNewsListState = recyclerNewsList.getLayoutManager().onSaveInstanceState();
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
        if (this.isFinishing()) {
            PresenterHolder.getInstance().remove(NewsListActivity.class);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(RECYCLER_VIEW_STATE,
                recyclerNewsList.getLayoutManager().onSaveInstanceState());
        PresenterHolder.getInstance().putPresenter(NewsListActivity.class, mNewsListPresenter);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        final MenuItem filterMenuItem = menu.findItem(R.id.action_filter);

        setUpSearchView(searchMenuItem, filterMenuItem);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Open the navigation drawer when the home icon is selected from the toolbar.
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_filter:
                drawerLayout.openDrawer(GravityCompat.END);
                return true;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClicked(long date) {
        Navigator.getInstance().navigateToNewsDetails(getContext(), date, ACTIVITY_SOURCE);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    public void onBookmarkClicked(NewsItem newsItem) {
    }

    @Override
    public void filterItemClicked(List<Category> categoryList, int position, CheckBox checkBox) {
        checkBox.setChecked(!checkBox.isChecked());
        mNewsListPresenter.filterItemClicked(categoryList, position);
    }

    @Override
    public void filterItemCheckBoxClicked(List<Category> categoryList, int position) {
        mNewsListPresenter.filterItemClicked(categoryList, position);
    }

    @Override
    public void setPresenter(@NonNull NewsListContract.Presenter presenter) {
        mNewsListPresenter = presenter;
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
    public void showUpdating() {
        swipeToRefresh.post(new Runnable() {
            @Override
            public void run() {
                swipeToRefresh.setRefreshing(true);
            }
        });
    }

    @Override
    public void hideUpdating() {
        swipeToRefresh.setRefreshing(false);
    }

    @Override
    public void showNews(List<NewsItem> newsItemList) {
        emptyState.setVisibility(View.GONE);
        recyclerNewsList.setVisibility(View.VISIBLE);

        NewsListAdapter adapter = (NewsListAdapter) recyclerNewsList.getAdapter();
        adapter.setNewsListAdapter(newsItemList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showError() {
        emptyState.setVisibility(View.VISIBLE);
        recyclerNewsList.setVisibility(View.GONE);
        if (!NetUtils.isNetworkAvailable(getContext())) {
            emptyStateImage.setImageDrawable(
                    getResources().getDrawable(R.drawable.img_no_internet_connection));
            emptyStateMsg.setText(R.string.no_internet_msg);
            emptyStateMsgHint.setText(R.string.no_internet_msg_hint);
            emptyStateButton.setText(R.string.retry_message);
        } else {
            emptyStateImage.setImageDrawable(
                    getResources().getDrawable(R.drawable.img_no_news_to_list));
            emptyStateMsg.setText(R.string.no_news_msg);
            emptyStateMsgHint.setText(R.string.no_news_msg_hint);
            emptyStateButton.setText(R.string.open_filter_message);
        }
    }

    @Override
    public void showNoInternetMsg() {
        Snackbar.make(drawerLayout, R.string.no_internet_msg_short, Snackbar.LENGTH_SHORT)
                .setAction(R.string.retry_message, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refreshNews();
                    }
                })
                .setDuration(7000).show();
    }

    @Override
    public void showLastUpdateTime(String lastUpdateTime) {
        lastTimeUpdated.setText(lastUpdateTime);
    }

    @Override
    public void updateFilterList() {
        FilterAdapter adapter = (FilterAdapter) filterList.getAdapter();
        adapter.setFilterAdapter(CategoryUtils.createCategoryList());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        refreshNews();
    }

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, NewsListActivity.class);
    }

    @OnClick(R.id.empty_state_button)
    void emptyStateButtonClick() {
        String checkButton = getResources().getString(R.string.retry_message);
        if (emptyStateButton.getText().equals(checkButton)) {
            mNewsListPresenter.updateNews();
        } else {
            drawerLayout.openDrawer(GravityCompat.END);
        }
    }

    private void initializeActivity() {
        createPresenter();
        swipeToRefresh.setOnRefreshListener(this);
        swipeToRefresh.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
    }

    public NewsListContract.Presenter createPresenter() {
        mNewsListPresenter = PresenterHolder.getInstance().getPresenter(NewsListActivity.class);
        if (mNewsListPresenter != null) {
            mNewsListPresenter.setView(this);
        } else {
            mNewsListPresenter = new NewsListPresenter(this);
        }
        return mNewsListPresenter;
    }

    private void setUpRecyclerView() {
        final NewsListAdapter adapter = new NewsListAdapter(this, this);
        recyclerNewsList.setAdapter(adapter);
        recyclerNewsList.getLayoutManager().onRestoreInstanceState(mRecyclerNewsListState);
        recyclerNewsList.setHasFixedSize(true);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setTitle(R.string.news_list_activity_title);
    }

    private void setUpNavigationDrawer() {
        if (navigationView != null) {
            setUpDrawerContent(navigationView);
            // We check actual position in Navigation Drawer
            navigationView.getMenu().getItem(0).setChecked(true);
        }
    }

    private void setUpDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.list_navigation_menu_item:
                                // Do nothing, we're already on that screen
                                menuItem.setChecked(true);
                                break;
                            case R.id.bookmarks_navigation_menu_item:
                                menuItem.setChecked(true);
                                Navigator.getInstance().navigateToBookmarksList(getContext());
                                overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
                                break;
                            case R.id.settings_navigation_menu_item:
                                Navigator.getInstance().navigateToSettings(getContext());
                                overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                                break;
                            case R.id.about_navigation_menu_item:
                                Navigator.getInstance().navigateToAbout(getContext());
                                overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                                break;
                            default:
                                break;
                        }
                        // Close the navigation drawer when an item is selected.
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    private void setUpFilterDrawer() {
        final FilterAdapter adapter = new FilterAdapter(this, this);
        adapter.setFilterAdapter(CategoryUtils.createCategoryList());
        filterList.setAdapter(adapter);
    }

    private void setUpSearchView(MenuItem searchMenuItem, final MenuItem filterMenuItem) {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        TextView searchText = (TextView)
                searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchText.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));

        // Hide filter option when SearchView is expanded, restore it when collapsed.
        MenuItemCompat.setOnActionExpandListener(searchMenuItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        filterMenuItem.setVisible(false);
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        filterMenuItem.setVisible(true);
                        return true;
                    }
                });
    }

    private void refreshNews() {
        mNewsListPresenter.updateNews();
    }

    private Context getContext() {
        return mContext;
    }

}
