package com.example.musicapp.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.example.musicapp.activity.SongDetailActivity;
import com.example.musicapp.api.PlaylistAPI;
import com.example.musicapp.api.SongAPI;
import com.example.musicapp.command.Command;
import com.example.musicapp.command.NavigateToActivityCommand;
import com.example.musicapp.dto.SongDTO;
import com.example.musicapp.dto.SongRatingResponseDTO;
import com.example.musicapp.ultis.RetrofitService;
import com.example.musicapp.dto.PlaylistDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private final List<SongDTO> songList;
    private final OnSongClickListener listener;
    private final Context context;
    private final SongAPI songAPI;

    private PlaylistAPI playlistAPI;


    public SongAdapter(Context context, List<SongDTO> songList, OnSongClickListener listener) {
        this.context = context;
        this.songList = songList;
        this.listener = listener;
        this.songAPI = RetrofitService.getInstance(context).createService(SongAPI.class);
        this.playlistAPI = RetrofitService.getInstance(context).createService(PlaylistAPI.class);
    }

    public interface OnSongClickListener {
        void onSongClick(SongDTO song);
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        SongDTO song = songList.get(position);
        holder.titleText.setText(song.getTitle());
        holder.artistText.setText(song.getArtist());
        Glide.with(holder.itemView.getContext())
                .load(song.getImageUrl())
                .placeholder(R.drawable.placeholder)
                .into(holder.coverImage);

        songAPI.getUserRatingForSong(song.getId()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<SongRatingResponseDTO> call,@NonNull Response<SongRatingResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String userRating = response.body().getRating();
                    if ("like".equals(userRating)) {
                        holder.likeButton.setImageResource(R.drawable.ic_like_filled);
                        holder.dislikeButton.setImageResource(R.drawable.ic_dislike_empty);
                    } else if ("dislike".equals(userRating)) {
                        holder.likeButton.setImageResource(R.drawable.ic_like_empty);
                        holder.dislikeButton.setImageResource(R.drawable.ic_dislike_filled);
                    } else {
                        holder.likeButton.setImageResource(R.drawable.ic_like_empty);
                        holder.dislikeButton.setImageResource(R.drawable.ic_dislike_empty);
                    }
                } else {
                    Log.e("SongAdapter", "getUserRating failed: " + response.code() + ", " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<SongRatingResponseDTO> call,@NonNull Throwable t) {
                Log.e("SongAdapter", "getUserRating error", t);
            }
        });



        holder.itemView.setOnClickListener(v -> listener.onSongClick(song));

        holder.menuButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.menuButton);
            popup.inflate(R.menu.song_menu);
            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_song_detail) {
                    Bundle extras = new Bundle();
                    extras.putSerializable("song", song);

                    Command goToSongDetail = new NavigateToActivityCommand(context, SongDetailActivity.class, extras);
                    goToSongDetail.execute();
                    return true;
                } else if (itemId == R.id.menu_add_to_playlist) {
                    showPlaylistDialog(song);
                    return true;
                }
                return false;
            });
            popup.show();
        });

        // Handle like button click
        holder.likeButton.setOnClickListener(v -> {
            songAPI.likeSong(song.getId()).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<SongRatingResponseDTO> call,@NonNull Response<SongRatingResponseDTO> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String userRating = response.body().getRating();
                        if ("Rating updated".equals(userRating)) {
                            holder.likeButton.setImageResource(R.drawable.ic_like_filled);
                            holder.dislikeButton.setImageResource(R.drawable.ic_dislike_empty);
                            Toast.makeText(context, "Liked " + song.getTitle(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("SongAdapter", "likeSong failed: " + response.code() + ", " + response.message());
                    }

                }

                @Override
                public void onFailure(@NonNull Call<SongRatingResponseDTO> call,@NonNull Throwable t) {
                    Toast.makeText(context, "Failed to like", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Handle dislike button click
        holder.dislikeButton.setOnClickListener(v -> {
            songAPI.dislikeSong(song.getId()).enqueue(new Callback<SongRatingResponseDTO>() {
                @Override
                public void onResponse(@NonNull Call<SongRatingResponseDTO> call,@NonNull Response<SongRatingResponseDTO> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String userRating = response.body().getRating();
                        if ("Rating updated".equals(userRating)) {
                            holder.likeButton.setImageResource(R.drawable.ic_like_empty);
                            holder.dislikeButton.setImageResource(R.drawable.ic_dislike_filled);
                            Toast.makeText(context, "Disliked " + song.getTitle(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("SongAdapter", "dislikeSong failed: " + response.code() + ", " + response.message());
                    }


                }

                @Override
                public void onFailure(@NonNull Call<SongRatingResponseDTO> call,@NonNull Throwable t) {
                    Toast.makeText(context, "Failed to dislike", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void showPlaylistDialog(SongDTO song) {
        PlaylistAPI playlistAPI = RetrofitService.getInstance(context).createService(PlaylistAPI.class);

        playlistAPI.getAllPlaylists().enqueue(new Callback<List<PlaylistDTO>>() {
            @Override
            public void onResponse(Call<List<PlaylistDTO>> call, Response<List<PlaylistDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PlaylistDTO> allPlaylists = response.body();

                    // Lọc ra các playlist chưa chứa bài hát
                    List<PlaylistDTO> availablePlaylists = new ArrayList<>();
                    for (PlaylistDTO p : allPlaylists) {
                        if (p.getSongIds() == null || !p.getSongIds().contains(song.getId())) {
                            availablePlaylists.add(p);
                        }
                    }

                    if (availablePlaylists.isEmpty()) {
                        Toast.makeText(context, "All playlists already contain this song", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String[] playlistNames = new String[availablePlaylists.size()];
                    for (int i = 0; i < availablePlaylists.size(); i++) {
                        playlistNames[i] = availablePlaylists.get(i).getName();
                    }

                    new android.app.AlertDialog.Builder(context)
                            .setTitle("Add to Playlist")
                            .setItems(playlistNames, (dialog, which) -> {
                                PlaylistDTO selected = availablePlaylists.get(which);
                                playlistAPI.addSongToPlaylist(selected.getId(), song.getId()).enqueue(new Callback<PlaylistDTO>() {
                                    @Override
                                    public void onResponse(Call<PlaylistDTO> call, Response<PlaylistDTO> response) {
                                        Toast.makeText(context, "Added to " + selected.getName(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Call<PlaylistDTO> call, Throwable t) {
                                        Toast.makeText(context, "Failed to add to playlist", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            })
                            .show();
                }
            }

            @Override
            public void onFailure(Call<List<PlaylistDTO>> call, Throwable t) {
                Toast.makeText(context, "Failed to fetch playlists", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return songList != null ? songList.size() : 0;
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, artistText;
        ImageView coverImage, menuButton;
        ImageView likeButton, dislikeButton;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.songTitle);
            artistText = itemView.findViewById(R.id.songArtist);
            coverImage = itemView.findViewById(R.id.songCoverImage);
            menuButton = itemView.findViewById(R.id.menu_button);
            likeButton = itemView.findViewById(R.id.like_button);
            dislikeButton = itemView.findViewById(R.id.dislike_button);
        }
    }
}
