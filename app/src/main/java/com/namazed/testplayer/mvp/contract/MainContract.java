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

        void pauseMusic(int currentPositionOfMusic);

        void stopMusic();
    }

    interface Presenter extends MvpPresenter<View> {
        void loadUrlsOfMusic();

        void loadMusic(List<String> musicsPath);

        void getPathOfMusic(int positionOfMusic);

        /**
         * If dataSourceMusic not null then play music
         * @param dataSourceMusic - path of music
         */
        void onClickPlayMusic(String dataSourceMusic);

        void onClickPauseMusic(int currentPositionOfMusic);

        void onClickStopMusic();
    }
}
