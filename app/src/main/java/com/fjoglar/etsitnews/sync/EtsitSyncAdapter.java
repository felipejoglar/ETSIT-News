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
package com.fjoglar.etsitnews.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.fjoglar.etsitnews.domain.UseCase;
import com.fjoglar.etsitnews.domain.UseCaseHandler;
import com.fjoglar.etsitnews.domain.usecase.UpdateNews;
import com.fjoglar.etsitnews.model.repository.NewsRepository;

public class EtsitSyncAdapter extends AbstractThreadedSyncAdapter {

    public EtsitSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {

        UseCaseHandler useCaseHandler = UseCaseHandler.getInstance();

        UpdateNews updateNews = new UpdateNews(NewsRepository.getInstance());
        useCaseHandler.execute(updateNews, new UpdateNews.RequestValues(),
                new UseCase.UseCaseCallback<UpdateNews.ResponseValue>() {
                    @Override
                    public void onSuccess(UpdateNews.ResponseValue response) {
                        // TODO: Update last sync time and check if notification is needed.
                    }

                    @Override
                    public void onError() {
                        // Do nothing, it's background sync.
                    }
                });

    }

}
