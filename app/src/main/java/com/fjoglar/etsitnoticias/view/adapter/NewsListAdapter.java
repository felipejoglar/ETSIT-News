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
package com.fjoglar.etsitnoticias.view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fjoglar.etsitnoticias.R;
import com.fjoglar.etsitnoticias.data.entities.NewsItem;
import com.fjoglar.etsitnoticias.utils.CategoryUtils;
import com.fjoglar.etsitnoticias.utils.DateUtils;
import com.fjoglar.etsitnoticias.view.widget.bookmark.BookmarkButtonView;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.NewsViewHolder> {

    private final String LOG_TAG = NewsListAdapter.class.getSimpleName();

    private List<NewsItem> mNewsItemList;
    private OnItemClickListener mOnItemClickListener;
    private OnBookmarkClickListener mOnBookmarkClickListener;

    public NewsListAdapter(@NonNull OnItemClickListener onItemClickListener,
                           @NonNull OnBookmarkClickListener onBookmarkClickListener) {
        this.mNewsItemList = Collections.emptyList();
        this.mOnItemClickListener = onItemClickListener;
        this.mOnBookmarkClickListener = onBookmarkClickListener;
    }

    @Override
    public NewsListAdapter.NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_news_list, parent, false);
        return new NewsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final NewsListAdapter.NewsViewHolder holder, final int position) {
        final NewsItem item = mNewsItemList.get(position);

        holder.title.setText(item.getTitle());
        holder.date.setText(DateUtils.formatListViewTime(item.getFormattedPubDate()));
        if (!TextUtils.isEmpty(item.getDescription())) {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(item.getDescription().replaceAll("\\n", ""));
        } else {
            holder.description.setVisibility(View.GONE);
        }
        holder.category.setText(CategoryUtils.categoryToString(holder.category.getContext(),
                item.getCategory()));
        holder.bookmark.setImage(item.getBookmarked());
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClicked(item.getFormattedPubDate());
            }
        });
        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.bookmark.onClick(v);
                mOnBookmarkClickListener.onBookmarkClicked(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNewsItemList.size();
    }

    public void setNewsListAdapter(List<NewsItem> newsItemList) {
        this.mNewsItemList = newsItemList;
    }

    final static class NewsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_title) TextView title;
        @BindView(R.id.item_date) TextView date;
        @BindView(R.id.item_description) TextView description;
        @BindView(R.id.item_category) TextView category;
        @BindView(R.id.item_bookmark) BookmarkButtonView bookmark;

        public NewsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setOnClickListener(View.OnClickListener listener) {
            itemView.setOnClickListener(listener);
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(long date);
    }

    public interface OnBookmarkClickListener {
        void onBookmarkClicked(NewsItem newsItem);
    }

}
