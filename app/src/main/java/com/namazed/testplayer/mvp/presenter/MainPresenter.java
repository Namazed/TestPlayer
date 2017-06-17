package com.namazed.testplayer.mvp.presenter;

import android.support.v4.util.Pair;

import com.namazed.testplayer.data.PreferenceDataManager;
import com.namazed.testplayer.mvp.base.BasePresenter;
import com.namazed.testplayer.mvp.contract.MainContract;
import com.namazed.testplayer.network.LinksService;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import timber.log.Timber;


public class MainPresenter extends BasePresenter<MainContract.View>
        implements MainContract.Presenter {

    private final PreferenceDataManager preferenceDataManager;
    private CompositeDisposable compositeDisposable;
    private LinksService linksService;
    private int position;
    private boolean isExists;
    private LinkedHashSet<String> musicsPaths;

    public MainPresenter(PreferenceDataManager preferenceDataManager) {
        this.preferenceDataManager = preferenceDataManager;
    }

    @Override
    public void attachView(MainContract.View view) {
        super.attachView(view);
        compositeDisposable = new CompositeDisposable();
        linksService = getView().getTestPlayerApp().getLinksService();
    }

    @Override
    public void loadUrlsOfMusic() {
        if (!isViewAttached()) return;

        getView().showProgress(true);

        //проверяем загружена ли уже музыка, если да, то выходим из этого метода.
        musicsPaths = preferenceDataManager.getMusicsPaths();
        position = 0;
        if (musicsPaths != null) {
            compositeDisposable.add(getObservableMusicsPaths(musicsPaths).subscribe(
                    path -> {
                        getView().showInitialData(path, position);
                        getView().showProgress(false);
                        position++;
                    }, throwable -> {
                        // TODO: 16.06.2017 тут необходимо заменить текст ошибки
                        getView().showError();
                        Timber.e(throwable, throwable.getMessage());
                    })
            );
            return;
        }

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
                    if (isViewAttached()) {
                        getView().showProgress(false);
                        getView().showData(list);
                    }
                }, throwable -> {
                    Timber.d(throwable, throwable.getMessage());
                    if (isViewAttached()) {
                        getView().showProgress(false);
                        getView().showError();
                    }
                }));
    }

    @Override
    public void loadMusic(List<String> musicsPath) {
        if (!isViewAttached()) return;

        position = 0;
        musicsPaths = new LinkedHashSet<>();

        compositeDisposable.add(Observable.fromIterable(musicsPath)
                .flatMapSingle(path -> linksService.getSong(path))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseBody -> {
                            if (isViewAttached()) {
                                String fileName = "music" + position;
                                String path = createFile(responseBody, fileName);
                                musicsPaths.add(path);
                                getView().showData(path, position);
                                position++;
                            }
                        },
                        throwable -> Timber.e(throwable, throwable.getMessage()),
                        () -> {
                            if (isViewAttached()) {
                                getView().showSuccessLoad();
                                preferenceDataManager.setMusicsPaths(musicsPaths);
                            }
                        }
                ));
    }

    @Override
    public void getPathOfMusic(int positionOfMusic) {
        if (!isViewAttached()) return;

        position = 0;
        if (musicsPaths != null) {
            compositeDisposable.add(getObservableMusicsPaths(musicsPaths)
                    .map(path -> {
                        Pair<String, Integer> pair = new Pair<>(path, position);
                        position++;
                        return pair;
                    })
                    .subscribe(
                            pair -> {
                                if (pair.second == positionOfMusic) {
                                    getView().playMusic(pair.first);
                                }
                            }, throwable -> {
                                Timber.e(throwable, throwable.getMessage());
                            }
                    ));
        }
    }

    @Override
    public void checkDataSource(String dataSourceMusic) {
        if (isViewAttached() && dataSourceMusic != null) {
            getView().playMusic(dataSourceMusic);
        }
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

    private String createFile(ResponseBody responseBody, String fileName) {
        if (!isViewAttached()) return null;

        File file = new File(getView().getTestPlayerApp().getFilesDir(), fileName);
        getView().writeDataIntoFile(responseBody, fileName);
        return file.getAbsolutePath();
    }

    private Observable<String> getObservableMusicsPaths(LinkedHashSet<String> musicsPaths) {
        return Observable.fromIterable(musicsPaths)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
