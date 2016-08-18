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
package com.fjoglar.etsitnoticias.presenter.contracts;

import com.fjoglar.etsitnoticias.data.entities.NewsItem;
import com.fjoglar.etsitnoticias.presenter.BasePresenter;
import com.fjoglar.etsitnoticias.view.BaseView;

public interface NewsDetailsContract {

    interface View extends BaseView<Presenter> {

        void showNewsItem(NewsItem newsItem);

        void updateBookmarkIcon(boolean isBookmarked);

        void showProgress();

        void hideProgress();

        void showError(String message);

    }

    interface Presenter extends BasePresenter {

        void getNewsItemByDate(long date, String source);

        void manageBookmark(NewsItem newsItem);

    }

}
