package com.namazed.testplayer.mvp.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.namazed.testplayer.TestPlayerApp;

public abstract class BaseActivity<V extends MvpView, P extends MvpPresenter<V>>
        extends AppCompatActivity implements MvpView {

    private P mvpPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onCreatePresenter(P mvpPresenter, V view) {
        this.mvpPresenter = mvpPresenter;
        this.mvpPresenter.attachView(view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mvpPresenter != null) {
            mvpPresenter.detachView();
        }
    }

    @Override
    public TestPlayerApp getTestPlayerApp() {
        return (TestPlayerApp) getApplication();
    }

    public P getPresenter() {
        return mvpPresenter;
    }
}
