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
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fjoglar.etsitnews.R;
import com.fjoglar.etsitnews.executor.ThreadExecutor;
import com.fjoglar.etsitnews.model.entities.NewsItem;
import com.fjoglar.etsitnews.presenter.NewsDetailsPresenter;
import com.fjoglar.etsitnews.presenter.NewsDetailsPresenterImpl;
import com.fjoglar.etsitnews.threading.MainThreadImpl;
import com.fjoglar.etsitnews.utils.AttachmentsUtils;
import com.fjoglar.etsitnews.utils.DateUtils;
import com.fjoglar.etsitnews.utils.FormatTextUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewsDetailsActivity extends AppCompatActivity implements NewsDetailsPresenter.View {

    private static final String INTENT_EXTRA_PARAM_NEWS_ITEM_ID =
            "com.fjoglar.INTENT_PARAM_NEWS_ITEM";
    private static final String INSTANCE_STATE_PARAM_NEWS_ITEM_ID =
            "com.fjoglar.STATE_PARAM_NEWS_ITEM";

    public static Intent getCallingIntent(Context context, int id) {
        Intent callingIntent = new Intent(context, NewsDetailsActivity.class);
        callingIntent.putExtra(INTENT_EXTRA_PARAM_NEWS_ITEM_ID, id);
        return callingIntent;
    }

    private NewsDetailsPresenter mNewsDetailsPresenter;
    private int mNewsItemId;
    private Context mContext;

    @Bind(R.id.detail_progress_bar) ProgressBar detailProgressBar;
    @Bind(R.id.detail_attachments_card) CardView detailAttachmentsCard;
    @Bind(R.id.detail_attachments_card_content) LinearLayout detailAttachmentsCardContent;
    @Bind(R.id.detail_title) TextView detailTitle;
    @Bind(R.id.detail_date) TextView detailDate;
    @Bind(R.id.detail_description) TextView detailDescription;
    @Bind(R.id.detail_category) TextView detailCategory;
    @Bind(R.id.detail_link) TextView detailLink;

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
            outState.putInt(INSTANCE_STATE_PARAM_NEWS_ITEM_ID, this.mNewsItemId);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * Initializes this activity.
     */
    private void initializeActivity(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            this.mNewsItemId =
                    getIntent().getIntExtra(INTENT_EXTRA_PARAM_NEWS_ITEM_ID, 0);
        } else {
            this.mNewsItemId =
                    savedInstanceState.getInt(INSTANCE_STATE_PARAM_NEWS_ITEM_ID);
        }
        mNewsDetailsPresenter = new NewsDetailsPresenterImpl(ThreadExecutor.getInstance(),
                MainThreadImpl.getInstance(),
                this,
                mNewsItemId);
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public void showNewsItem(NewsItem newsItem) {

        detailTitle.setText(newsItem.getTitle());
        detailDate.setText(DateUtils.formatDetailViewTime(newsItem.getFormattedPubDate()));
        detailDescription.setText(FormatTextUtils.formatText(newsItem.getDescription()));
        detailCategory.setText(FormatTextUtils.categoryToString(getContext(),
                newsItem.getCategory()));

        List<AttachmentsUtils.Attachment> attachmentList = AttachmentsUtils
                .extractAttachments(newsItem.getAttachments());

        if (attachmentList != null) {
            if (detailAttachmentsCard.getVisibility() == View.GONE) {
                detailAttachmentsCard.setVisibility(View.VISIBLE);
                for (final AttachmentsUtils.Attachment attachment : attachmentList) {
                    final TextView attachmentTextView = new TextView(this);
                    configureTextView(attachmentTextView,
                            attachment.getTitle(),
                            attachment.getDownloadLink(),
                            attachment.getFileType());
                    detailAttachmentsCardContent.addView(attachmentTextView);
                }
            }
        }
    }

    @Override
    public void showProgress() {
        detailProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        detailProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showError(String message) {

    }

    @OnClick(R.id.detail_link)
    void showMoreInfo() {

    }

    /**
     * Configure the TextViews that will show the attachments of the new.
     *
     * @param textView      TextView to be configured.
     * @param title         Text shown in TextView
     * @param downloadLink  Link attached to TextView.
     */
    private void configureTextView(TextView textView,
                                   String title,
                                   final String downloadLink,
                                   AttachmentsUtils.Attachment.FILE_TYPE fileType) {

        final int TEXT_VIEW_MIN_HEIGHT = 40;
        final int TEXT_VIEW_MARGIN_TOP = 4;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, convertDpToPx(TEXT_VIEW_MARGIN_TOP), 0, 0);
        textView.setLayoutParams(params);

        textView.setText(title);
        textView.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
        textView.setMinHeight(convertDpToPx(TEXT_VIEW_MIN_HEIGHT));
        textView.setGravity(Gravity.CENTER_VERTICAL);

        switch (fileType) {
            case FILE:
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_file, 0, 0, 0);
                break;
            case IMAGE:
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_photo, 0, 0, 0);
                break;
            default:
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_link, 0, 0, 0);
                break;
        }

        textView.setCompoundDrawablePadding(convertDpToPx(4));

        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground,
                typedValue,
                true);
        textView.setBackgroundResource(typedValue.resourceId);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),
                        downloadLink,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Converts dps into pixels.
     *
     * @param dp    Measure in dp.
     * @return      Measure in px.
     */
    private int convertDpToPx(int dp) {
        return Math.round(dp
                * (getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));

    }
}
