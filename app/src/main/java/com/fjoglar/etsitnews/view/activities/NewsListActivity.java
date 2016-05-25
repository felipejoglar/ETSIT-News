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
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.Toast;

import com.fjoglar.etsitnews.R;
import com.fjoglar.etsitnews.model.entities.Category;
import com.fjoglar.etsitnews.model.entities.NewsItem;
import com.fjoglar.etsitnews.presenter.NewsListPresenter;
import com.fjoglar.etsitnews.presenter.contracts.NewsListContract;
import com.fjoglar.etsitnews.sync.EtsitSyncAdapter;
import com.fjoglar.etsitnews.utils.CategoryUtils;
import com.fjoglar.etsitnews.utils.NetUtils;
import com.fjoglar.etsitnews.view.adapter.FilterAdapter;
import com.fjoglar.etsitnews.view.adapter.NewsListAdapter;
import com.fjoglar.etsitnews.view.navigation.Navigator;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class NewsListActivity extends AppCompatActivity
        implements NewsListContract.View, NewsListAdapter.ItemClickListener,
        FilterAdapter.FilterItemClickListener, FilterAdapter.FilterItemCheckBoxClickListener {

    private static final String ACTIVITY_SOURCE = "NEWS";

    private NewsListContract.Presenter mNewsListPresenter;
    private Context mContext;

    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.filter_list) RecyclerView filterList;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recycler_news_list) RecyclerView recyclerNewsList;
    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.empty_state) RelativeLayout emptyState;
    @BindView(R.id.empty_state_image) ImageView emptyStateImage;
    @BindView(R.id.empty_state_msg) TextView emptyStateMsg;
    @BindView(R.id.empty_state_msg_hint) TextView emptyStateMsgHint;
    @BindView(R.id.empty_state_button) Button emptyStateButton;

    TextView lastTimeUpdated;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        unbinder = ButterKnife.bind(this);
        lastTimeUpdated = (TextView) navigationView.getHeaderView(0)
                .findViewById(R.id.last_time_updated);

        mContext = this;
        initializeActivity();

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
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            case R.id.action_search:
                Toast.makeText(this, "Buscar", Toast.LENGTH_SHORT).show();
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
    public void itemClicked(long date) {
        Navigator.getInstance().navigateToNewsDetails(getContext(), date, ACTIVITY_SOURCE);
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
    public void showLastUpdateTime(String lastUpdateTime) {
        lastTimeUpdated.setText(lastUpdateTime);
    }

    @Override
    public void updateFilterList() {
        FilterAdapter adapter = (FilterAdapter) filterList.getAdapter();
        adapter.setFilterAdapter(CategoryUtils.createCategoryList());
        adapter.notifyDataSetChanged();
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
        mNewsListPresenter = new NewsListPresenter(this);
        setUpRecyclerView();
        setUpToolbar();
        setUpNavigationDrawer();
        setUpFilterDrawer();
    }

    private void setUpRecyclerView() {
        final NewsListAdapter adapter = new NewsListAdapter(this);
        recyclerNewsList.setAdapter(adapter);
        recyclerNewsList.setLayoutManager(new LinearLayoutManager(getParent(),
                LinearLayoutManager.VERTICAL,
                false)
        );
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
                                Navigator.getInstance().navigateToBookmarksList(getContext());
                                menuItem.setChecked(true);
                                break;
                            case R.id.settings_navigation_menu_item:
                                Toast.makeText(getContext(), "Ajustes", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.about_navigation_menu_item:
                                Toast.makeText(getContext(), "Acerca de", Toast.LENGTH_SHORT).show();
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
        filterList.setLayoutManager(new LinearLayoutManager(getParent(),
                LinearLayoutManager.VERTICAL,
                false)
        );
    }

    private Context getContext() {
        return mContext;
    }

}
