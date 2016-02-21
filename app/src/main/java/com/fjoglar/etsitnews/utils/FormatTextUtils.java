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

public class FormatTextUtils {

    public static String formatText(String text) {
        text = text.replaceAll("[^\\s\\p{Print}]", "")
                .replace(" ", "AuxText")
                .replace("\r\nAuxText", "\r\n")
                .replaceAll("[\\r\\n]+", "\n\n")
                .replace("AuxText", " ");

        return text;
    }

    public static String capitalizeWord(String word) {
        return Character.toUpperCase(word.charAt(0)) + word.substring(1);
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
            default:
                return context.getString(R.string.category_otros);
        }
    }

}
