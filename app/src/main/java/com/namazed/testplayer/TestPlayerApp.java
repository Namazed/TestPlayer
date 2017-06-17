package com.namazed.testplayer;

import android.app.Application;
import android.content.SharedPreferences;

import com.namazed.testplayer.data.DefaultPreferenceDataManager;
import com.namazed.testplayer.data.PreferenceDataManager;
import com.namazed.testplayer.network.LinksService;
import com.namazed.testplayer.network.RetrofitService;

import timber.log.Timber;

public class TestPlayerApp extends Application {

    public final static String PREFERENCES = "testPlayerPrefs";
    private RetrofitService retrofitService;
    private LinksService linksService;
    private SharedPreferences sharedPreferences;
    private PreferenceDataManager preferenceDataManager;

    @Override
    public void onCreate() {
        sharedPreferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        preferenceDataManager = new DefaultPreferenceDataManager(sharedPreferences);

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

    public PreferenceDataManager getPreferenceDataManager() {
        return preferenceDataManager;
    }
}
