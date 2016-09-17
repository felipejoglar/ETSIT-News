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

import android.animation.AnimatorSet;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsListItemAnimator extends DefaultItemAnimator {

    Map<RecyclerView.ViewHolder, AnimatorSet> bookmarkAnimationsMap = new HashMap<>();

    @Override
    public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder) {
        return true;
    }

    @NonNull
    @Override
    public ItemHolderInfo recordPreLayoutInformation(@NonNull RecyclerView.State state,
                                                     @NonNull RecyclerView.ViewHolder viewHolder,
                                                     int changeFlags,
                                                     @NonNull List<Object> payloads) {

        BookmarkStateInfo bookmarkStateInfo = new BookmarkStateInfo();
        bookmarkStateInfo.setFrom(viewHolder);

        return bookmarkStateInfo;
    }

    @NonNull
    @Override
    public ItemHolderInfo recordPostLayoutInformation(@NonNull RecyclerView.State state,
                                                      @NonNull RecyclerView.ViewHolder viewHolder) {
        BookmarkStateInfo bookmarkStateInfo = new BookmarkStateInfo();
        bookmarkStateInfo.setFrom(viewHolder);

        return bookmarkStateInfo;
    }

    @Override
    public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder,
                                 @NonNull final RecyclerView.ViewHolder newHolder,
                                 @NonNull ItemHolderInfo preInfo,
                                 @NonNull ItemHolderInfo postInfo) {

        cancelCurrentAnimationIfExists(newHolder);

        if (preInfo instanceof BookmarkStateInfo) {
            BookmarkStateInfo bookmarkStateInfo = (BookmarkStateInfo) preInfo;
            NewsListAdapter.NewsViewHolder holder = (NewsListAdapter.NewsViewHolder) newHolder;

            if (bookmarkStateInfo.getBookmarkTag().equals(NewsListAdapter.BOOKMARK_OFF_TAG)) {
                holder.bookmark.animateBookmarkView();
            }
        }

        return false;
    }

    private class BookmarkStateInfo extends ItemHolderInfo {
        String bookmarkTag;

        @Override
        public ItemHolderInfo setFrom(RecyclerView.ViewHolder holder) {
            if (holder instanceof NewsListAdapter.NewsViewHolder) {
                NewsListAdapter.NewsViewHolder newsViewHolder = (NewsListAdapter.NewsViewHolder) holder;
                bookmarkTag = (String) newsViewHolder.bookmark.getTag();
            }
            return super.setFrom(holder);
        }

        public String getBookmarkTag() {
            return bookmarkTag;
        }
    }

    private void cancelCurrentAnimationIfExists(RecyclerView.ViewHolder holder) {
        if (bookmarkAnimationsMap.containsKey(holder)) {
            bookmarkAnimationsMap.get(holder).cancel();
        }
    }

}

