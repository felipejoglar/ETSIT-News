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
package com.fjoglar.etsitnews.domain.usecase;

import com.fjoglar.etsitnews.domain.UseCase;
import com.fjoglar.etsitnews.model.entities.NewsItem;
import com.fjoglar.etsitnews.model.repository.NewsDataSource;

public class GetNewsItemByDate
        extends UseCase<GetNewsItemByDate.RequestValues, GetNewsItemByDate.ResponseValue> {

    private final NewsDataSource mNewsDataSource;

    public GetNewsItemByDate(NewsDataSource mNewsDataSource) {
        this.mNewsDataSource = mNewsDataSource;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mNewsDataSource.getNewsItemByDate(requestValues.getNewsItemPubDate(),
                new NewsDataSource.GetNewsItemCallback() {
                    @Override
                    public void onNewsItemLoaded(NewsItem newsItem) {
                        ResponseValue responseValue = new ResponseValue(newsItem);
                        getUseCaseCallback().onSuccess(responseValue);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        getUseCaseCallback().onError();
                    }
                });
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final long mNewsItemPubDate;

        public RequestValues(long newsItemPubDate) {
            this.mNewsItemPubDate = newsItemPubDate;
        }

        public long getNewsItemPubDate() {
            return mNewsItemPubDate;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        private NewsItem mNewsItem;

        public ResponseValue(NewsItem newsItem) {
            mNewsItem = newsItem;
        }

        public NewsItem getNewsItem() {
            return mNewsItem;
        }
    }

}
