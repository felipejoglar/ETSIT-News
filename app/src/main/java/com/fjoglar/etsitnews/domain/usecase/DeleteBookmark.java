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
import com.fjoglar.etsitnews.model.repository.NewsRepository;

public class DeleteBookmark
        extends UseCase<DeleteBookmark.RequestValues, DeleteBookmark.ResponseValue> {

    private final NewsRepository mNewsRepository;

    public DeleteBookmark(NewsRepository mNewsRepository) {
        this.mNewsRepository = mNewsRepository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        // Save the bookmarks form the db.
        mNewsRepository.deleteBookmarkByDate(requestValues.getNewsItemPubDate());
        // Update bookmarked status in news table item.
        mNewsRepository.updateNewsItemIsBookmarkedStatusByDate(false,
                requestValues.getNewsItemPubDate());

        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static class RequestValues extends UseCase.RequestValues {
        private final long mNewsItemPubDate;

        public RequestValues(long newsItemPubDate) {
            this.mNewsItemPubDate = newsItemPubDate;
        }

        public long getNewsItemPubDate() {
            return mNewsItemPubDate;
        }
    }

    public static class ResponseValue extends UseCase.ResponseValue {}

}
