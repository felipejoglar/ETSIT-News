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
package com.fjoglar.etsitnoticias.view.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.fjoglar.etsitnoticias.R;
import com.fjoglar.etsitnoticias.data.entities.NewsItem;
import com.fjoglar.etsitnoticias.data.repository.NewsDataSource;
import com.fjoglar.etsitnoticias.data.repository.NewsRepository;
import com.fjoglar.etsitnoticias.data.repository.datasource.NewsSharedPreferences;
import com.fjoglar.etsitnoticias.view.activities.NewsListActivity;

import java.util.ArrayList;
import java.util.List;

public class Notification {

    private static final int ETSIT_NEWS_NOTIFICATION_ID = 8008;

    private static List<NewsItem> mUpdatedNewsList;
    private static List<String> mNotificationText = new ArrayList<>();
    private static List<String> mNotificationDesc = new ArrayList<>();
    private static int mNewsCount;

    public static void createNotification(Context context) {
        if (needToNotify()) {

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

            if (mNewsCount == 1) {
                NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
                bigTextStyle.bigText(mNotificationDesc.get(0))
                        .setSummaryText(context.getResources().getString(R.string.app_name));

                // Create the notification.
                mBuilder.setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(mNotificationText.get(0))
                        .setContentText(mNotificationDesc.get(0))
                        .setColor(context.getResources().getColor(R.color.colorPrimary))
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setAutoCancel(true)
                        .setStyle(bigTextStyle);
            } else {
                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                inboxStyle.setBigContentTitle(context.getResources().getString(R.string.app_name));
                inboxStyle.setSummaryText("Tienes " + mNewsCount + " noticias nuevas.");
                for (String notificationTxt : mNotificationText) {
                    inboxStyle.addLine(notificationTxt);
                }

                // Create the notification.
                mBuilder.setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(context.getResources().getString(R.string.app_name))
                        .setContentText("Tienes " + mNewsCount + " noticias nuevas.")
                        .setColor(context.getResources().getColor(R.color.colorPrimary))
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setAutoCancel(true)
                        .setStyle(inboxStyle);
            }

            Intent resultIntent = new Intent(context, NewsListActivity.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(ETSIT_NEWS_NOTIFICATION_ID, mBuilder.build());
        }

        // Clear values for next notifications.
        mNewsCount = 0;
        mNotificationText.clear();
    }

    private static boolean needToNotify() {
        NewsSharedPreferences newsSharedPreferences = NewsSharedPreferences.getInstance();
        boolean isNotificationOn = newsSharedPreferences.getBoolean(
                newsSharedPreferences.getStringFromResId(R.string.pref_enable_notifications_key),
                true);
        if (isNotificationOn) {
            long lastUpdatedTime = newsSharedPreferences.getLong(
                    newsSharedPreferences.getStringFromResId(R.string.pref_last_updated_key), 0L);

            NewsDataSource newsDataSource = NewsRepository.getInstance();
            newsDataSource.getAllNews(new NewsDataSource.LoadNewsCallback() {
                @Override
                public void onNewsLoaded(List<NewsItem> newsItemList) {
                    mUpdatedNewsList = newsItemList;
                }

                @Override
                public void onDataNotAvailable() {

                }
            });

            for (NewsItem newsItem : mUpdatedNewsList) {
                if (newsItem.getFormattedPubDate() > lastUpdatedTime) {
                    mNotificationText.add(newsItem.getTitle());
                    mNotificationDesc.add(newsItem.getDescription());
                    mNewsCount += 1;
                }
            }
        }

        return mNewsCount > 0;
    }

}
