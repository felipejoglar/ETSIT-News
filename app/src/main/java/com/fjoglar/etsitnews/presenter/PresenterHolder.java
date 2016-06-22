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

import java.util.HashMap;
import java.util.Map;

public class PresenterHolder {

    static volatile PresenterHolder INSTANCE = null;
    private Map<Class, BasePresenter> presenterMap;

    public static PresenterHolder getInstance() {
        if (INSTANCE == null) {
            synchronized (PresenterHolder.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PresenterHolder();
                }
            }
        }
        return INSTANCE;
    }

    private PresenterHolder() {
        this.presenterMap = new HashMap<>();
    }

    public void putPresenter(Class c, BasePresenter p) {
        presenterMap.put(c, p);
    }

    public <T extends BasePresenter> T getPresenter(Class c) {
        return (T) presenterMap.get(c);
    }

    public void remove(Class c) {
        presenterMap.remove(c);
    }

}
