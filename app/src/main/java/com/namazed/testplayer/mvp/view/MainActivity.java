package com.namazed.testplayer.mvp.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.namazed.testplayer.R;
import com.namazed.testplayer.adapter.SongAdapter;
import com.namazed.testplayer.mvp.base.BaseActivity;
import com.namazed.testplayer.mvp.contract.MainContract;
import com.namazed.testplayer.mvp.presenter.MainPresenter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import timber.log.Timber;

public class MainActivity
        extends BaseActivity<MainContract.View, MainContract.Presenter>
        implements MainContract.View {

    private RecyclerView songsRecycler;
    private SongAdapter adapter;
    private ProgressDialog progressDialog;
    private final MediaMetadataRetriever mmr = new MediaMetadataRetriever();
    private MediaPlayer player;
    private ImageButton playButton;
    private ImageButton pauseButton;
    private ImageButton stopButton;
    private String dataSourceMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getPresenter() == null) {
            super.onCreatePresenter(new MainPresenter(getTestPlayerApp().getPreferenceDataManager()), this);
        }
        setContentView(R.layout.activity_main);

        initViews();
        getPresenter().loadUrlsOfMusic();
    }

    private void initViews() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.progress_load_urls));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        playButton = (ImageButton) findViewById(R.id.btn_play);
        playButton.setOnClickListener(view -> getPresenter().checkDataSource(dataSourceMusic));

        pauseButton = (ImageButton) findViewById(R.id.btn_pause);
        stopButton = (ImageButton) findViewById(R.id.btn_stop);

        SongAdapter.MusicListener musicListener =
                positionOfMusic -> getPresenter().getPathOfMusic(positionOfMusic);
        songsRecycler = (RecyclerView) findViewById(R.id.recycler_songs);
        songsRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SongAdapter(getApplicationContext(), musicListener);
        songsRecycler.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void showProgress(boolean isShow) {
        if (isShow) {
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }
    }

    @Override
    public void showData(List<String> urls) {
        adapter.setData(urls);
        getPresenter().loadMusic(urls);
    }

    @Override
    public void showData(String musicPath, int position) {
        adapter.setData(getMetaMusicName(musicPath), position);
    }

    @Override
    public void showInitialData(String musicPath, int position) {
        adapter.setInitialData(getMetaMusicName(musicPath), position);
    }

    @Override
    public void writeDataIntoFile(ResponseBody responseBody, String fileName) {
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(responseBody.bytes());
            outputStream.close();
        } catch (IOException e) {
            Timber.e(e, e.getMessage());
        }
    }

    @Override
    public void playMusic(String pathOfMusic) {
        try {
            if (player == null) {
                //if first start player
                player = new MediaPlayer();
            } else if (dataSourceMusic != null && !dataSourceMusic.equals(pathOfMusic)) {
                //if change track
                player.stop();
                player.release();
                player = new MediaPlayer();
            } else {
                //if was clicked pause
                // TODO: 17.06.2017 обработать паузу
            }
            dataSourceMusic = pathOfMusic;
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(pathOfMusic);
            player.prepare();
            player.start();
            playButton.setEnabled(false);
            pauseButton.setEnabled(true);
            stopButton.setEnabled(true);
        } catch (IOException e) {
            Timber.e(e, e.getMessage());
        }
    }

    @Override
    public void showError() {
        Toast.makeText(this, R.string.msg_error_network, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSuccessLoad() {
        Toast.makeText(this, R.string.msg_load_success, Toast.LENGTH_SHORT).show();
    }

    private String getMetaMusicName(String musicPath) {
        mmr.setDataSource(musicPath);
        return mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
    }
}
