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
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.fjoglar.etsitnews.R;
import com.fjoglar.etsitnews.view.adapter.AboutViewPagerAdapter;
import com.fjoglar.etsitnews.view.widget.DotsPageIndicator;
import com.fjoglar.etsitnews.view.widget.InkPageIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AboutActivity extends AppCompatActivity {

    private Context mContext;

    @BindView(R.id.frame_layout) FrameLayout frameLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.pager) ViewPager pager;
    @BindView(R.id.page_indicator) View viewIndicator;

    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        unbinder = ButterKnife.bind(this);
        mContext = this;

        initializeActivity();
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
        unbinder.unbind();
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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
    }

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, AboutActivity.class);
    }

    private void initializeActivity() {
        setUpToolbar();
        setUpViewPager();
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setTitle(R.string.about_activity_title);
    }

    private void setUpViewPager() {
        pager.setAdapter(new AboutViewPagerAdapter(this));

        if (viewIndicator instanceof InkPageIndicator) {
            InkPageIndicator inkPageIndicator = (InkPageIndicator) viewIndicator;
            inkPageIndicator.setViewPager(pager);

        } else {
            DotsPageIndicator circlePageIndicator = (DotsPageIndicator) viewIndicator;
            circlePageIndicator.setViewPager(pager);
        }
    }

    private Context getContext() {
        return mContext;
    }

}