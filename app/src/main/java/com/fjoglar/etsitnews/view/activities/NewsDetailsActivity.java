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
import android.widget.TextView;

import com.fjoglar.etsitnews.R;
import com.fjoglar.etsitnews.model.entities.NewsItem;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewsDetailsActivity extends AppCompatActivity {

    private static final String INTENT_EXTRA_PARAM_NEWS_ITEM = "com.fjoglar.INTENT_PARAM_NEWS_ITEM";
    private static final String INSTANCE_STATE_PARAM_NEWS_ITEM = "com.fjoglar.STATE_PARAM_NEWS_ITEM";

    public static Intent getCallingIntent(Context context, NewsItem newsItem) {
        Intent callingIntent = new Intent(context, NewsDetailsActivity.class);
        callingIntent.putExtra(INTENT_EXTRA_PARAM_NEWS_ITEM, newsItem);
        return callingIntent;
    }

    private NewsItem mNewsItem;
    private Context mContext;

    @Bind(R.id.textview) TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);
        mContext = this;

        this.initializeActivity(savedInstanceState);
        textView.setText(mNewsItem.toString());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
    protected void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putSerializable(INSTANCE_STATE_PARAM_NEWS_ITEM, this.mNewsItem);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * Initializes this activity.
     */
    private void initializeActivity(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            this.mNewsItem =
                    (NewsItem) getIntent().getSerializableExtra(INTENT_EXTRA_PARAM_NEWS_ITEM);
        } else {
            this.mNewsItem =
                    (NewsItem) savedInstanceState.getSerializable(INSTANCE_STATE_PARAM_NEWS_ITEM);
        }
    }

    private Context getContext(){
        return mContext;
    }
}
