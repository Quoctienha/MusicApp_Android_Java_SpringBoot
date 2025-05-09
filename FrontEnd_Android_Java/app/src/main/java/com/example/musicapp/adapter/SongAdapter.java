package com.example.musicapp.adapter;

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
import com.example.musicapp.dto.SongDTO;
import com.example.musicapp.ultis.RetrofitService;

import java.util.ArrayList; // Dùng ArrayList
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private Context context;
    private List<SongDTO> songList; // Dùng List interface nhưng khởi tạo bằng ArrayList
    private OnSongClickListener listener; // Listener interface

    // Interface để xử lý click từ Fragment/Activity
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

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout cho từng item
        View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        // Lấy dữ liệu của bài hát tại vị trí position
        SongDTO song = songList.get(position);
        // Gắn dữ liệu vào ViewHolder và truyền listener
        holder.bind(song, listener);
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

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        ImageView songImageView;
        TextView titleTextView;
        TextView artistTextView;
        ImageView moreOptionsImageView;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);

            // --- Ánh xạ View dùng ID chính xác từ file res/layout/item_song.xml ---
            songImageView = itemView.findViewById(R.id.songCoverImage);      // Sử dụng ID: songCoverImage
            titleTextView = itemView.findViewById(R.id.songTitle);          // Sử dụng ID: songTitle
            artistTextView = itemView.findViewById(R.id.songArtist);         // Sử dụng ID: songArtist
            moreOptionsImageView = itemView.findViewById(R.id.menu_button);   // Sử dụng ID: menu_button

            if (songImageView == null) {
                Log.e("SongAdapter", "SongViewHolder: Could not find view with ID R.id.songCoverImage");
            }
            if (titleTextView == null) {
                Log.e("SongAdapter", "SongViewHolder: Could not find view with ID R.id.songTitle");
            }
            if (artistTextView == null) {
                Log.e("SongAdapter", "SongViewHolder: Could not find view with ID R.id.songArtist");
            }
            if (moreOptionsImageView == null) {
                Log.e("SongAdapter", "SongViewHolder: Could not find view with ID R.id.menu_button (moreOptions)");
            }
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