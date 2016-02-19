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
package com.fjoglar.etsitnews.interactor;

import com.fjoglar.etsitnews.executor.MainThread;
import com.fjoglar.etsitnews.model.entities.NewsItem;
import com.fjoglar.etsitnews.repository.NewsRepository;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * This interactor handles getting all news from the web.
 * News should be sorted by date with the most recent one coming first and the oldest
 * one coming last.
 */
public class GetNewsInteractorImpl extends UseCase implements GetNewsInteractor {

    private Callback mCallback;
    private NewsRepository mNewsRepository;

    public GetNewsInteractorImpl(Executor threadExecutor, MainThread mainThread,
                                 NewsRepository newsRepository,
                                 Callback callback) {
        super(threadExecutor, mainThread);

        if (newsRepository == null || callback == null) {
            throw new IllegalArgumentException("Arguments can not be null!");
        }

        mNewsRepository = newsRepository;
        mCallback = callback;
    }

    @Override
    public void run() {
        // Get the news form the web.
        final List<NewsItem> newsItemList = mNewsRepository.getAllNews();
        // Show news on the main thread.
        mMainThread.post(new Runnable() {
            @Override
            public void run() {
                mCallback.onNewsRetrieved(newsItemList);
            }
        });
    }
}
