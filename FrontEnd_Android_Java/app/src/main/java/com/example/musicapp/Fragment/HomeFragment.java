package com.example.musicapp.Fragment;
import android.content.BroadcastReceiver;

import android.content.Context;

import android.content.Intent;

import android.content.IntentFilter;

import android.media.AudioAttributes; // <<--- THÊM IMPORT

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes; // <<--- THÊM IMPORT
import android.media.AudioManager;

import android.media.MediaPlayer;

import android.os.Build; // <<--- THÊM IMPORT

import android.os.Bundle;

import android.os.Handler;

import android.os.Looper;

import android.util.Log;

import android.view.LayoutInflater;

import android.view.View;

import android.view.ViewGroup;

import android.widget.TextView;

import android.widget.Toast;


import androidx.annotation.NonNull;

import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.RecyclerView;


import com.example.musicapp.R;

import com.example.musicapp.adapter.ArtistAdapter;

import com.example.musicapp.adapter.SongAdapter;

import com.example.musicapp.api.ArtistAPI;

import com.example.musicapp.api.SongAPI;

import com.example.musicapp.dto.ArtistDTO;

import com.example.musicapp.dto.SongDTO;

import com.example.musicapp.ultis.RetrofitService;

import com.google.android.material.button.MaterialButton;

import com.google.android.material.slider.Slider;


import java.io.IOException;

import java.util.ArrayList;

import java.util.List;

import java.util.Locale;
// import java.util.Objects; // Cân nhắc nếu không dùng

// import java.util.Objects; // Cân nhắc nếu không dùng


import retrofit2.Call;

import retrofit2.Callback;

import retrofit2.Response;


public class HomeFragment extends Fragment {

    private RecyclerView trendingArtistsRecycler;

    private RecyclerView trendingMusicRecycler;
    private SongAdapter trendingSongAdapter;
    private ArtistAdapter trendingArtistAdapter; // <<--- Thêm tham chiếu cho ArtistAdapter

    private SongAdapter trendingSongAdapter;

    private ArtistAdapter trendingArtistAdapter; // <<--- Thêm tham chiếu cho ArtistAdapter


    private MediaPlayer mediaPlayer;

    private List<SongDTO> currentPlaylistForPlayer;

    private int currentSongIndexInPlayer = -1;

    private boolean isPlaying = false;
    private boolean isPreparingMediaPlayer = false;


    private boolean isPreparingMediaPlayer = false;



    private MaterialButton playButton, nextBtn, previousBtn;

    private Slider seekBar;

    private TextView songTitleTextView;

    private TextView songArtistTextView;

    private TextView startTimeTextView, endTimeTextView;


    private final Handler playerHandler = new Handler(Looper.getMainLooper());

    private static final String TAG = "HomeFragment_Player";

    private BroadcastReceiver trendingUpdateReceiver;


    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);


// ... (findViewById cho các views khác giữ nguyên) ...

        trendingArtistsRecycler = view.findViewById(R.id.recycler_trending_artists);

        trendingMusicRecycler = view.findViewById(R.id.songRecyclerView);

        playButton = view.findViewById(R.id.play_button);

        nextBtn = view.findViewById(R.id.next_button);

        previousBtn = view.findViewById(R.id.prev_button);

        seekBar = view.findViewById(R.id.song_progress_slider);

        startTimeTextView = view.findViewById(R.id.current_time);

        endTimeTextView = view.findViewById(R.id.total_time);

        songTitleTextView = view.findViewById(R.id.songTitleTextView);

        songArtistTextView = view.findViewById(R.id.songArtistTextView);



        setupTrendingArtistsRecyclerView(); // Setup RecyclerView và Adapter một lần

        setupTrendingMusicRecyclerView();

        setupPlayerControls();


// Gọi fetch dữ liệu sau khi view đã được tạo hoàn toàn (trong onResume hoặc onViewCreated)

        setupTrendingArtistsRecyclerView(); // Setup RecyclerView và Adapter một lần
        setupTrendingMusicRecyclerView();
        setupPlayerControls();

        // Gọi fetch dữ liệu sau khi view đã được tạo hoàn toàn (trong onResume hoặc onViewCreated)
        return view;

    }


    @Override

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        trendingUpdateReceiver = new BroadcastReceiver() {

            @Override

            public void onReceive(Context context, Intent intent) {

                if (SearchFragment.ACTION_UPDATE_TRENDING.equals(intent.getAction())) {

                    Log.d(TAG, "Received broadcast to update trending music from SearchFragment.");

                    if (isAdded() && getView() != null) {

                        fetchAndUpdateTrendingMusicData();

                    }

                }

            }

        };

        if (getContext() != null) {

            LocalBroadcastManager.getInstance(requireContext()).registerReceiver(

                    trendingUpdateReceiver, new IntentFilter(SearchFragment.ACTION_UPDATE_TRENDING)

            );

            Log.d(TAG, "TrendingUpdateReceiver registered in onViewCreated.");

        }

// Gọi fetch dữ liệu ở đây sau khi các RecyclerView đã được setup

        fetchAndUpdateTrendingArtistsData();

    }


    @Override

    public void onResume() {

        super.onResume();

        Log.d(TAG, "onResume called - Explicitly fetching/refreshing trending music data.");

        fetchAndUpdateTrendingMusicData(); // Luôn làm mới trending songs khi fragment resume


// Cập nhật trạng thái nút play

        if (mediaPlayer != null && playButton != null) {

            if (mediaPlayer.isPlaying()) {

                playButton.setIconResource(R.drawable.ic_pause);

            } else {

                playButton.setIconResource(R.drawable.ic_play);

            }

        } else if (playButton != null) {

            playButton.setIconResource(R.drawable.ic_play);

        }

    }


// ... (onPause giữ nguyên) ...

    @Override

    public void onPause() {

        super.onPause();

        Log.d(TAG, "onPause called.");

    }


    private void setupTrendingArtistsRecyclerView() {

        if (getContext() == null || !isAdded() || trendingArtistsRecycler == null) return;

        trendingArtistsRecycler.setLayoutManager(

                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

// Khởi tạo adapter một lần với danh sách rỗng

        trendingArtistAdapter = new ArtistAdapter(new ArrayList<>()); // Truyền danh sách rỗng

        trendingArtistsRecycler.setAdapter(trendingArtistAdapter);

    }


    private void fetchAndUpdateTrendingArtistsData() {

        if (getContext() == null || !isAdded() || getView() == null) return;

        RetrofitService retrofitService = RetrofitService.getInstance(requireContext());

        ArtistAPI artistAPI = retrofitService.createService(ArtistAPI.class);


        Log.d(TAG, "Fetching trending artists data...");

        artistAPI.getTopArtists().enqueue(new Callback<List<ArtistDTO>>() {

            @Override

            public void onResponse(@NonNull Call<List<ArtistDTO>> call, @NonNull Response<List<ArtistDTO>> response) {

                if (!isAdded() || getContext() == null || getView() == null) return;

                if (response.isSuccessful() && response.body() != null) {

                    if (trendingArtistAdapter != null) {

// Cập nhật dữ liệu cho adapter hiện có

// Bạn cần thêm phương thức `updateData` vào ArtistAdapter

// trendingArtistAdapter.updateData(response.body());

// Hoặc nếu ArtistAdapter là ListAdapter:

// trendingArtistAdapter.submitList(response.body());

// Tạm thời, để giữ code chạy, chúng ta tạo lại adapter, nhưng đây là điểm cần tối ưu:

                        trendingArtistsRecycler.setAdapter(new ArtistAdapter(response.body()));

                        Log.d(TAG, "Trending artists updated: " + response.body().size());

                    }

                } else {

                    if (getContext() != null) Toast.makeText(getContext(), "Failed to load artists", Toast.LENGTH_SHORT).show();

                }

            }
            @Override

            public void onFailure(@NonNull Call<List<ArtistDTO>> call, @NonNull Throwable t) {

                if (!isAdded() || getContext() == null || getView() == null) return;

                if (getContext() != null) Toast.makeText(getContext(), "API Error (Artists): " + t.getMessage(), Toast.LENGTH_SHORT).show();

            }

        });

    }



    private void setupTrendingMusicRecyclerView() {

        if (getContext() == null || !isAdded() || trendingMusicRecycler == null) return;

        trendingMusicRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        trendingSongAdapter = new SongAdapter(requireContext(), new ArrayList<>(), song -> {

            if (isPreparingMediaPlayer) {

                if (getContext() != null) Toast.makeText(getContext(), "Player is preparing...", Toast.LENGTH_SHORT).show();

                return;

            }

            if (isPlaying) {

                stopMusic();

            }

            List<SongDTO> currentAdapterSongs = trendingSongAdapter.getCurrentList();

            currentPlaylistForPlayer = new ArrayList<>(currentAdapterSongs);

            currentSongIndexInPlayer = currentPlaylistForPlayer.indexOf(song);


            if (currentSongIndexInPlayer != -1) {

                updatePlayerSongInfo(song);

                startMusic(song.getFileUrl());

            } else {

                Log.e(TAG, "Clicked song from trending not found. Song: " + song.getTitle());

                if (getContext() != null) Toast.makeText(getContext(), "Error selecting song.", Toast.LENGTH_SHORT).show();

            }

        });

        trendingMusicRecycler.setAdapter(trendingSongAdapter);

    }


    private void fetchAndUpdateTrendingMusicData() {

        if (getContext() == null || !isAdded() || getView() == null) {

            Log.w(TAG, "fetchAndUpdateTrendingMusicData: Fragment not ready or view is null.");

            return;

        }

        RetrofitService retrofitService = RetrofitService.getInstance(requireContext());

        SongAPI songAPI = retrofitService.createService(SongAPI.class);


        Log.d(TAG, "HomeFragment: Fetching trending music data...");

        songAPI.getTop10Songs().enqueue(new Callback<List<SongDTO>>() {

            @Override

            public void onResponse(@NonNull Call<List<SongDTO>> call, @NonNull Response<List<SongDTO>> response) {

                if (!isAdded() || getContext() == null || getView() == null) return;

                if (response.isSuccessful() && response.body() != null) {

                    if (trendingSongAdapter != null) {

                        List<SongDTO> newSongs = response.body();

// Nếu SongAdapter là ListAdapter, dùng submitList:

// trendingSongAdapter.submitList(newSongs);

// Nếu SongAdapter là RecyclerView.Adapter thường, đảm bảo setSongs gọi notifyDataSetChanged()

                        trendingSongAdapter.setSongs(newSongs);

                        Log.d(TAG, "HomeFragment: Trending songs updated via fetch. Count: " + newSongs.size());

                    }

                } else {

                    Log.e(TAG, "HomeFragment: Failed to fetch trending songs! Code: " + response.code());

                    if (getContext() != null) Toast.makeText(getContext(), "Failed to fetch trending songs!", Toast.LENGTH_SHORT).show();

                }

            }
            @Override

            public void onFailure(@NonNull Call<List<SongDTO>> call, @NonNull Throwable t) {

                if (!isAdded() || getContext() == null || getView() == null) return;

                Log.e(TAG, "HomeFragment: API Error (Trending Songs): " + t.getMessage());

                if (getContext() != null) Toast.makeText(getContext(), "API Error (Trending Songs): " + t.getMessage(), Toast.LENGTH_SHORT).show();

            }

        });
    }

    }


// ... (setupPlayerControls, updatePlayerSongInfo giữ nguyên) ...

    private void setupPlayerControls() {

        if (playButton == null) return;

        playButton.setOnClickListener(v -> {

            if (currentPlaylistForPlayer == null || currentPlaylistForPlayer.isEmpty() || currentSongIndexInPlayer == -1) {

                if(getContext() != null) Toast.makeText(getContext(), "Vui lòng chọn bài hát!", Toast.LENGTH_SHORT).show();

                return;

            }

            if (isPreparingMediaPlayer) {

                if(getContext() != null) Toast.makeText(getContext(), "Player is preparing...", Toast.LENGTH_SHORT).show();

                return;

            }


            if (mediaPlayer == null) {

                SongDTO songToPlay = currentPlaylistForPlayer.get(currentSongIndexInPlayer);

                updatePlayerSongInfo(songToPlay);

                startMusic(songToPlay.getFileUrl());

            } else if (mediaPlayer.isPlaying()) {

                mediaPlayer.pause();

                isPlaying = false;

                playButton.setIconResource(R.drawable.ic_play);

            } else {

                mediaPlayer.start();

                isPlaying = true;

                playButton.setIconResource(R.drawable.ic_pause);

                startUpdatingSeekBar();

            }

        });


        nextBtn.setOnClickListener(v -> {

            if (currentPlaylistForPlayer == null || currentPlaylistForPlayer.isEmpty()) return;

            if (isPreparingMediaPlayer) return;

            if (isPlaying || mediaPlayer != null) {

                stopMusic();

            }
    c(nextSong.getFileUrl());

            currentSongIndexInPlayer = (currentSongIndexInPlayer + 1) % currentPlaylistForPlayer.size();

            SongDTO nextSong = currentPlaylistForPlayer.get(currentSongIndexInPlayer);

            updatePlayerSongInfo(nextSong);

            startMusic(nextSong.getFileUrl());

        });


        previousBtn.setOnClickListener(v -> {

            if (currentPlaylistForPlayer == null || currentPlaylistForPlayer.isEmpty()) return;

            if (isPreparingMediaPlayer) return;

            if (isPlaying || mediaPlayer != null) {

                stopMusic();

            }

            currentSongIndexInPlayer = (currentSongIndexInPlayer - 1 + currentPlaylistForPlayer.size()) % currentPlaylistForPlayer.size();

            SongDTO prevSong = currentPlaylistForPlayer.get(currentSongIndexInPlayer);

            updatePlayerSongInfo(prevSong);

            startMusic(prevSong.getFileUrl());

        });


        seekBar.addOnChangeListener((slider, value, fromUser) -> {


            if (fromUser && mediaPlayer != null && mediaPlayer.getDuration() > 0) {

                try {

                    int seekPosition = (int) (mediaPlayer.getDuration() * (value / 100f));

                    mediaPlayer.seekTo(seekPosition);

                } catch (IllegalStateException e) {

                    Log.e(TAG, "IllegalStateException while seeking: " + e.getMessage());

                }

            }

        });

    }


    private void updatePlayerSongInfo(SongDTO song) {

        if (!isAdded() || getView() == null) return;

        if (songTitleTextView == null || songArtistTextView == null) return;

        if (song == null) {

            songTitleTextView.setText("N/A");

            songArtistTextView.setText("N/A");

            return;

        }

        songTitleTextView.setText(song.getTitle());

        songArtistTextView.setText(song.getArtist() != null ? song.getArtist() : "Unknown Artist");

    }



    private void startMusic(String url) {

        if (getContext() == null || !isAdded() || getView() == null) return;

        if (url == null || url.isEmpty()) {

            if (getContext() != null) Toast.makeText(getContext(), "Invalid song URL.", Toast.LENGTH_SHORT).show();

            return;

        }


        releasePlayer();
        isPreparingMediaPlayer = true;
        setPlayerControlsEnabled(false);

        isPreparingMediaPlayer = true;

        setPlayerControlsEnabled(false);


        mediaPlayer = new MediaPlayer();


// << --- SỬ DỤNG AudioAttributes CHO API 21+ --- >>

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            AudioAttributes audioAttributes = new AudioAttributes.Builder()

                    .setUsage(AudioAttributes.USAGE_MEDIA)

                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)

                    .build();

            mediaPlayer.setAudioAttributes(audioAttributes);

        } else {

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        }


        try {

            mediaPlayer.setDataSource(url);

            mediaPlayer.setOnPreparedListener(mp -> {

                if (!isAdded() || getView() == null) {

                    releasePlayer();

                    return;

                }

                isPreparingMediaPlayer = false;

                setPlayerControlsEnabled(true);

                mp.start();

                isPlaying = true;

                if (playButton != null) playButton.setIconResource(R.drawable.ic_pause);

                if (seekBar != null) {

                    seekBar.setValue(0);

                    seekBar.setValueTo(100);

                }

                if(endTimeTextView != null) endTimeTextView.setText(formatTime(mp.getDuration()));

                if(startTimeTextView != null) startTimeTextView.setText(formatTime(0));

                startUpdatingSeekBar();


                if (currentPlaylistForPlayer != null && currentSongIndexInPlayer >= 0 && currentSongIndexInPlayer < currentPlaylistForPlayer.size()) {

                    long songId = currentPlaylistForPlayer.get(currentSongIndexInPlayer).getId();

                    SongAPI songAPI = RetrofitService.getInstance(requireContext()).createService(SongAPI.class);

                    songAPI.incrementView(songId).enqueue(new Callback<Void>() {

                        @Override

                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {

                            if (!isAdded() || getView() == null) return;

                            if (response.isSuccessful()) {

                                Log.d(TAG, "View count updated for song id: " + songId + " (from HomeFragment player)");

                                fetchAndUpdateTrendingMusicData();

                            } else {

                                Log.w(TAG, "Failed to update view (HomeFragment). Code: " + response.code());

                            }

                        }
                        @Override

                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {

                            if (!isAdded() || getView() == null) return;

                            Log.e(TAG, "Error updating view (HomeFragment): " + t.getMessage());

                        }

                    });

                }

            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {

                if (!isAdded() || getContext() == null || getView() == null) return true;

                isPreparingMediaPlayer = false;

                setPlayerControlsEnabled(true);

                releasePlayer();

                Toast.makeText(getContext(), "Không thể phát bài hát này.", Toast.LENGTH_SHORT).show();

                if (playButton != null) playButton.setIconResource(R.drawable.ic_play);

                updatePlayerSongInfo(null);

                return true;

            });

            mediaPlayer.setOnCompletionListener(mp -> {

                if (!isAdded() || getView() == null) return;

                isPlaying = false;

                if (playButton != null) playButton.setIconResource(R.drawable.ic_play);

                if (playerHandler != null) playerHandler.removeCallbacksAndMessages(null);

                if(seekBar != null) seekBar.setValue(0);

                if(startTimeTextView != null) startTimeTextView.setText(formatTime(0));


                if (currentPlaylistForPlayer != null && !currentPlaylistForPlayer.isEmpty()) {

                    currentSongIndexInPlayer = (currentSongIndexInPlayer + 1) % currentPlaylistForPlayer.size();

                    SongDTO nextSong = currentPlaylistForPlayer.get(currentSongIndexInPlayer);

                    updatePlayerSongInfo(nextSong);

                    startMusic(nextSong.getFileUrl());

                } else {

                    releasePlayer();

                }

            });
            mediaPlayer.setOnCompletionListener(mp -> {
                if (!isAdded() || getView() == null) return;
                isPlaying = false;
                if (playButton != null) playButton.setIconResource(R.drawable.ic_play);
                if (playerHandler != null) playerHandler.removeCallbacksAndMessages(null);
                if(seekBar != null) seekBar.setValue(0);
                if(startTimeTextView != null) startTimeTextView.setText(formatTime(0));

                if (currentPlaylistForPlayer != null && !currentPlaylistForPlayer.isEmpty()) {
                    currentSongIndexInPlayer = (currentSongIndexInPlayer + 1) % currentPlaylistForPlayer.size();
                    SongDTO nextSong = currentPlaylistForPlayer.get(currentSongIndexInPlayer);
                    updatePlayerSongInfo(nextSong);
                    startMusic(nextSong.getFileUrl());
                } else {
                    releasePlayer();
                }
            });
            mediaPlayer.prepareAsync();

        } catch (IOException | IllegalStateException | IllegalArgumentException | SecurityException e) { // Bắt thêm các Exception có thể xảy ra

            Log.e(TAG, "Exception in startMusic: " + e.getMessage());

            isPreparingMediaPlayer = false;

            setPlayerControlsEnabled(true);

            releasePlayer();

            if (getContext() != null) Toast.makeText(getContext(), "Lỗi khi chuẩn bị phát nhạc.", Toast.LENGTH_SHORT).show();

        }

    }


// ... (setPlayerControlsEnabled, startUpdatingSeekBar, formatTime, stopMusic, releasePlayer giữ nguyên) ...

    private void setPlayerControlsEnabled(boolean enabled) {

        if (!isAdded() || getView() == null) return;

        if (playButton != null) playButton.setEnabled(enabled);

        if (nextBtn != null) nextBtn.setEnabled(enabled);

        if (previousBtn != null) previousBtn.setEnabled(enabled);

        if (seekBar != null) seekBar.setEnabled(enabled);

    }


    private void startUpdatingSeekBar() {

        if (playerHandler == null || mediaPlayer == null || !isPlaying || !isAdded() || getView() == null) {

            return;

        }

        playerHandler.post(new Runnable() {

            @Override

            public void run() {

                if (mediaPlayer != null && isPlaying && isAdded() && getView() != null) {

                    try {

                        int currentPosition = mediaPlayer.getCurrentPosition();

                        int duration = mediaPlayer.getDuration();


                        if (duration > 0) {

                            float progress = (currentPosition * 100f) / duration;

                            if (seekBar != null) seekBar.setValue(Math.min(progress, 100f));

                            if (startTimeTextView != null) startTimeTextView.setText(formatTime(currentPosition));

                        }

                        if (isPlaying && isAdded()) {

                            playerHandler.postDelayed(this, 500);

                        }

                    } catch (IllegalStateException e) {

                        Log.e(TAG, "IllegalStateException in startUpdatingSeekBar: " + e.getMessage());

                        playerHandler.removeCallbacksAndMessages(null);

                    }

                } else {

                    playerHandler.removeCallbacksAndMessages(null);

                }

            }

        });
    }


    private String formatTime(int millis) {

        if (millis < 0) millis = 0;

        int seconds = (millis / 1000) % 60;

        int minutes = (millis / (1000 * 60)) % 60;

        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

    }


    private void stopMusic() {

        if (mediaPlayer != null) {

            try {

                if (mediaPlayer.isPlaying()) {

                    mediaPlayer.stop();

                }

                mediaPlayer.reset();

            } catch (IllegalStateException e) {

                Log.e(TAG, "IllegalStateException in stopMusic: " + e.getMessage());

                releasePlayer();

                return;

            }

        }

        isPlaying = false;

        isPreparingMediaPlayer = false;

        if (isAdded() && getView() != null) {

            if (playButton != null) {

                playButton.setIconResource(R.drawable.ic_play);

            }

            setPlayerControlsEnabled(true);

        }

        if (playerHandler != null) {

            playerHandler.removeCallbacksAndMessages(null);

        }

    }


    private void releasePlayer() {

        if (mediaPlayer != null) {

            try {

                if (mediaPlayer.isPlaying()) {

                    mediaPlayer.stop();

                }

                mediaPlayer.reset();

                mediaPlayer.release();

            } catch (Exception e) {

                Log.e(TAG, "Exception in releasePlayer: " + e.getMessage());

            }

            mediaPlayer = null;

        }

        isPlaying = false;

        isPreparingMediaPlayer = false;

        if (isAdded() && getView() != null) {

            if (playButton != null) playButton.setIconResource(R.drawable.ic_play);

            if (seekBar != null) seekBar.setValue(0);

            if (startTimeTextView != null) startTimeTextView.setText(formatTime(0));

            if (endTimeTextView != null) endTimeTextView.setText(formatTime(0));

            setPlayerControlsEnabled(true);

        }

        if (playerHandler != null) {

            playerHandler.removeCallbacksAndMessages(null);

        }

    }


    @Override

    public void onDestroyView() {

        super.onDestroyView();

        if (trendingUpdateReceiver != null && getContext() != null) {

            LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(trendingUpdateReceiver);

            Log.d(TAG, "TrendingUpdateReceiver unregistered in onDestroyView.");

        }

        releasePlayer();

        if(playerHandler != null) {

            playerHandler.removeCallbacksAndMessages(null);

        }

        Log.d(TAG, "onDestroyView completed.");

    }


    public void playSongFromSearch(SongDTO selectedSong, List<SongDTO> searchResultList) {

        if (!isAdded() || getContext() == null || getView() == null) {

            Log.e(TAG, "playSongFromSearch: Fragment not ready or view is null.");

            return;

        }

        if (selectedSong == null || searchResultList == null || searchResultList.isEmpty()) {

            if(getContext() != null) Toast.makeText(getContext(), "Invalid song or playlist from search!", Toast.LENGTH_SHORT).show();

            return;

        }

        Log.d(TAG, "playSongFromSearch called for song: " + selectedSong.getTitle());


        if (isPreparingMediaPlayer) {

            releasePlayer();

        } else if (isPlaying) {

            stopMusic();

        }
        currentPlaylistForPlayer = new ArrayList<>(searchResultList);
        currentSongIndexInPlayer = currentPlaylistForPlayer.indexOf(selectedSong);
        if (currentSongIndexInPlayer == -1) {

            Log.w(TAG, "Selected song from search not found in provided list. Adding it to the beginning.");

            currentPlaylistForPlayer.add(0, selectedSong);

            currentSongIndexInPlayer = 0;

        }
        updatePlayerSongInfo(selectedSong);
        startMusic(selectedSong.getFileUrl());
        Log.d(TAG, "Playing song from search in HomeFragment's player: " + selectedSong.getTitle());
    }
