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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fjoglar.etsitnews.R;
import com.fjoglar.etsitnews.model.entities.NewsItem;
import com.fjoglar.etsitnews.presenter.BookmarksListPresenter;
import com.fjoglar.etsitnews.presenter.contracts.BookmarksListContract;
import com.fjoglar.etsitnews.view.adapter.NewsListAdapter;
import com.fjoglar.etsitnews.view.navigation.Navigator;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookmarksListActivity extends AppCompatActivity
        implements BookmarksListContract.View, NewsListAdapter.ItemClickListener {

    private static final String ACTIVITY_SOURCE = "BOOKMARKS";

    private BookmarksListContract.Presenter mBookmarksListPresenter;
    private Context mContext;

    @Bind(R.id.drawer_layout) DrawerLayout drawerLayout;
    @Bind(R.id.nav_view) NavigationView navigationView;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.recycler_bookmarks_list)  RecyclerView recyclerBookmarksList;
    @Bind(R.id.progress_bar) ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

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
        mBookmarksListPresenter.start();
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
        ButterKnife.unbind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bookmarks, menu);
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
                Toast.makeText(this, "Filtrar", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_search:
                Toast.makeText(this, "Buscar", Toast.LENGTH_SHORT).show();
                return true;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        Navigator.getInstance().navigateToNewsList(getContext());
        super.onBackPressed();
    }

    @Override
    public void itemClicked(long date) {
        Navigator.getInstance().navigateToNewsDetails(getContext(), date, ACTIVITY_SOURCE);
    }

    @Override
    public void setPresenter(BookmarksListContract.Presenter presenter) {
        mBookmarksListPresenter = presenter;
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
        NewsListAdapter adapter = (NewsListAdapter) recyclerBookmarksList.getAdapter();
        adapter.setNewsListAdapter(newsItemList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, BookmarksListActivity.class);
    }

    private void initializeActivity() {
        mBookmarksListPresenter = new BookmarksListPresenter(this);
        setUpRecyclerView();
        setUpToolbar();
        setUpNavigationDrawer();
    }

    private void setUpRecyclerView() {
        final NewsListAdapter adapter = new NewsListAdapter(this);
        recyclerBookmarksList.setAdapter(adapter);
        recyclerBookmarksList.setLayoutManager(new LinearLayoutManager(getParent(),
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
        toolbar.setTitle(R.string.bookmarks_list_activity_title);
    }

    private void setUpNavigationDrawer() {
        if (navigationView != null) {
            setupDrawerContent(navigationView);
            // We check actual position in Navigation Drawer
            navigationView.getMenu().getItem(1).setChecked(true);
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.list_navigation_menu_item:
                                Navigator.getInstance().navigateToNewsList(getContext());
                                menuItem.setChecked(true);
                                break;
                            case R.id.bookmarks_navigation_menu_item:
                                // Do nothing, we're already on that screen
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

    private Context getContext() {
        return mContext;
    }

}
