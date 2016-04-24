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

public class UpdateNews extends UseCase<UpdateNews.RequestValues, UpdateNews.ResponseValue> {

    private final NewsRepository mNewsRepository;

    public UpdateNews(NewsRepository mNewsRepository) {
        this.mNewsRepository = mNewsRepository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mNewsRepository.updateNews();
    }

    public static class RequestValues extends UseCase.RequestValues {
    }

    public static class ResponseValue extends UseCase.ResponseValue {
    }

}
