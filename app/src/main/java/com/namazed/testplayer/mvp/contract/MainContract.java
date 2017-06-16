package com.namazed.testplayer.mvp.contract;

import com.namazed.testplayer.mvp.base.MvpPresenter;
import com.namazed.testplayer.mvp.base.MvpView;

import java.util.List;

public interface MainContract {

    interface View extends MvpView {
        void showProgress(boolean isShow);

        void showData(List<String> urls);

        void showData(String name, int position);

        void showError();

        void showSuccessLoad();
    }

    interface Presenter extends MvpPresenter<View> {
        void loadUrlsOfMusic();

        void loadMusic(List<String> musicsPath);
    }
}
