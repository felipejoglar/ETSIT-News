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
import com.fjoglar.etsitnews.data.entities.NewsItem;
import com.fjoglar.etsitnews.data.repository.NewsDataSource;

import java.util.List;

public class GetFilteredBookmarks extends UseCase<GetFilteredBookmarks.RequestValues, GetFilteredBookmarks.ResponseValue> {

    private final NewsDataSource mNewsDataSource;

    public GetFilteredBookmarks(NewsDataSource mNewsDataSource) {
        this.mNewsDataSource = mNewsDataSource;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mNewsDataSource.getFilteredBookmarks(
                requestValues.getFilterKeysList(),
                new NewsDataSource.LoadNewsCallback() {
                    @Override
                    public void onNewsLoaded(List<NewsItem> newsItemList) {
                        ResponseValue responseValue = new ResponseValue(newsItemList);
                        getUseCaseCallback().onSuccess(responseValue);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        getUseCaseCallback().onError();
                    }
                });
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private List<String> mFilterKeysList;

        public RequestValues(List<String> filterKeysList) {
            mFilterKeysList = filterKeysList;
        }

        public List<String> getFilterKeysList() {
            return mFilterKeysList;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        private List<NewsItem> mNewsItemFilteredList;

        public ResponseValue(List<NewsItem> newsItemFilteredList) {
            mNewsItemFilteredList = newsItemFilteredList;
        }

        public List<NewsItem> getNewsItemFilteredList() {
            return mNewsItemFilteredList;
        }
    }

}
