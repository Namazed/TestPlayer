package com.namazed.testplayer.data;

import android.support.annotation.Nullable;

import java.util.LinkedHashSet;

public interface PreferenceDataManager {

    void setMusicsPaths(LinkedHashSet<String> names);

    @Nullable
    LinkedHashSet<String> getMusicsPaths();
}
