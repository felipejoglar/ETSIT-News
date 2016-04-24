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
import com.fjoglar.etsitnews.domain.error.DataNotAvailableError;
import com.fjoglar.etsitnews.model.entities.NewsItem;
import com.fjoglar.etsitnews.model.repository.NewsDataSource;

import java.util.List;

public class GetNews extends UseCase<GetNews.RequestValues, GetNews.ResponseValue> {

    private final NewsDataSource mNewsDataSource;

    public GetNews(NewsDataSource mNewsDataSource) {
        this.mNewsDataSource = mNewsDataSource;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        if (requestValues.isForceUpdate()) {
            mNewsDataSource.updateNews();
        }

        mNewsDataSource.getAllNews(
                new NewsDataSource.LoadNewsCallback() {
                    @Override
                    public void onNewsLoaded(List<NewsItem> newsItemList) {
                        ResponseValue responseValue = new ResponseValue(newsItemList);
                        getUseCaseCallback().onSuccess(responseValue);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        getUseCaseCallback().onError(new DataNotAvailableError());
                    }
                });
    }

    public static class RequestValues extends UseCase.RequestValues {
        private final boolean mForceUpdate;

        public RequestValues(boolean forceUpdate) {
            this.mForceUpdate = forceUpdate;
        }

        public boolean isForceUpdate() {
            return mForceUpdate;
        }
    }

    public static class ResponseValue extends UseCase.ResponseValue {
        private List<NewsItem> mNewsItemList;

        public ResponseValue(List<NewsItem> newsItemList) {
            mNewsItemList = newsItemList;
        }

        public List<NewsItem> getNewsItemList() {
            return mNewsItemList;
        }
    }

}
