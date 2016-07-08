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
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;

import com.fjoglar.etsitnews.R;
import com.fjoglar.etsitnews.data.repository.NewsDataSource;
import com.fjoglar.etsitnews.data.repository.NewsRepository;
import com.fjoglar.etsitnews.data.repository.datasource.NewsSharedPreferences;
import com.fjoglar.etsitnews.view.notification.Notification;

public class EtsitSyncAdapter extends AbstractThreadedSyncAdapter {

    // Interval at which to sync with the weather, in seconds.
    // One hour interval.
    public static final int SYNC_INTERVAL = 60 * 60;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    public EtsitSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {

        NewsDataSource newsDataSource = NewsRepository.getInstance();
        newsDataSource.updateNews(new NewsDataSource.UpdateNewsCallback() {
            @Override
            public void onUpdateFinished(Boolean isUpdated) {
                Notification.createNotification(getContext());

                // Update last updated time in SharedPreferences.
                NewsSharedPreferences newsSharedPreferences = NewsSharedPreferences.getInstance();
                newsSharedPreferences.put(
                        newsSharedPreferences.getStringFromResId(R.string.pref_last_updated_key),
                        System.currentTimeMillis());
            }
        });
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet. If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        NewsSharedPreferences newsSharedPreferences = NewsSharedPreferences.getInstance();
        int syncPeriod = newsSharedPreferences.get(
                newsSharedPreferences.getStringFromResId(R.string.pref_sync_frequency_key),
                1);

        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval * syncPeriod, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval * syncPeriod);
        }
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
        configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(
                newAccount, context.getString(R.string.content_authority), true);
    }

}
