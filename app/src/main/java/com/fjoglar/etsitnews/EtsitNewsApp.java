package com.fjoglar.etsitnews;

import android.app.Application;

import com.fjoglar.etsitnews.repository.NewsRepositoryImpl;

public class EtsitNewsApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Set the Context for Repository.
        NewsRepositoryImpl.getInstance().setContext(this);
    }
}
