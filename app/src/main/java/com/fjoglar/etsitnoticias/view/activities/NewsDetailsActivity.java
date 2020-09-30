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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.fjoglar.etsitnoticias.R;
import com.fjoglar.etsitnoticias.data.entities.Attachment;
import com.fjoglar.etsitnoticias.data.entities.NewsItem;
import com.fjoglar.etsitnoticias.presenter.NewsDetailsPresenter;
import com.fjoglar.etsitnoticias.presenter.contracts.NewsDetailsContract;
import com.fjoglar.etsitnoticias.utils.AttachmentsUtils;
import com.fjoglar.etsitnoticias.utils.CategoryUtils;
import com.fjoglar.etsitnoticias.utils.DateUtils;
import com.fjoglar.etsitnoticias.utils.UiUtils;
import com.fjoglar.etsitnoticias.view.navigation.Navigator;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class NewsDetailsActivity extends AppCompatActivity implements NewsDetailsContract.View {

    private static final String INTENT_EXTRA_PARAM_NEWS_ITEM_DATE =
            "com.fjoglar.INTENT_PARAM_NEWS_ITEM";
    private static final String INSTANCE_STATE_PARAM_NEWS_ITEM_DATE =
            "com.fjoglar.STATE_PARAM_NEWS_ITEM";
    private static final String INTENT_EXTRA_PARAM_SOURCE =
            "com.fjoglar.INTENT_PARAM_SOURCE";
    private static final String INSTANCE_STATE_PARAM_SOURCE =
            "com.fjoglar.STATE_PARAM_SOURCE";
    private static final String INSTANCE_STATE_SCROLL_POSITION =
            "com.fjoglar.STATE_SCROLL_POSITION";

    static final String SHARE_HASHTAG = "#NoticiasETSIT";

    private NewsDetailsContract.Presenter mNewsDetailsPresenter;

    private String mSource;
    private String mMoreInfoUrl;
    private long mNewsItemDate;
    private Context mContext;
    private Menu mMenu;
    private NewsItem mNewsItem;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.scrollView) ScrollView scrollView;
    @BindView(R.id.detail_attachments_card) CardView detailAttachmentsCard;
    @BindView(R.id.detail_attachments_card_content) LinearLayout detailAttachmentsCardContent;
    @BindView(R.id.detail_title) TextView detailTitle;
    @BindView(R.id.detail_date) TextView detailDate;
    @BindView(R.id.detail_description) TextView detailDescription;
    @BindView(R.id.detail_category) TextView detailCategory;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        unbinder = ButterKnife.bind(this);
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
        mNewsDetailsPresenter.start();
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
    protected void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putLong(INSTANCE_STATE_PARAM_NEWS_ITEM_DATE, this.mNewsItemDate);
            outState.putString(INSTANCE_STATE_PARAM_SOURCE, this.mSource);
            outState.putIntArray(INSTANCE_STATE_SCROLL_POSITION,
                    new int[]{ scrollView.getScrollX(), scrollView.getScrollY()});
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int[] position = savedInstanceState.getIntArray(INSTANCE_STATE_SCROLL_POSITION);
        if(position != null)
            scrollView.postDelayed(new Runnable() {
                public void run() {
                    scrollView.scrollBy(position[0], position[1]);
                }
            }, 100);
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
                mNewsItem.changeBookmarkedStatus(mNewsItem.getBookmarked());
                return true;
            case R.id.action_share:
                String shareText =
                        mNewsItem.getTitle() + ". "
                        + mNewsItem.getLink() + " " + SHARE_HASHTAG;
                shareNewsItem(shareText);
                return true;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
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
            detailCategory.setText(CategoryUtils.categoryToString(getContext(),
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
    public void setPresenter(NewsDetailsContract.Presenter presenter) {
        mNewsDetailsPresenter = presenter;
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
        mNewsDetailsPresenter = new NewsDetailsPresenter(this, mNewsItemDate, mSource);
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

    private void shareNewsItem(String text) {
        Intent sharingIntent = new Intent();
        sharingIntent.setAction(android.content.Intent.ACTION_SEND);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, text);
        sharingIntent.setType("text/plain");
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.action_share_title)));
    }

    private Context getContext() {
        return mContext;
    }

}
