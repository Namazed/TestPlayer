package com.namazed.testplayer.mvp.base;

import java.lang.ref.WeakReference;

public abstract class BasePresenter<V extends MvpView> implements MvpPresenter<V> {

    private WeakReference<V> viewWeakReference;

    @Override
    public void attachView(V view) {
        viewWeakReference = new WeakReference<>(view);
    }

    @Override
    public void detachView() {
        if (viewWeakReference != null) {
            viewWeakReference.clear();
            viewWeakReference = null;
        }
    }

    protected boolean isViewAttached() {
        return viewWeakReference != null && viewWeakReference.get() != null;
    }

    protected V getView() {
        return viewWeakReference.get();
    }
}
