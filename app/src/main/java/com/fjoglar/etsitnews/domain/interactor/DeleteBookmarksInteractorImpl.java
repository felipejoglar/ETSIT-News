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
package com.fjoglar.etsitnews.domain.interactor;

import com.fjoglar.etsitnews.domain.executor.Executor;
import com.fjoglar.etsitnews.domain.executor.MainThread;
import com.fjoglar.etsitnews.domain.interactor.base.UseCase;
import com.fjoglar.etsitnews.model.entities.NewsItem;
import com.fjoglar.etsitnews.model.repository.NewsRepository;

/**
 * This interactor is responsible for deleting the selected bookmark from DB.
 */
public class DeleteBookmarksInteractorImpl extends UseCase implements DeleteBookmarksInteractor {

    private Callback mCallback;
    private NewsRepository mNewsRepository;
    private NewsItem mNewsItem;

    public DeleteBookmarksInteractorImpl(Executor threadExecutor, MainThread mainThread,
                                         NewsRepository newsRepository,
                                         Callback callback,
                                         NewsItem newsItem) {
        super(threadExecutor, mainThread);

        if (newsRepository == null || callback == null) {
            throw new IllegalArgumentException("Arguments can not be null!");
        }

        mNewsRepository = newsRepository;
        mCallback = callback;
        mNewsItem = newsItem;
    }

    @Override
    public void run() {
        // Save the bookmarks form the db.
        mNewsRepository.deleteBookmarkByDate(mNewsItem.getFormattedPubDate());
        // Update bookmarked status in news table item.
        mNewsRepository.updateNewsItemIsBookmarkedStatusByDate(false, mNewsItem.getFormattedPubDate());
        mMainThread.post(new Runnable() {
            @Override
            public void run() {
                mCallback.onBookmarkDeleted();
            }
        });
    }
}
