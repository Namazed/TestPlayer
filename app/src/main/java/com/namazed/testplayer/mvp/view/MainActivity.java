package com.namazed.testplayer.mvp.view;

import android.app.ProgressDialog;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.namazed.testplayer.R;
import com.namazed.testplayer.adapter.SongAdapter;
import com.namazed.testplayer.mvp.base.BaseActivity;
import com.namazed.testplayer.mvp.contract.MainContract;
import com.namazed.testplayer.mvp.presenter.MainPresenter;

import java.io.File;
import java.util.List;

public class MainActivity
        extends BaseActivity<MainContract.View, MainContract.Presenter>
        implements MainContract.View {

    private RecyclerView songsRecycler;
    private SongAdapter adapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getPresenter() == null) {
            super.onCreatePresenter(new MainPresenter(), this);
        }
        setContentView(R.layout.activity_main);

        initViews();
        getPresenter().loadUrlsOfMusic();
    }

    private void initViews() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.progress_load_urls));
        progressDialog.setIndeterminate(true);

        songsRecycler = (RecyclerView) findViewById(R.id.recycler_songs);
        songsRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SongAdapter(getApplicationContext());
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
    public void showData(String name, int position) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(getApplication().getFilesDir() + File.separator + name);
        String metaName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        adapter.setData(metaName, position);
    }

    @Override
    public void showError() {
        Toast.makeText(this, R.string.msg_error_network, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSuccessLoad() {
        Toast.makeText(this, R.string.msg_load_success, Toast.LENGTH_SHORT).show();
    }
}
