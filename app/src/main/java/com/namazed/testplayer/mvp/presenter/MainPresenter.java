package com.namazed.testplayer.mvp.presenter;

import com.namazed.testplayer.mvp.base.BasePresenter;
import com.namazed.testplayer.mvp.contract.MainContract;
import com.namazed.testplayer.network.LinksService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class MainPresenter extends BasePresenter<MainContract.View>
        implements MainContract.Presenter {

    private CompositeDisposable compositeDisposable;
    private LinksService linksService;

    @Override
    public void attachView(MainContract.View view) {
        super.attachView(view);
        compositeDisposable = new CompositeDisposable();
        linksService = getView().getTestPlayerApp().getLinksService();
    }

    @Override
    public void loadUrlsOfMusic() {
        if (!isViewAttached()) {
            return;
        }

        getView().showProgress(true);

        compositeDisposable.add(linksService.getFileWithUrls()
                .onErrorResumeNext(throwable -> linksService.getFileWithUrls())
                .map(responseBody -> {
                    InputStream inputStream = responseBody.byteStream();
                    BufferedReader bufferedReader =
                            new BufferedReader(new InputStreamReader(inputStream));
                    List<String> songsPath = new LinkedList<>();
                    String path;
                    while ((path = bufferedReader.readLine()) != null) {
                        songsPath.add(path);
                    }
                    inputStream.close();
                    bufferedReader.close();

                    return songsPath;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    if (getView() != null) {
                        getView().showProgress(false);
                        getView().showData(list);
                    }
                }, throwable ->{
                    Timber.d(throwable, throwable.getMessage());
                    if (getView() != null) {
                        getView().showProgress(false);
                        getView().showError();
                    }
                }));
    }

    @Override
    public void loadMusic(List<String> musicsPath) {

    }

    @Override
    public void detachView() {
        super.detachView();
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
            compositeDisposable.clear();
            compositeDisposable = null;
        }
    }
}
