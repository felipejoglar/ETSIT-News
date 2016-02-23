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

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fjoglar.etsitnews.R;
import com.fjoglar.etsitnews.model.entities.NewsItem;
import com.fjoglar.etsitnews.utils.DateUtils;
import com.fjoglar.etsitnews.utils.FormatTextUtils;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.NewsViewHolder> {

    private final String LOG_TAG = NewsListAdapter.class.getSimpleName();

    private List<NewsItem> mNewsItemList;
    private ItemClickListener mItemClickListener;

    public interface ItemClickListener {
        void itemClicked(NewsItem newsItem);
    }

    public NewsListAdapter(@NonNull ItemClickListener itemClickListener) {
        this.mNewsItemList = Collections.emptyList();
        this.mItemClickListener = itemClickListener;
    }

    public void setNewsListAdapter(List<NewsItem> newsItemList) {
        this.mNewsItemList = newsItemList;
    }

    final static class NewsViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.item_title) TextView title;
        @Bind(R.id.item_date) TextView date;
        @Bind(R.id.item_description) TextView description;
        @Bind(R.id.item_category) TextView category;

        public NewsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setOnClickListener(View.OnClickListener listener) {
            itemView.setOnClickListener(listener);
        }
    }

    @Override
    public NewsListAdapter.NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_news_list, parent, false);
        return new NewsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NewsListAdapter.NewsViewHolder holder, int position) {
        final NewsItem item = mNewsItemList.get(position);

        holder.title.setText(FormatTextUtils.formatText(item.getTitle()));
        holder.date.setText(DateUtils.formatTime(item.getPubDate()));
        if (!TextUtils.isEmpty(item.getDescription())) {
            holder.description.setText(FormatTextUtils.formatText(item.getDescription()));
        } else {
            holder.description.setVisibility(View.GONE);
        }
        holder.category.setText(FormatTextUtils.categoryToString(holder.category.getContext(),
                item.getCategory()));
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.itemClicked(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNewsItemList.size();
    }

}
