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

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fjoglar.etsitnoticias.R;
import com.fjoglar.etsitnoticias.data.entities.NewsItem;
import com.fjoglar.etsitnoticias.utils.CategoryUtils;
import com.fjoglar.etsitnoticias.utils.DateUtils;
import com.fjoglar.etsitnoticias.utils.FormatTextUtils;
import com.fjoglar.etsitnoticias.view.widget.bookmark.BookmarkButtonView;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.NewsViewHolder> {

    public static final String BOOKMARK_ON_TAG = "bookmark_on_tag";
    public static final String BOOKMARK_OFF_TAG = "bookmark_off_tag";

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
    public void onBindViewHolder(final NewsListAdapter.NewsViewHolder holder, int position) {
        final NewsItem item = mNewsItemList.get(position);

        holder.title.setText(item.getTitle());
        holder.date.setText(DateUtils.formatListViewTime(item.getFormattedPubDate()));
        if (!TextUtils.isEmpty(item.getDescription())) {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(FormatTextUtils.formatSmallText(item.getDescription()));
        } else {
            holder.description.setVisibility(View.GONE);
        }
        holder.category.setText(CategoryUtils.categoryToString(holder.category.getContext(),
                item.getCategory()));
        holder.bookmark.setImage(item.getBookmarked());
        holder.bookmark.setTag(
                (item.getBookmarked() == 1) ? BOOKMARK_ON_TAG : BOOKMARK_OFF_TAG);
        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnBookmarkClickListener.onBookmarkClicked(item);
                item.changeBookmarkedStatus(item.getBookmarked());
                notifyItemChanged(holder.getAdapterPosition());
            }
        });
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClicked(item.getFormattedPubDate());
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
