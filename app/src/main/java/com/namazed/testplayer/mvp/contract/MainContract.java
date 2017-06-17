package com.namazed.testplayer.mvp.contract;

import com.namazed.testplayer.mvp.base.MvpPresenter;
import com.namazed.testplayer.mvp.base.MvpView;

import java.util.List;

import okhttp3.ResponseBody;

public interface MainContract {

    interface View extends MvpView {
        void showProgress(boolean isShow);

        void showData(List<String> urls);

        void showData(String musicPath, int position);

        void showError();

        void showSuccessLoad();

        void showInitialData(String path, int position);

        void writeDataIntoFile(ResponseBody responseBody, String fileName);

        void playMusic(String pathOfMusic);
    }

    interface Presenter extends MvpPresenter<View> {
        void loadUrlsOfMusic();

        void loadMusic(List<String> musicsPath);

        void getPathOfMusic(int positionOfMusic);

        /**
         * Checking dataSourceMusic on null
         * @param dataSourceMusic - path of music
         */
        void checkDataSource(String dataSourceMusic);
    }
}
