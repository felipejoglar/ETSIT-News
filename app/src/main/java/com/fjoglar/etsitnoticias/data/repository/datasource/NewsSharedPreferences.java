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
package com.fjoglar.etsitnoticias.data.repository.datasource;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Helper class to make simple the use of SharedPreferences.
 */
public class NewsSharedPreferences {

    private static volatile NewsSharedPreferences INSTANCE;
    private Context mContext;

    private NewsSharedPreferences() {

    }

    public static NewsSharedPreferences getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NewsSharedPreferences();
        }
        return INSTANCE;
    }

    // Put and Get SharedPreferences Strings.
    public void putString(String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (value == null) {
            prefs.edit().putString(key, null).apply();
        } else {
            prefs.edit().putString(key, value).apply();
        }
    }

    public String getString(String key, String defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return prefs.getString(key, defaultValue);
    }

    // Put and Get SharedPreferences Booleans.
    public void putBoolean(String key, Boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        prefs.edit().putBoolean(key, value).apply();
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return prefs.getBoolean(key, defaultValue);
    }

    // Put and Get SharedPreferences ints.
    public void putInt(String key, int value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        prefs.edit().putInt(key, value).apply();
    }

    public int getInt(String key, int defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return prefs.getInt(key, defaultValue);
    }

    // Put and Get SharedPreferences longs.
    public void putLong(String key, long value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        prefs.edit().putLong(key, value).apply();
    }

    public long getLong(String key, long defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return prefs.getLong(key, defaultValue);
    }

    public String getStringFromResId (int ResId) {
        return mContext.getString(ResId);
    }

    public void setContext(Context context) {
        mContext = context;
    }
}