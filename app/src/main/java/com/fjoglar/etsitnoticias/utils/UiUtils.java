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
package com.fjoglar.etsitnoticias.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fjoglar.etsitnoticias.R;
import com.fjoglar.etsitnoticias.data.entities.Attachment;
import com.fjoglar.etsitnoticias.view.navigation.Navigator;

public class UiUtils {

    /**
     * Configure the TextViews that will show the attachments of the new.
     *
     * @param textView      TextView to be configured.
     * @param title         Text shown in TextView
     * @param downloadLink  Link attached to TextView.
     * @param fileType      Type of file of the attachment.
     * @param context       The context of activity.
     */
    public static void configureTextView(TextView textView,
                                   String title,
                                   final String downloadLink,
                                   Attachment.FILE_TYPE fileType,
                                   final Context context) {

        final int TEXT_VIEW_MIN_HEIGHT = 40;
        final int TEXT_VIEW_MARGIN_TOP = 4;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, convertDpToPx(TEXT_VIEW_MARGIN_TOP, context), 0, 0);
        textView.setLayoutParams(params);

        textView.setText(title);
        textView.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
        textView.setMinHeight(convertDpToPx(TEXT_VIEW_MIN_HEIGHT, context));
        textView.setGravity(Gravity.CENTER_VERTICAL);

        switch (fileType) {
            case FILE:
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_file, 0, 0, 0);
                break;
            case IMAGE:
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_photo, 0, 0, 0);
                break;
            case FOLDER:
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_folder, 0, 0, 0);
                break;
            default:
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_link, 0, 0, 0);
                break;
        }

        textView.setCompoundDrawablePadding(convertDpToPx(4, context));

        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground,
                typedValue,
                true);
        textView.setBackgroundResource(typedValue.resourceId);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigator.getInstance().openUrl(context, downloadLink);
            }
        });
    }

    /**
     * Update the menu's bookmark icon in function of its status.
     *
     * @param menu          Menu container of MenuItem.
     * @param isBookmarked  Status of bookmark.
     * @param context       The context of activity.
     */
    public static void updateBookmarkIcon(Menu menu, boolean isBookmarked, Context context){
        if (menu != null) {
            MenuItem menuItem = menu.findItem(R.id.action_bookmark);
            if (isBookmarked) {
                menuItem.setIcon(context.getResources().getDrawable(R.drawable.ic_bookmark));
            } else {
                menuItem.setIcon(context.getResources().getDrawable(R.drawable.ic_bookmark_border));
            }
        }
    }

    /**
     * Converts dps into pixels.
     *
     * @param dp        Measure in dp.
     * @param context   The context of activity.
     * @return          Measure in px.
     */
    private static int convertDpToPx(int dp, Context context) {
        return Math.round(dp
                * (context.getResources().getDisplayMetrics().xdpi
                        / DisplayMetrics.DENSITY_DEFAULT));

    }

    /**
     * Create a circular drawable from a resource drawable id.
     *
     * @param context   Need to get the resources
     * @param resId     Drawable's id.
     *
     * @return RoundedBitmapDrawable
     */
    public static RoundedBitmapDrawable getCircleBitmapDrawable(Context context, int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory
                .create(context.getResources(), bitmap);
        drawable.setCornerRadius(Math.max(bitmap.getWidth() / 2, bitmap.getHeight() / 2));
        drawable.setAntiAlias(true);
        return drawable;
    }

    public static double mapValueFromRangeToRange(double value, double fromLow, double fromHigh, double toLow, double toHigh) {
        return toLow + ((value - fromLow) / (fromHigh - fromLow) * (toHigh - toLow));
    }

    public static double clamp(double value, double low, double high) {
        return Math.min(Math.max(value, low), high);
    }

}
