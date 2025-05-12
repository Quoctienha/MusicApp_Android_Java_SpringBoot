package com.example.musicapp.adapter;

import android.app.AlertDialog; // For showPlaylistDialog
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu; // Import PopupMenu
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapp.R; // Import lớp R của project
import com.example.musicapp.api.SongAPI;
import com.example.musicapp.command.Command;
import com.example.musicapp.command.NavigateToActivityCommand;
import com.example.musicapp.dto.PlaylistDTO;
import com.example.musicapp.dto.SongDTO;
import com.example.musicapp.ultis.RetrofitService;

import java.util.ArrayList; // Dùng ArrayList
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private List<SongDTO> songList; // Use List interface, initialized in constructor
    private final OnSongClickListener listener;
    private final Context context;
    private final SongAPI songAPI;
    private final PlaylistAPI playlistAPI; // Added from "Bản 2"

    // Interface to handle click from Fragment/Activity (consistent with both versions)
    public interface OnSongClickListener {
        void onSongClick(SongDTO song);
        // Có thể thêm các hành động khác nếu cần, ví dụ:
        // void onAddToPlaylistClick(SongDTO song);
        // void onViewLyricsClick(SongDTO song);
    }

    // Constructor nhận Context, danh sách bài hát và listener
    public SongAdapter(Context context, List<SongDTO> songList, OnSongClickListener listener) {
        this.context = context;
        // Luôn khởi tạo bằng ArrayList để đảm bảo các phương thức clear/add/set hoạt động
        this.songList = (songList != null) ? new ArrayList<>(songList) : new ArrayList<>();
        this.listener = listener;
    }

    // Constructor based on "Bản 2" but with defensive list initialization from "Bản 1"
    public SongAdapter(Context context, List<SongDTO> songList, OnSongClickListener listener) {
        this.context = context;
        // Initialize songList defensively as in "Bản 1"
        this.songList = (songList != null) ? new ArrayList<>(songList) : new ArrayList<>();
        this.listener = listener;
        // Initialize API services as in "Bản 2"
        this.songAPI = RetrofitService.getInstance(context).createService(SongAPI.class);
        this.playlistAPI = RetrofitService.getInstance(context).createService(PlaylistAPI.class);
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()) // Use parent.getContext() for consistency
                .inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        // Lấy dữ liệu của bài hát tại vị trí position
        SongDTO song = songList.get(position);
        if (song == null) {
            Log.e("SongAdapter", "Song at position " + position + " is null.");
            // Optionally set views to default/empty state
            holder.titleText.setText("N/A");
            holder.artistText.setText("N/A");
            holder.coverImage.setImageResource(R.drawable.placeholder); // Default placeholder
            return;
        }

        // Bind basic data
        holder.titleText.setText(song.getTitle());
        holder.artistText.setText(song.getArtist());
        Glide.with(holder.itemView.getContext())
                .load(song.getImageUrl())
                .placeholder(R.drawable.placeholder) // Placeholder image
                .error(R.drawable.placeholder)       // Error image
                .into(holder.coverImage);

        // Fetch and display user's rating for the song (from "Bản 2")
        // Ensure songAPI is not null
        if (songAPI != null && song.getId() != null) {
            songAPI.getUserRatingForSong(song.getId()).enqueue(new Callback<SongRatingResponseDTO>() {
                @Override
                public void onResponse(@NonNull Call<SongRatingResponseDTO> call, @NonNull Response<SongRatingResponseDTO> response) {
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
                        // Log error but don't crash, default to empty icons
                        Log.e("SongAdapter", "getUserRating failed: " + response.code() + " " + response.message());
                        holder.likeButton.setImageResource(R.drawable.ic_like_empty);
                        holder.dislikeButton.setImageResource(R.drawable.ic_dislike_empty);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<SongRatingResponseDTO> call, @NonNull Throwable t) {
                    Log.e("SongAdapter", "getUserRating error: " + t.getMessage(), t);
                    // Default to empty icons on failure
                    holder.likeButton.setImageResource(R.drawable.ic_like_empty);
                    holder.dislikeButton.setImageResource(R.drawable.ic_dislike_empty);
                }
            });
        } else {
            // Default icons if songAPI is null or song ID is null
            holder.likeButton.setImageResource(R.drawable.ic_like_empty);
            holder.dislikeButton.setImageResource(R.drawable.ic_dislike_empty);
            if (songAPI == null) Log.e("SongAdapter", "songAPI is null in onBindViewHolder.");
            if (song.getId() == null) Log.w("SongAdapter", "Song ID is null for: " + song.getTitle());
        }


        // Item click listener (from "Bản 2", consistent with "Bản 1"'s interface)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSongClick(song);
            }
        });

        // Menu button click listener (from "Bản 2")
        holder.menuButton.setOnClickListener(v -> showPopupMenu(holder.menuButton, song, holder));


        // Like button click listener (from "Bản 2")
        holder.likeButton.setOnClickListener(v -> {
            if (songAPI != null && song.getId() != null) {
                songAPI.likeSong(song.getId()).enqueue(new Callback<SongRatingResponseDTO>() {
                    @Override
                    public void onResponse(@NonNull Call<SongRatingResponseDTO> call, @NonNull Response<SongRatingResponseDTO> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // Assuming "Rating updated" is a generic success message from your API
                            // A better approach would be for the API to return the new like/dislike status
                            // For now, we optimistically update the UI based on the action.
                            holder.likeButton.setImageResource(R.drawable.ic_like_filled);
                            holder.dislikeButton.setImageResource(R.drawable.ic_dislike_empty);
                            Toast.makeText(context, "Liked: " + song.getTitle(), Toast.LENGTH_SHORT).show();
                            // Optionally, update local song DTO's like/dislike counts if API returns them
                        } else {
                            Log.e("SongAdapter", "likeSong API call failed: " + response.code() + " " + response.message());
                            Toast.makeText(context, "Failed to like song", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<SongRatingResponseDTO> call, @NonNull Throwable t) {
                        Log.e("SongAdapter", "likeSong API call error: " + t.getMessage(), t);
                        Toast.makeText(context, "Error liking song", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Dislike button click listener (from "Bản 2")
        holder.dislikeButton.setOnClickListener(v -> {
            if (songAPI != null && song.getId() != null) {
                songAPI.dislikeSong(song.getId()).enqueue(new Callback<SongRatingResponseDTO>() {
                    @Override
                    public void onResponse(@NonNull Call<SongRatingResponseDTO> call, @NonNull Response<SongRatingResponseDTO> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            holder.dislikeButton.setImageResource(R.drawable.ic_dislike_filled);
                            holder.likeButton.setImageResource(R.drawable.ic_like_empty);
                            Toast.makeText(context, "Disliked: " + song.getTitle(), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("SongAdapter", "dislikeSong API call failed: " + response.code() + " " + response.message());
                            Toast.makeText(context, "Failed to dislike song", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<SongRatingResponseDTO> call, @NonNull Throwable t) {
                        Log.e("SongAdapter", "dislikeSong API call error: " + t.getMessage(), t);
                        Toast.makeText(context, "Error disliking song", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // showPopupMenu from "Bản 2" with minor logging improvements from "Bản 1"
    private void showPopupMenu(View view, SongDTO song, SongViewHolder holder) {
        PopupMenu popup = new PopupMenu(context, view); // Use view as anchor
        try {
            popup.inflate(R.menu.song_menu); // Ensure this menu resource exists
        } catch (Exception e) {
            Log.e("SongAdapter", "Error inflating song_menu.xml. Ensure it exists in res/menu.", e);
            Toast.makeText(context, "Cannot display song options.", Toast.LENGTH_SHORT).show();
            return;
        }

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_song_detail) {
                Bundle extras = new Bundle();
                extras.putSerializable("song", song); // Pass entire SongDTO object
                Command goToSongDetail = new NavigateToActivityCommand(context, SongDetailActivity.class, extras);
                goToSongDetail.execute();
                return true;
            } else if (itemId == R.id.menu_add_to_playlist) {
                showPlaylistDialog(song); // Method from "Bản 2"
                return true;
            }
            // Add more menu item handling here if needed
            return false;
        });
        popup.show();
    }

    // showPlaylistDialog from "Bản 2"
    private void showPlaylistDialog(SongDTO song) {
        if (playlistAPI == null || song.getId() == null) {
            Toast.makeText(context, "Error: Playlist service not available or song ID is null.", Toast.LENGTH_SHORT).show();
            Log.e("SongAdapter", "playlistAPI is " + (playlistAPI == null ? "null" : "not null") + ", song ID is " + (song.getId() == null ? "null" : "not null"));
            return;
        }

        playlistAPI.getAllPlaylists().enqueue(new Callback<List<PlaylistDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<PlaylistDTO>> call, @NonNull Response<List<PlaylistDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PlaylistDTO> allPlaylists = response.body();
                    List<PlaylistDTO> availablePlaylists = new ArrayList<>();

                    for (PlaylistDTO p : allPlaylists) {
                        // Check if songIds is null or does not contain the song's ID
                        if (p.getSongIds() == null || !p.getSongIds().contains(song.getId())) {
                            availablePlaylists.add(p);
                        }
                    }

                    if (availablePlaylists.isEmpty()) {
                        Toast.makeText(context, "No available playlists or song is already in all playlists.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    String[] playlistNames = new String[availablePlaylists.size()];
                    for (int i = 0; i < availablePlaylists.size(); i++) {
                        playlistNames[i] = availablePlaylists.get(i).getName();
                    }

                    new AlertDialog.Builder(context)
                            .setTitle("Add \"" + song.getTitle() + "\" to Playlist")
                            .setItems(playlistNames, (dialog, which) -> {
                                PlaylistDTO selectedPlaylist = availablePlaylists.get(which);
                                if (selectedPlaylist.getId() == null) {
                                    Toast.makeText(context, "Error: Selected playlist has no ID.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                playlistAPI.addSongToPlaylist(selectedPlaylist.getId(), song.getId()).enqueue(new Callback<PlaylistDTO>() {
                                    @Override
                                    public void onResponse(@NonNull Call<PlaylistDTO> call, @NonNull Response<PlaylistDTO> response) {
                                        if (response.isSuccessful()) {
                                            Toast.makeText(context, "Added \"" + song.getTitle() + "\" to " + selectedPlaylist.getName(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.e("SongAdapter", "Failed to add to playlist. Code: " + response.code() + " Msg: " + response.message());
                                            Toast.makeText(context, "Failed to add to playlist. Server error.", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<PlaylistDTO> call, @NonNull Throwable t) {
                                        Log.e("SongAdapter", "Failure adding to playlist: " + t.getMessage(), t);
                                        Toast.makeText(context, "Failed to add to playlist: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                } else {
                    Log.e("SongAdapter", "Failed to fetch playlists. Code: " + response.code() + " Msg: " + response.message());
                    Toast.makeText(context, "Failed to fetch playlists.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<PlaylistDTO>> call, @NonNull Throwable t) {
                Log.e("SongAdapter", "Failure fetching playlists: " + t.getMessage(), t);
                Toast.makeText(context, "Error fetching playlists: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        // Trả về số lượng item trong danh sách
        return (songList != null) ? songList.size() : 0;
    }

    // --- Các phương thức quản lý dữ liệu Adapter ---

    /**
     * Xóa tất cả bài hát khỏi danh sách và cập nhật RecyclerView.
     */
    public void clear() {
        if (songList != null) {
            int itemCount = songList.size();
            if (itemCount > 0) { // Chỉ thông báo nếu có item bị xóa
                songList.clear();
                notifyItemRangeRemoved(0, itemCount);
            }
        }
    }

    public void setSongs(List<SongDTO> newSongs) {
        // Gán lại list nội bộ (tạo bản sao)
        this.songList = (newSongs != null) ? new ArrayList<>(newSongs) : new ArrayList<>();
        notifyDataSetChanged(); // Thông báo toàn bộ dữ liệu thay đổi
    }
    public void addSongs(List<SongDTO> additionalSongs) {
        if (this.songList != null && additionalSongs != null && !additionalSongs.isEmpty()) {
            int startPosition = getItemCount(); // Vị trí bắt đầu thêm
            this.songList.addAll(additionalSongs);
            notifyItemRangeInserted(startPosition, additionalSongs.size()); // Thông báo item được chèn
        }
    }
    public List<SongDTO> getCurrentList() {
        return new ArrayList<>(this.songList != null ? this.songList : new ArrayList<>());
    }

    // --- Data management methods from "Bản 1" ---
    /**
     * Clears all songs from the adapter.
     */
    public void clear() {
        if (this.songList != null) {
            int oldSize = this.songList.size();
            this.songList.clear();
            if (oldSize > 0) {
                notifyItemRangeRemoved(0, oldSize);
            }
        }
    }

    /**
     * Sets a new list of songs, replacing the current one.
     * @param newSongs The new list of songs.
     */
    public void setSongs(List<SongDTO> newSongs) {
        this.songList = (newSongs != null) ? new ArrayList<>(newSongs) : new ArrayList<>();
        notifyDataSetChanged(); // Notify that the entire dataset has changed
        Log.d("SongAdapter", "Songs set. New count: " + getItemCount());
    }

    /**
     * Adds a list of additional songs to the end of the current list.
     * @param additionalSongs The songs to add.
     */
    public void addSongs(List<SongDTO> additionalSongs) {
        if (this.songList != null && additionalSongs != null && !additionalSongs.isEmpty()) {
            int startPosition = getItemCount();
            this.songList.addAll(additionalSongs);
            notifyItemRangeInserted(startPosition, additionalSongs.size());
            Log.d("SongAdapter", "Songs added. New count: " + getItemCount());
        }
    }

    /**
     * Returns a new copy of the current list of songs.
     * @return A new ArrayList containing the current songs.
     */
    public List<SongDTO> getCurrentList() {
        return new ArrayList<>(this.songList != null ? this.songList : new ArrayList<>());
    }

    // ViewHolder class (based on "Bản 2" as it includes like/dislike buttons)
    public static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, artistText;
        ImageView coverImage, menuButton;
        ImageView likeButton, dislikeButton; // From "Bản 2"

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            // IDs from your item_song.xml
            titleText = itemView.findViewById(R.id.songTitle);
            artistText = itemView.findViewById(R.id.songArtist);
            coverImage = itemView.findViewById(R.id.songCoverImage);
            menuButton = itemView.findViewById(R.id.menu_button);
            likeButton = itemView.findViewById(R.id.like_button);     // Ensure this ID exists
            dislikeButton = itemView.findViewById(R.id.dislike_button); // Ensure this ID exists

            // Log if any view is not found, helpful for debugging layouts
            if (titleText == null) Log.e("SongAdapterVH", "titleText is null");
            if (artistText == null) Log.e("SongAdapterVH", "artistText is null");
            if (coverImage == null) Log.e("SongAdapterVH", "coverImage is null");
            if (menuButton == null) Log.e("SongAdapterVH", "menuButton is null");
            if (likeButton == null) Log.e("SongAdapterVH", "likeButton is null");
            if (dislikeButton == null) Log.e("SongAdapterVH", "dislikeButton is null");
        }

        public void bind(final SongDTO song, final OnSongClickListener listener) {
            if (titleTextView != null) {
                titleTextView.setText(song.getTitle());
            }
            if (artistTextView != null) {
                artistTextView.setText(song.getArtist());
            }
            if (songImageView != null && itemView != null) { // Thêm kiểm tra itemView
                Glide.with(itemView.getContext())
                        .load(song.getImageUrl())
                        .placeholder(R.drawable.placeholder) // Ảnh chờ
                        .error(R.drawable.placeholder)       // Ảnh lỗi
                        .into(songImageView);
            }

            // Bắt sự kiện click vào toàn bộ item -> Gọi listener onSongClick
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSongClick(song);
                }
            });

            // Bắt sự kiện click vào nút more options (nếu có)
            if (moreOptionsImageView != null) {
                moreOptionsImageView.setOnClickListener(v -> showPopupMenu(v, song));
            } else {
                // Log nếu không tìm thấy nút more options để debug layout
                Log.w("SongAdapter", "moreOptionsImageView is null for item: " + (titleTextView != null ? titleTextView.getText() : "Unknown"));
            }
        }

        // Hiển thị PopupMenu khi nhấn nút more options
        private void showPopupMenu(View view, SongDTO song) {
            Context context = view.getContext();
            PopupMenu popup = new PopupMenu(context, view);
            try {
                // Inflate menu từ file res/menu/song_menu.xml
                popup.inflate(R.menu.song_menu);
            } catch (Exception e) {
                Log.e("SongAdapter", "Error inflating song menu. Check res/menu/song_menu.xml", e);
                Toast.makeText(context,"Cannot load song menu", Toast.LENGTH_SHORT).show();
                return;
            }


            popup.show();
        }
    } // Kết thúc SongViewHolder
} // Kết thúc SongAdapter