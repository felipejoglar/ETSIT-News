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
package com.fjoglar.etsitnews.view.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.fjoglar.etsitnews.R;
import com.fjoglar.etsitnews.view.navigation.Navigator;

import java.security.InvalidParameterException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutViewPagerAdapter extends PagerAdapter {

    private final String GITHUB_LINK = "https://github.com/fjoglar/ETSIT-News";

    private View aboutEtsitNews;
    private View aboutImages;
    private View aboutLibs;
    @Nullable @BindView(R.id.recycler_libs) RecyclerView recyclerLibs;
    @Nullable @BindView(R.id.github_link) Button githubLink;

    private final LayoutInflater layoutInflater;

    public AboutViewPagerAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        View layout = getPage(position, collection);
        collection.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private View getPage(int position, final ViewGroup parent) {
        switch (position) {
            case 0:
                if (aboutEtsitNews == null) {
                    aboutEtsitNews = layoutInflater.inflate(R.layout.about_etsit_news, parent, false);
                    ButterKnife.bind(this, aboutEtsitNews);

                    githubLink.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Navigator.getInstance().openUrl(parent.getContext(), GITHUB_LINK);
                        }
                    });
                }
                return aboutEtsitNews;
            case 1:
                if (aboutImages == null) {
                    aboutImages = layoutInflater.inflate(R.layout.about_images, parent, false);
                }
                return aboutImages;
            case 2:
                if (aboutLibs == null) {
                    aboutLibs = layoutInflater.inflate(R.layout.about_libs, parent, false);
                    ButterKnife.bind(this, aboutLibs);

                    recyclerLibs.setAdapter(new LibraryAdapter());
                }
                return aboutLibs;
        }
        throw new InvalidParameterException();
    }

}
