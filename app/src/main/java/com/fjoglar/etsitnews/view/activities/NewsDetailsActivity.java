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
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fjoglar.etsitnews.R;
import com.fjoglar.etsitnews.executor.ThreadExecutor;
import com.fjoglar.etsitnews.model.entities.Attachment;
import com.fjoglar.etsitnews.model.entities.NewsItem;
import com.fjoglar.etsitnews.navigation.Navigator;
import com.fjoglar.etsitnews.presenter.NewsDetailsPresenter;
import com.fjoglar.etsitnews.presenter.NewsDetailsPresenterImpl;
import com.fjoglar.etsitnews.threading.MainThreadImpl;
import com.fjoglar.etsitnews.utils.AttachmentsUtils;
import com.fjoglar.etsitnews.utils.DateUtils;
import com.fjoglar.etsitnews.utils.FormatTextUtils;
import com.fjoglar.etsitnews.utils.UiUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewsDetailsActivity extends AppCompatActivity implements NewsDetailsPresenter.View {

    private static final String INTENT_EXTRA_PARAM_NEWS_ITEM_DATE =
            "com.fjoglar.INTENT_PARAM_NEWS_ITEM";
    private static final String INSTANCE_STATE_PARAM_NEWS_ITEM_DATE =
            "com.fjoglar.STATE_PARAM_NEWS_ITEM";
    private static final String INTENT_EXTRA_PARAM_SOURCE =
            "com.fjoglar.INTENT_PARAM_SOURCE";
    private static final String INSTANCE_STATE_PARAM_SOURCE =
            "com.fjoglar.STATE_PARAM_SOURCE";

    private NewsDetailsPresenter mNewsDetailsPresenter;

    private String mSource;
    private String mMoreInfoUrl;
    private long mNewsItemDate;
    private Context mContext;
    private Menu mMenu;
    private NewsItem mNewsItem;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.progress_bar) ProgressBar progressBar;
    @Bind(R.id.detail_attachments_card) CardView detailAttachmentsCard;
    @Bind(R.id.detail_attachments_card_content) LinearLayout detailAttachmentsCardContent;
    @Bind(R.id.detail_title) TextView detailTitle;
    @Bind(R.id.detail_date) TextView detailDate;
    @Bind(R.id.detail_description) TextView detailDescription;
    @Bind(R.id.detail_category) TextView detailCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);
        mContext = this;

        this.initializeActivity(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNewsDetailsPresenter.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNewsDetailsPresenter.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mNewsDetailsPresenter.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        mNewsDetailsPresenter.destroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putLong(INSTANCE_STATE_PARAM_NEWS_ITEM_DATE, this.mNewsItemDate);
            outState.putString(INSTANCE_STATE_PARAM_SOURCE, this.mSource);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        mMenu = menu;
        if (mNewsItem != null) {
            updateBookmarkIcon(mNewsItem.getBookmarked() == 1);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_bookmark:
                mNewsDetailsPresenter.manageBookmark(mNewsItem);
                return true;
            case R.id.action_share:
                Toast.makeText(this, "Compartir", Toast.LENGTH_SHORT).show();
                return true;
        }

        return true;
    }

    @Override
    public void showNewsItem(NewsItem newsItem) {
        if (newsItem == null) {
            onBackPressed();
        } else {
            mNewsItem = newsItem;
            mMoreInfoUrl = newsItem.getLink();

            detailTitle.setText(newsItem.getTitle());
            detailDate.setText(DateUtils.formatDetailViewTime(newsItem.getFormattedPubDate()));
            detailDescription.setText(newsItem.getDescription());
            detailCategory.setText(FormatTextUtils.categoryToString(getContext(),
                    newsItem.getCategory()));

            List<Attachment> attachmentList = AttachmentsUtils
                    .extractAttachments(newsItem.getAttachments());

            if (attachmentList != null) {
                if (detailAttachmentsCard.getVisibility() == View.GONE) {
                    detailAttachmentsCard.setVisibility(View.VISIBLE);
                    for (final Attachment attachment : attachmentList) {
                        final TextView attachmentTextView = new TextView(this);
                        UiUtils.configureTextView(attachmentTextView,
                                attachment.getTitle(),
                                attachment.getDownloadLink(),
                                attachment.getFileType(),
                                getContext());
                        detailAttachmentsCardContent.addView(attachmentTextView);
                    }
                }
            }
            // Set the NewsItem title as subtitle of the Toolbar.
            toolbar.setSubtitle(newsItem.getTitle());

            // Set the bookmark icon.
            updateBookmarkIcon(newsItem.getBookmarked() == 1);
        }
    }

    @Override
    public void updateBookmarkIcon(boolean isBookmarked) {
        UiUtils.updateBookmarkIcon(mMenu, isBookmarked, getContext());
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
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static Intent getCallingIntent(Context context, long date, String source) {
        Intent callingIntent = new Intent(context, NewsDetailsActivity.class);
        callingIntent.putExtra(INTENT_EXTRA_PARAM_NEWS_ITEM_DATE, date);
        callingIntent.putExtra(INTENT_EXTRA_PARAM_SOURCE, source);
        return callingIntent;
    }

    @OnClick(R.id.detail_link)
    void showMoreInfo() {
        Navigator.getInstance().openUrl(getContext(), mMoreInfoUrl);
    }

    /**
     * Initializes this activity.
     */
    private void initializeActivity(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            this.mNewsItemDate =
                    getIntent().getLongExtra(INTENT_EXTRA_PARAM_NEWS_ITEM_DATE, 0);
            this.mSource =
                    getIntent().getStringExtra(INTENT_EXTRA_PARAM_SOURCE);
        } else {
            this.mNewsItemDate =
                    savedInstanceState.getLong(INSTANCE_STATE_PARAM_NEWS_ITEM_DATE);
            this.mSource =
                    savedInstanceState.getString(INSTANCE_STATE_PARAM_SOURCE);
        }
        mNewsDetailsPresenter = new NewsDetailsPresenterImpl(ThreadExecutor.getInstance(),
                MainThreadImpl.getInstance(),
                this,
                mNewsItemDate,
                mSource);
        setUpToolbar();
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setTitle(R.string.news_details_activity_title);
    }

    private Context getContext() {
        return mContext;
    }

}
