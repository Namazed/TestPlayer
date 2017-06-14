package com.namazed.testplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.InputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //testing of work
        disposable = ((TestPlayerApp) getApplicationContext()).getLinksService().getFileWithUrls()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    InputStream inputStream = responseBody.byteStream();
                    Toast.makeText(
                            MainActivity.this,
                            String.valueOf(inputStream.available()),
                            Toast.LENGTH_SHORT).show();
                    inputStream.close();
                }, throwable -> Timber.d(throwable, throwable.getMessage()));
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
