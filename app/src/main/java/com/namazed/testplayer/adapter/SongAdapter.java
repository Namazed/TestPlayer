package com.namazed.testplayer.adapter;

import android.content.Context;
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

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private final Context context;
    private List<String> songsName;

    public SongAdapter(Context context) {
        this.context = context;
        songsName = new LinkedList<>();
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SongViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false));
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        holder.bind(songsName.get(position));
    }

    @Override
    public int getItemCount() {
        return songsName == null ? 0 : songsName.size();
    }

    public void setData(List<String> list) {
        songsName.addAll(list);
        notifyDataSetChanged();
    }

    public void setData(String name, int position) {
        songsName.set(position, name);
        notifyItemChanged(position);
    }

    class SongViewHolder extends RecyclerView.ViewHolder {
        TextView nameSongTextView;
        ProgressBar loadMusicProgress;

        SongViewHolder(View itemView) {
            super(itemView);
            nameSongTextView = (TextView) itemView.findViewById(R.id.text_name_song);
            loadMusicProgress = (ProgressBar) itemView.findViewById(R.id.progress_load_music);
        }

        void bind(String name) {
            if (name.contains(RetrofitService.BASE_URL)) {
                nameSongTextView.setText(name);
                loadMusicProgress.setVisibility(View.VISIBLE);
            } else {
                nameSongTextView.setText(name);
                loadMusicProgress.setVisibility(View.INVISIBLE);
            }
        }
    }
}
