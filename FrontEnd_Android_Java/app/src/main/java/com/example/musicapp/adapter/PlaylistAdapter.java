package com.example.musicapp.adapter;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.dto.PlaylistDTO;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private final List<PlaylistDTO> playlists;
    private final OnPlaylistActionListener listener;

    public interface OnPlaylistActionListener {
        void onRename(PlaylistDTO playlist);
        void onDelete(PlaylistDTO playlist);
    }

    public PlaylistAdapter(List<PlaylistDTO> playlists, OnPlaylistActionListener listener) {
        this.playlists = playlists;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        PlaylistDTO playlist = playlists.get(position);
        holder.name.setText(playlist.getName());
        holder.rename.setOnClickListener(v -> listener.onRename(playlist));
        holder.delete.setOnClickListener(v -> listener.onDelete(playlist));
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        MaterialButton rename, delete;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.playlistName);
            rename = itemView.findViewById(R.id.renameIcon);
            delete = itemView.findViewById(R.id.deleteIcon);
        }
    }
}
