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

import com.fjoglar.etsitnews.model.entities.NewsItem;
import com.fjoglar.etsitnews.presenter.base.Presenter;
import com.fjoglar.etsitnews.view.BaseView;

public interface NewsDetailsPresenter extends Presenter {
    interface View extends BaseView {
        void showNewsItem(NewsItem newsItem);

        void updateBookmarkIcon(boolean isBookmarked);
    }

    void getNewsItemByDate(long date);

    void manageBookmark(NewsItem newsItem);
}
