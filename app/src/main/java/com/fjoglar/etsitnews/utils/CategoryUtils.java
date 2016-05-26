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
package com.fjoglar.etsitnews.utils;

import android.content.Context;

import com.fjoglar.etsitnews.R;
import com.fjoglar.etsitnews.model.entities.Category;
import com.fjoglar.etsitnews.model.repository.datasource.NewsSharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class CategoryUtils {

    public static List<Category> createCategoryList () {
        List<Category> categories = new ArrayList<>();

        NewsSharedPreferences newsSharedPreferences = NewsSharedPreferences.getInstance();

        categories.add(new Category(
                newsSharedPreferences.getStringFromResId(R.string.filter_1),
                "12",
                newsSharedPreferences.get(
                        newsSharedPreferences.getStringFromResId(R.string.pref_filter_1_key),
                        true)));
        categories.add(new Category(
                newsSharedPreferences.getStringFromResId(R.string.filter_2),
                "1,2",
                newsSharedPreferences.get(
                        newsSharedPreferences.getStringFromResId(R.string.pref_filter_2_key),
                        true)));
        categories.add(new Category(
                newsSharedPreferences.getStringFromResId(R.string.filter_3),
                "11",
                newsSharedPreferences.get(
                        newsSharedPreferences.getStringFromResId(R.string.pref_filter_3_key),
                        true)));
        categories.add(new Category(
                newsSharedPreferences.getStringFromResId(R.string.filter_4),
                "3,4",
                newsSharedPreferences.get(
                        newsSharedPreferences.getStringFromResId(R.string.pref_filter_4_key),
                        true)));
        categories.add(new Category(
                newsSharedPreferences.getStringFromResId(R.string.filter_5),
                "5",
                newsSharedPreferences.get(
                        newsSharedPreferences.getStringFromResId(R.string.pref_filter_5_key),
                        true)));
        categories.add(new Category(
                newsSharedPreferences.getStringFromResId(R.string.filter_6),
                "15",
                newsSharedPreferences.get(
                        newsSharedPreferences.getStringFromResId(R.string.pref_filter_6_key),
                        true)));
        categories.add(new Category(
                newsSharedPreferences.getStringFromResId(R.string.filter_7),
                "16",
                newsSharedPreferences.get(
                        newsSharedPreferences.getStringFromResId(R.string.pref_filter_7_key),
                        true)));
        categories.add(new Category(
                newsSharedPreferences.getStringFromResId(R.string.filter_8),
                "6,7,8,9,10,13,14",
                newsSharedPreferences.get(
                        newsSharedPreferences.getStringFromResId(R.string.pref_filter_8_key),
                        true)));

        return categories;
    }

    public static void updateCategoryFilterStatus(Category category) {
        NewsSharedPreferences newsSharedPreferences = NewsSharedPreferences.getInstance();

        switch (category.getCategoriesNumber()){
            case "12":
                newsSharedPreferences.put(
                        newsSharedPreferences.getStringFromResId(R.string.pref_filter_1_key),
                        !category.isEnabled());
                break;
            case "1,2":
                newsSharedPreferences.put(
                        newsSharedPreferences.getStringFromResId(R.string.pref_filter_2_key),
                        !category.isEnabled());
                break;
            case "11":
                newsSharedPreferences.put(
                        newsSharedPreferences.getStringFromResId(R.string.pref_filter_3_key),
                        !category.isEnabled());
                break;
            case "3,4":
                newsSharedPreferences.put(
                        newsSharedPreferences.getStringFromResId(R.string.pref_filter_4_key),
                        !category.isEnabled());
                break;
            case "5":
                newsSharedPreferences.put(
                        newsSharedPreferences.getStringFromResId(R.string.pref_filter_5_key),
                        !category.isEnabled());
                break;
            case "15":
                newsSharedPreferences.put(
                        newsSharedPreferences.getStringFromResId(R.string.pref_filter_6_key),
                        !category.isEnabled());
                break;
            case "16":
                newsSharedPreferences.put(
                        newsSharedPreferences.getStringFromResId(R.string.pref_filter_7_key),
                        !category.isEnabled());
                break;
            case "6,7,8,9,10,13,14":
                newsSharedPreferences.put(
                        newsSharedPreferences.getStringFromResId(R.string.pref_filter_8_key),
                        !category.isEnabled());
                break;
        }
    }

    public static boolean areAllCategoriesActive() {
        List<Category> categoryList = createCategoryList();
        for (Category category : categoryList) {
            if (!category.isEnabled()) {
                return false;
            }
        }
        return true;
    }

    public static List<String> getActiveCategories() {
        List<String> activeCategories = new ArrayList<>();
        List<Category> categoryList = createCategoryList();
        for (Category category : categoryList) {
            if (category.isEnabled()) {
                String filterKeys[] = category.getCategoriesNumber().split(",");
                for (String filterKey : filterKeys) {
                    if (!filterKey.equals("")) {
                        activeCategories.add(filterKey);
                    }
                }
            }
        }
        return activeCategories;
    }

    public static String categoryToString(Context context, String category) {
        switch (category){
            case "1":
            case "2":
                return context.getString(R.string.category_general);
            case "3":
            case "4":
                return context.getString(R.string.category_beca);
            case "5":
                return context.getString(R.string.category_tfg);
            case "11":
                return context.getString(R.string.category_conferencia);
            case "12":
                return context.getString(R.string.category_destacado);
            case "15":
                return context.getString(R.string.category_junta);
            case "16":
                return context.getString(R.string.category_investigacion);
            case "6":
            case "7":
            case "8":
            case "9":
            case "10":
            case "13":
            case "14":
                return context.getString(R.string.category_otros);
            default:
                return "";
        }
    }
}
