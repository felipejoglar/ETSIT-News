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
package com.fjoglar.etsitnews.view.navigation;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.fjoglar.etsitnews.R;
import com.fjoglar.etsitnews.view.activities.BookmarksListActivity;
import com.fjoglar.etsitnews.view.activities.NewsDetailsActivity;
import com.fjoglar.etsitnews.view.activities.NewsListActivity;
import com.fjoglar.etsitnews.view.activities.SettingsActivity;

/**
 * Class used to navigate through the application.
 */
public class Navigator {

    // This is a singleton
    private static volatile Navigator sNavigator;

    public Navigator() {
        //empty
    }

    public static Navigator getInstance() {
        if (sNavigator == null) {
            sNavigator = new Navigator();
        }

        return sNavigator;
    }

    /**
     * Goes to the news list screen.
     *
     * @param context A Context needed to open the destiny activity.
     */
    public void navigateToNewsList(Context context) {
        if (context != null) {
            Intent intentToLaunch = NewsListActivity.getCallingIntent(context);
            intentToLaunch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intentToLaunch);
        }
    }

    /**
     * Goes to the bookmarks list screen.
     *
     * @param context A Context needed to open the destiny activity.
     */
    public void navigateToBookmarksList(Context context) {
        if (context != null) {
            Intent intentToLaunch = BookmarksListActivity.getCallingIntent(context);
            context.startActivity(intentToLaunch);
        }
    }

    /**
     * Goes to the news details screen.
     *
     * @param context A Context needed to open the destiny activity.
     */
    public void navigateToNewsDetails(Context context, long date, String source) {
        if (context != null) {
            Intent intentToLaunch = NewsDetailsActivity.getCallingIntent(context, date, source);
            context.startActivity(intentToLaunch);
        }
    }

    /**
     * Goes to the Settings screen.
     *
     * @param context A Context needed to open the destiny activity.
     */
    public void navigateToSettings(Context context) {
        if (context != null) {
            Intent intentToLaunch = SettingsActivity.getCallingIntent(context);
            context.startActivity(intentToLaunch);
        }
    }

    /**
     * Opens an URL in a Chrome Custom tab.
     *
     * @param context   A Context needed to open the destiny activity.
     * @param url       The URL to open.
     */
    public void openUrl (Context context, String url) {
        final String EXTRA_SESSION =
                "android.support.customtabs.extra.SESSION";
        final String EXTRA_TOOLBAR_COLOR =
                "android.support.customtabs.extra.TOOLBAR_COLOR";
        final String EXTRA_CLOSE_BUTTON_ICON =
                "android.support.customtabs.extra.CLOSE_BUTTON_ICON";
        final String EXTRA_TITLE_VISIBILITY_STATE =
                "android.support.customtabs.extra.TITLE_VISIBILITY";

        final int SHOW_PAGE_TITLE = 1;

        Intent downloadIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Bundle extras = new Bundle();
            extras.putBinder(EXTRA_SESSION, null);
            downloadIntent.putExtras(extras);
        }

        downloadIntent.putExtra(EXTRA_TOOLBAR_COLOR,
                context.getResources().getColor(R.color.colorPrimary));
        downloadIntent.putExtra(EXTRA_CLOSE_BUTTON_ICON,
                BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_arrow_back));
        downloadIntent.putExtra(EXTRA_TITLE_VISIBILITY_STATE, SHOW_PAGE_TITLE);

        if (downloadIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(downloadIntent);
        }
    }

}
