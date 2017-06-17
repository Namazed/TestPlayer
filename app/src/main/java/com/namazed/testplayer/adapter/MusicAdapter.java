package com.namazed.testplayer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.namazed.testplayer.R;
import com.namazed.testplayer.network.RetrofitService;

import java.util.LinkedList;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private final MusicListener musicListener;
    private List<String> musicsName;

    public interface MusicListener {
        void onClickMusic(int positionOfMusic);
    }

    public MusicAdapter(MusicListener musicListener) {
        this.musicListener = musicListener;
        musicsName = new LinkedList<>();
    }

    @Override
    public MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MusicViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music, parent, false));
    }

    @Override
    public void onBindViewHolder(MusicViewHolder holder, int position) {
        holder.bind(musicsName.get(position), position);
    }

    @Override
    public int getItemCount() {
        return musicsName == null ? 0 : musicsName.size();
    }

    /**
     * Set the urls of musics, use it when we didn't load musics yet at phone
     * @param list - list urls of musics
     */
    public void setData(List<String> list) {
        musicsName.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * Set the name music into position from list, after loading music
     * @param name - name of music from metaData
     * @param position - position of music from list urls
     */
    public void setData(String name, int position) {
        musicsName.set(position, name);
        notifyItemChanged(position);
    }

    /**
     * Set the loaded musics when we start application
     * @param name - name of loaded music from metaData
     * @param position - position of loaded music from saved list in sharedPreference
     */
    public void setInitialData(String name, int position) {
        musicsName.add(name);
        notifyItemChanged(position);
    }

    class MusicViewHolder extends RecyclerView.ViewHolder {
        TextView nameMusicTextView;
        ProgressBar loadMusicProgress;

        MusicViewHolder(View itemView) {
            super(itemView);
            nameMusicTextView = (TextView) itemView.findViewById(R.id.text_name_music);
            loadMusicProgress = (ProgressBar) itemView.findViewById(R.id.progress_load_music);
        }

        void bind(String name, int positionOfMusic) {
            if (name.contains(RetrofitService.BASE_URL)) {
                nameMusicTextView.setText(name);
                loadMusicProgress.setVisibility(View.VISIBLE);
            } else {
                nameMusicTextView.setText(name);
                itemView.setOnClickListener(view -> musicListener.onClickMusic(positionOfMusic));
                loadMusicProgress.setVisibility(View.INVISIBLE);
            }
        }
    }
}
