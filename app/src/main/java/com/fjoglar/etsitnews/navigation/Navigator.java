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
package com.fjoglar.etsitnews.navigation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.fjoglar.etsitnews.view.activities.NewsDetailsActivity;
import com.fjoglar.etsitnews.view.activities.NewsListActivity;

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
    public void navigateToUserList(Context context) {
        if (context != null) {
            Intent intentToLaunch = NewsListActivity.getCallingIntent(context);
            context.startActivity(intentToLaunch);
        }
    }

    /**
     * Goes to the news details screen.
     *
     * @param context A Context needed to open the destiny activity.
     */
    public void navigateToUserDetails(Context context, int id) {
        if (context != null) {
            Intent intentToLaunch = NewsDetailsActivity.getCallingIntent(context, id);
            context.startActivity(intentToLaunch);
        }
    }

    /**
     * Opens an URL.
     *
     * @param context   A Context needed to open the destiny activity.
     * @param url       The URL to open.
     */
    public void openUrl (Context context, String url) {
        Intent downloadIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (downloadIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(downloadIntent);
        }
    }

}
