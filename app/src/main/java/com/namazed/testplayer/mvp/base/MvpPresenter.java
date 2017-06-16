package com.namazed.testplayer.mvp.base;

public interface MvpPresenter<V> {

    void attachView(V view);

    void detachView();
}
