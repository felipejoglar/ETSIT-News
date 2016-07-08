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
package com.fjoglar.etsitnews.presenter;

import android.support.annotation.NonNull;

import com.fjoglar.etsitnews.R;
import com.fjoglar.etsitnews.domain.UseCaseHandler;
import com.fjoglar.etsitnews.data.repository.datasource.NewsSharedPreferences;
import com.fjoglar.etsitnews.presenter.contracts.SettingsContract;

public class SettingsPresenter implements SettingsContract.Presenter {

    private final SettingsContract.View mSettingsView;
    private final UseCaseHandler mUseCaseHandler;
    private final NewsSharedPreferences mNewsSharedPreferences;

    public SettingsPresenter(@NonNull SettingsContract.View settingsView) {
        mUseCaseHandler = UseCaseHandler.getInstance();
        mNewsSharedPreferences = NewsSharedPreferences.getInstance();
        mSettingsView = settingsView;

        mSettingsView.setPresenter(this);
    }

    @Override
    public void getCurrentSettings() {
        boolean isNotificationEnabled =
                mNewsSharedPreferences.get(
                        mNewsSharedPreferences.getStringFromResId(R.string.pref_enable_notifications_key),
                        true);
        int syncFrequencyPeriod =
                mNewsSharedPreferences.get(
                        mNewsSharedPreferences.getStringFromResId(R.string.pref_sync_frequency_key),
                        1);
        int index = getSyncFrequencyIndex(syncFrequencyPeriod);

        mSettingsView.setupSettings(
                isNotificationEnabled,
                getSyncFrequencyString(syncFrequencyPeriod),
                index);
    }

    @Override
    public void updateNotificationSettings(boolean isNotificationEnabled) {
        mNewsSharedPreferences.put(
                mNewsSharedPreferences.getStringFromResId(R.string.pref_enable_notifications_key),
                isNotificationEnabled);
    }

    @Override
    public void updateSyncFrequencySettings(int index) {
        mNewsSharedPreferences.put(
                mNewsSharedPreferences.getStringFromResId(R.string.pref_sync_frequency_key),
                getSyncFrequencyValue(index));
    }

    @Override
    public void start() {
        getCurrentSettings();
    }

    private String getSyncFrequencyString(int value) {
        switch (value){
            case 1:
                return mNewsSharedPreferences
                        .getStringFromResId(R.string.pref_sync_frequency_label_one);
            case 3:
                return mNewsSharedPreferences
                        .getStringFromResId(R.string.pref_sync_frequency_label_three);
            case 6:
                return mNewsSharedPreferences
                        .getStringFromResId(R.string.pref_sync_frequency_label_six);
            case 12:
                return mNewsSharedPreferences
                        .getStringFromResId(R.string.pref_sync_frequency_label_twelve);
            case 24:
                return mNewsSharedPreferences
                        .getStringFromResId(R.string.pref_sync_frequency_label_twenty_four);
            default:
                return "Error";
        }
    }

    private int getSyncFrequencyValue(int index) {
        switch (index){
            case 0:
                return 1;
            case 1:
                return 3;
            case 2:
                return 6;
            case 3:
                return 12;
            case 4:
                return 24;
            default:
                return 1;
        }
    }

    private int getSyncFrequencyIndex(int value) {
        switch (value){
            case 1:
                return 0;
            case 3:
                return 1;
            case 6:
                return 2;
            case 12:
                return 3;
            case 24:
                return 4;
            default:
                return 0;
        }
    }

}
