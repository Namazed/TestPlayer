package com.namazed.testplayer.mvp.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.namazed.testplayer.R;
import com.namazed.testplayer.TestPlayerApp;
import com.namazed.testplayer.adapter.MusicAdapter;
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

    private final MediaMetadataRetriever mmr = new MediaMetadataRetriever();
    private Handler progressMusicHandler = new Handler();
    private MediaPlayer player;

    private RecyclerView musicsRecycler;
    private MusicAdapter adapter;
    private ProgressDialog progressDialog;
    private ImageButton playButton;
    private ImageButton pauseButton;
    private ImageButton stopButton;
    private String dataSourceMusic;
    private SeekBar progressMusicSeekBar;
    private TextView currentMusicsNameTextView;

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

        currentMusicsNameTextView = (TextView) findViewById(R.id.text_current_musics_name);

        progressMusicSeekBar = (SeekBar) findViewById(R.id.seek_progress_music);
        progressMusicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                progressMusicHandler.removeCallbacks(updateProgressBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                progressMusicHandler.removeCallbacks(updateProgressBar);
                if (player != null) {
                    player.seekTo(seekBar.getProgress());
                }

                updateProgressMusic();
            }
        });

        playButton = (ImageButton) findViewById(R.id.btn_play);
        playButton.setEnabled(false);
        playButton.setOnClickListener(view -> getPresenter().onClickPlayMusic(dataSourceMusic));

        pauseButton = (ImageButton) findViewById(R.id.btn_pause);
        pauseButton.setEnabled(false);
        pauseButton.setOnClickListener(view -> getPresenter().onClickPauseMusic(player.getCurrentPosition()));

        stopButton = (ImageButton) findViewById(R.id.btn_stop);
        stopButton.setEnabled(false);
        stopButton.setOnClickListener(view -> getPresenter().onClickStopMusic());

        MusicAdapter.MusicListener musicListener =
                positionOfMusic -> getPresenter().getPathOfMusic(positionOfMusic);
        musicsRecycler = (RecyclerView) findViewById(R.id.recycler_musics);
        musicsRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MusicAdapter(musicListener);
        musicsRecycler.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressMusicHandler != null) {
            progressMusicHandler.removeCallbacks(updateProgressBar);
            progressMusicHandler = null;
        }

        if (player != null && player.isPlaying()) {
            player.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (progressMusicHandler != null) {
            progressMusicHandler.removeCallbacks(updateProgressBar);
            progressMusicHandler = null;
        }

        progressMusicSeekBar.setOnSeekBarChangeListener(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressMusicHandler != null) {
            progressMusicHandler.removeCallbacks(updateProgressBar);
            progressMusicHandler = null;
        }

        progressMusicSeekBar.setOnSeekBarChangeListener(null);

        if (player != null && player.isPlaying()) {
            player.pause();
            player.stop();
            player.release();
            player = null;
        }
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
                getPresenter().onClickStopMusic();
                player = new MediaPlayer();
            } else {
                //if was clicked pause
                player.start();
                progressMusicSeekBar.setProgress(player.getCurrentPosition());
                playButton.setEnabled(false);
                pauseButton.setEnabled(true);
                stopButton.setEnabled(true);

                updateProgressMusic();
                return;
            }
            dataSourceMusic = pathOfMusic;
            currentMusicsNameTextView.setText(getMetaMusicName(dataSourceMusic));
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(pathOfMusic);
            player.prepare();
            progressMusicSeekBar.setMax(player.getDuration());
            player.start();
            playButton.setEnabled(false);
            pauseButton.setEnabled(true);
            stopButton.setEnabled(true);

            updateProgressMusic();
        } catch (IOException e) {
            Timber.e(e, e.getMessage());
        }
    }

    @Override
    public void pauseMusic(int currentPositionOfMusic) {
        progressMusicSeekBar.setProgress(currentPositionOfMusic);
        player.seekTo(currentPositionOfMusic);
        player.pause();
        progressMusicHandler.removeCallbacks(updateProgressBar);
        pauseButton.setEnabled(false);
        playButton.setEnabled(true);
    }

    @Override
    public void stopMusic() {
        player.stop();
        player.release();
        player = null;
        playButton.setEnabled(true);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
        progressMusicHandler.removeCallbacks(updateProgressBar);
        progressMusicSeekBar.setProgress(0);
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

    private void updateProgressMusic() {
        progressMusicHandler.postDelayed(updateProgressBar, TestPlayerApp.TIMER_REPEAT);
    }

    /**
     * Update progress seekBar from mediaPlayer currentPosition
     */
    private Runnable updateProgressBar = new Runnable() {
        @Override
        public void run() {
            if (player != null) {
                progressMusicSeekBar.setProgress(player.getCurrentPosition());
            }

            progressMusicHandler.postDelayed(this, TestPlayerApp.TIMER_REPEAT);
        }
    };
}
