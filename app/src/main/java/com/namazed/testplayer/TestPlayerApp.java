package com.namazed.testplayer;

import android.app.Application;

import com.namazed.testplayer.network.LinksService;
import com.namazed.testplayer.network.RetrofitService;

import timber.log.Timber;

public class TestPlayerApp extends Application {

    private RetrofitService retrofitService;
    private LinksService linksService;

    @Override
    public void onCreate() {
        retrofitService = new RetrofitService();
        linksService = retrofitService.createRetrofitClient(LinksService.class);
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public LinksService getLinksService() {
        return linksService;
    }
}
