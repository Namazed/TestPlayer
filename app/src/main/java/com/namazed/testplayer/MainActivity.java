package com.namazed.testplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.namazed.testplayer.adapter.SongAdapter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private RecyclerView songsRecycler;
    private SongAdapter adapter;
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        //for test
        disposable = ((TestPlayerApp) getApplicationContext()).getLinksService().getFileWithUrls()
                .onErrorResumeNext(throwable -> ((TestPlayerApp) getApplicationContext())
                        .getLinksService().getFileWithUrls())
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
                    // TODO: 15.06.2017 добавить progressDialog и здесь скрывать его.
                    adapter.setData(list);
                }, throwable -> Timber.d(throwable, throwable.getMessage()));
    }

    private void initViews() {
        songsRecycler = (RecyclerView) findViewById(R.id.recycler_songs);
        songsRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SongAdapter(getApplicationContext());
        songsRecycler.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
        }
    }
}
