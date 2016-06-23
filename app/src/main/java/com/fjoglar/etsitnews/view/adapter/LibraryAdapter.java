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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fjoglar.etsitnews.R;
import com.fjoglar.etsitnews.view.navigation.Navigator;

import java.security.InvalidParameterException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LibraryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_INTRO = 0;
    private static final int VIEW_TYPE_LIBRARY = 1;
    private final Library[] libs = {
            new Library("Android support libs",
                    "https://android.googlesource.com/platform/frameworks/support/",
                    R.drawable.library_android_support),
            new Library("ButterKnife",
                    "http://jakewharton.github.io/butterknife/",
                    R.drawable.library_butterknife),
            new Library("JSoup",
                    "https://github.com/jhy/jsoup/",
                    R.drawable.library_jsoup),
            new Library("Retrofit",
                    "http://square.github.io/retrofit/",
                    R.drawable.library_retrofit),
            new Library("Android Architecture Blueprints",
                    "https://github.com/googlesamples/android-architecture/tree/todo-mvp-clean/",
                    R.drawable.library_architecture_sample),
            new Library("Plaid",
                    "https://github.com/nickbutcher/plaid",
                    R.drawable.library_plaid),
            new Library("Google I/O",
                    "https://github.com/google/iosched",
                    R.drawable.library_google_io)};

    public LibraryAdapter() {
        // empty constructor.
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_INTRO:
                return new LibraryIntroHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.about_libs_intro, parent, false));
            case VIEW_TYPE_LIBRARY:
                return new LibraryHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.about_libs_item, parent, false));
        }
        throw new InvalidParameterException();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_LIBRARY) {
            bindLibrary((LibraryHolder) holder, libs[position - 1]); // adjust for intro
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_INTRO : VIEW_TYPE_LIBRARY;
    }

    @Override
    public int getItemCount() {
        return libs.length + 1;
    }

    private void bindLibrary(final LibraryHolder holder, final Library lib) {
        holder.libraryName.setText(lib.name);
        holder.libraryImage.setImageResource(lib.image);
        final View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigator.getInstance().openUrl(holder.libraryLink.getContext(), lib.link);
            }
        };
        holder.itemView.setOnClickListener(clickListener);
        holder.libraryLink.setOnClickListener(clickListener);
    }

    final static class LibraryHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.library_image) ImageView libraryImage;
        @BindView(R.id.library_name) TextView libraryName;
        @BindView(R.id.library_link) Button libraryLink;

        public LibraryHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    final static class LibraryIntroHolder extends RecyclerView.ViewHolder {

        TextView intro;

        public LibraryIntroHolder(View itemView) {
            super(itemView);
            intro = (TextView) itemView;
        }
    }

    private class Library {
        public final String name;
        public final String link;
        public final int image;

        public Library(String name, String link, int image) {
            this.name = name;
            this.link = link;
            this.image = image;
        }
    }

}