package com.namazed.testplayer.data;


import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedHashSet;

public class DefaultPreferenceDataManager implements PreferenceDataManager {

    private static final String PATHS_KEY = "paths";

    private final SharedPreferences sharedPreferences;
    private final Gson gson;
    private final Type type;

    public DefaultPreferenceDataManager(SharedPreferences preferences) {
        sharedPreferences = preferences;
        gson = new Gson();
        type = new TypeToken<LinkedHashSet<String>>(){}.getType();
    }

    @Override
    public void setMusicsPaths(LinkedHashSet<String> names) {
        String jsonLinkedSet = gson.toJson(names, type);
        sharedPreferences.edit().putString(PATHS_KEY, jsonLinkedSet).apply();
    }

    @Nullable
    @Override
    public LinkedHashSet<String> getMusicsPaths() {
        return gson.fromJson(sharedPreferences.getString(PATHS_KEY, null), type);
    }
}
