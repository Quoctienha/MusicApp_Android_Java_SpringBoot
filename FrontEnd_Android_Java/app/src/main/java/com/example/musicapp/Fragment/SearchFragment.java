package com.example.musicapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.adapter.SongAdapter;
import com.example.musicapp.api.SongAPI;
import com.example.musicapp.dto.PageWrapper;
import com.example.musicapp.dto.SongDTO;
import com.example.musicapp.ultis.RetrofitService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment implements SongAdapter.OnSongClickListener {

    private static final String TAG = "SearchFragment_Player";
    private static final String ARG_QUERY = "initial_query";
    public static final String ACTION_UPDATE_TRENDING = "com.example.musicapp.UPDATE_TRENDING_ACTION"; // <<--- THÊM ACTION NAME

    // Interface này có thể không cần thiết nữa nếu SearchFragment tự xử lý mọi thứ
    // Hoặc có thể giữ lại cho các mục đích khác nếu có

    // Views - Tìm kiếm
    private SearchView searchView;
    private RecyclerView recyclerViewSearchResults;
    private ProgressBar progressBarSearch;
    private TextView textViewNoResults;
    private ImageView searchFragmentBackIcon;

    // Views - Phát nhạc
    private MediaPlayer mediaPlayer;
    private MaterialButton playButton, nextBtn, previousBtn;
    private Slider seekBar;
    private TextView songTitleTextView;
    private TextView songArtistTextView;
    private TextView startTimeTextView, endTimeTextView;
    private final Handler playerHandler = new Handler(Looper.getMainLooper());


    // Logic components
    private SongAdapter songAdapter;
    private SongAPI songAPI;
    private LinearLayoutManager layoutManager;

    // State variables - Tìm kiếm
    private String currentQuery = "";
    private boolean isLoading = false;
    private int currentPage = 0;
    private int totalPages = 1;
    private boolean isLastPage = false;
    private static final int PAGE_SIZE = 10;
    private static final int VISIBLE_THRESHOLD = 5;

    // State variables - Phát nhạc
    private List<SongDTO> currentPlaylistForPlayer;
    private int currentSongIndexInPlayer = -1;
    private boolean isPlaying = false;
    private boolean isPreparingMediaPlayer = false;


    public static SearchFragment newInstance(String query) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUERY, query);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isAdded() && getContext() != null) {
            songAPI = RetrofitService.getInstance(requireContext()).createService(SongAPI.class);
        }

        if (getArguments() != null) {
            String initialQuery = getArguments().getString(ARG_QUERY);
            if (initialQuery != null && !initialQuery.trim().isEmpty()) {
                currentQuery = initialQuery.trim();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (songAPI == null && getContext() != null) {
            songAPI = RetrofitService.getInstance(requireContext()).createService(SongAPI.class);
        }
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchView = view.findViewById(R.id.searchView);
        recyclerViewSearchResults = view.findViewById(R.id.recyclerViewSearchResults);
        progressBarSearch = view.findViewById(R.id.progressBarSearch);
        textViewNoResults = view.findViewById(R.id.textViewNoResults);
        searchFragmentBackIcon = view.findViewById(R.id.search_fragment_back_icon);

        playButton = view.findViewById(R.id.play_button);
        nextBtn = view.findViewById(R.id.next_button);
        previousBtn = view.findViewById(R.id.prev_button);
        seekBar = view.findViewById(R.id.song_progress_slider);
        startTimeTextView = view.findViewById(R.id.current_time);
        endTimeTextView = view.findViewById(R.id.total_time);
        songTitleTextView = view.findViewById(R.id.songTitleTextView);
        songArtistTextView = view.findViewById(R.id.songArtistTextView);

        setupRecyclerView();
        setupSearchView();
        setupPlayerControls();

        if (searchFragmentBackIcon != null) {
            searchFragmentBackIcon.setOnClickListener(v -> {
                hideKeyboard();
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }

        if (currentQuery != null && !currentQuery.isEmpty()) {
            if (searchView != null) {
                searchView.post(() -> searchView.setQuery(currentQuery, false));
            }
            resetSearchStateAndSearch();
        } else {
            showKeyboardAndFocus();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releasePlayer();
        playerHandler.removeCallbacksAndMessages(null);
    }

    // ... (setupRecyclerView, setupSearchView, setupPlayerControls, resetSearchState, resetSearchStateAndSearch, performNewSearch, performSearch, showKeyboardAndFocus, hideKeyboard giữ nguyên)
    private void setupRecyclerView() {
        if (getContext() == null || !isAdded()) return;
        songAdapter = new SongAdapter(getContext(), new ArrayList<>(), this);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerViewSearchResults.setLayoutManager(layoutManager);
        recyclerViewSearchResults.setAdapter(songAdapter);
        recyclerViewSearchResults.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { // Check for scroll down
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && !isLastPage) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - VISIBLE_THRESHOLD
                                && firstVisibleItemPosition >= 0
                                && totalItemCount > 0
                                && currentPage < totalPages - 1)
                        {
                            Log.i(TAG, "Scroll near end detected. Requesting page: " + (currentPage + 1));
                            performSearch(currentQuery, currentPage + 1);
                        }
                    }
                }
            }
        });
    }

    private void setupSearchView() {
        if (searchView == null) { Log.e(TAG, "setupSearchView: searchView is null!"); return; }
        searchView.setQueryHint("Tìm bài hát, nghệ sĩ...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.trim().isEmpty()) {
                    String newQuery = query.trim().toLowerCase();
                    if (!currentQuery.equals(newQuery) || songAdapter.getItemCount() == 0 ) {
                        currentQuery = newQuery;
                        resetSearchStateAndSearch();
                    }
                } else {
                    resetSearchState();
                }
                hideKeyboard();
                if (searchView != null) searchView.clearFocus();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null && newText.trim().isEmpty() && (songAdapter != null && songAdapter.getItemCount() > 0)) {
                    currentQuery = "";
                    resetSearchState();
                }
                return true;
            }
        });
    }

    private void setupPlayerControls() {
        if (playButton == null) {
            Log.e(TAG, "Player control views are not initialized. Check your layout file.");
            return;
        }
        playButton.setOnClickListener(v -> {
            if (currentPlaylistForPlayer == null || currentPlaylistForPlayer.isEmpty() || currentSongIndexInPlayer == -1) {
                if (getContext() != null) Toast.makeText(getContext(), "Vui lòng chọn bài hát!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isPreparingMediaPlayer) {
                if (getContext() != null) Toast.makeText(getContext(), "Player is preparing, please wait.", Toast.LENGTH_SHORT).show();
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
            if (isPlaying) {
                stopMusic();
            }
            currentSongIndexInPlayer = (currentSongIndexInPlayer + 1) % currentPlaylistForPlayer.size();
            SongDTO nextSong = currentPlaylistForPlayer.get(currentSongIndexInPlayer);
            updatePlayerSongInfo(nextSong);
            startMusic(nextSong.getFileUrl());
        });

        previousBtn.setOnClickListener(v -> {
            if (currentPlaylistForPlayer == null || currentPlaylistForPlayer.isEmpty()) return;
            if (isPreparingMediaPlayer) return;
            if (isPlaying) {
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
                    Log.e(TAG, "Error seeking MediaPlayer: " + e.getMessage());
                }
            }
        });
    }

    private void resetSearchState() {
        Log.d(TAG, "Resetting search state (results only). Current query: '" + currentQuery + "'");
        currentPage = 0;
        totalPages = 1;
        isLastPage = false;
        isLoading = false;
        if (songAdapter != null) songAdapter.clear();
        if (textViewNoResults != null) textViewNoResults.setVisibility(View.GONE);
        if (progressBarSearch != null) progressBarSearch.setVisibility(View.GONE);
    }

    private void resetSearchStateAndSearch() {
        Log.d(TAG, "Resetting full search state and initiating new search for query: '" + currentQuery + "'");
        currentPage = 0;
        totalPages = 1;
        isLastPage = false;
        if (songAdapter != null) songAdapter.clear();
        if (textViewNoResults != null) textViewNoResults.setVisibility(View.GONE);
        performSearch(currentQuery, 0);
    }

    public void performNewSearch(String newQuery) {
        if (newQuery == null) {
            Log.w(TAG, "performNewSearch called with null query.");
            return;
        }
        Log.i(TAG, "performNewSearch called with query: " + newQuery);
        currentQuery = newQuery.trim().toLowerCase();

        if (searchView != null) {
            searchView.setQuery(currentQuery, false);
        }

        if (!currentQuery.isEmpty()) {
            resetSearchStateAndSearch();
        } else {
            resetSearchState();
        }
    }

    private void performSearch(final String query, final int page) {
        final String queryToSearch = (query != null && !query.isEmpty()) ? query.trim() : currentQuery.trim();

        if (isLoading) {
            return;
        }
        if (queryToSearch.isEmpty()) {
            resetSearchState();
            return;
        }
        if (songAPI == null) {
            if (getContext() != null) {
                songAPI = RetrofitService.getInstance(requireContext()).createService(SongAPI.class);
            } else {

                isLoading = false;

                return;
            }
        }

        isLoading = true;
        if (page == 0 && progressBarSearch != null) progressBarSearch.setVisibility(View.VISIBLE);
        if (textViewNoResults != null) textViewNoResults.setVisibility(View.GONE);

        String searchType = "keyword";
        songAPI.searchSongs(queryToSearch, searchType, page, PAGE_SIZE).enqueue(new Callback<PageWrapper<SongDTO>>() {
            @Override
            public void onResponse(@NonNull Call<PageWrapper<SongDTO>> call, @NonNull Response<PageWrapper<SongDTO>> response) {
                if (!isAdded() || getContext() == null) return;
                isLoading = false;
                if (progressBarSearch != null) progressBarSearch.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    PageWrapper<SongDTO> pageResult = response.body();
                    List<SongDTO> songs = pageResult.getContent();
                    currentPage = pageResult.getNumber();
                    totalPages = pageResult.getTotalPages();
                    isLastPage = pageResult.isLast();

                    if (page == 0) {
                        if (songs == null || songs.isEmpty()) {
                            if (textViewNoResults != null) textViewNoResults.setVisibility(View.VISIBLE);
                            if (songAdapter != null) songAdapter.clear();
                        } else {
                            if (textViewNoResults != null) textViewNoResults.setVisibility(View.GONE);
                            if (songAdapter != null) songAdapter.setSongs(songs);
                        }
                    } else {
                        if (songs != null && !songs.isEmpty()) {
                            if (songAdapter != null) songAdapter.addSongs(songs);
                        }
                    }
                } else {
                    if (page == 0 && songAdapter != null) songAdapter.clear();
                    if (getContext() != null) Toast.makeText(getContext(), "Lỗi khi tải dữ liệu. Code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PageWrapper<SongDTO>> call, @NonNull Throwable t) {
                if (!isAdded() || getContext() == null) return;
                isLoading = false;
                if (progressBarSearch != null) progressBarSearch.setVisibility(View.GONE);
                if (page == 0 && songAdapter != null) songAdapter.clear();
                if (getContext() != null) Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showKeyboardAndFocus() {
        if (searchView == null || !isAdded() || getActivity() == null || !searchView.isAttachedToWindow()) return;
        searchView.post(() -> {
            if (!isAdded() || getActivity() == null || !searchView.isFocusable()) return;
            searchView.setIconified(false);
            if (searchView.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(searchView.findFocus(), InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });
    }

    private void hideKeyboard() {
        if (!isAdded() || getActivity() == null) return;
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getActivity().getCurrentFocus();
        if (view == null) {
            view = getView();
        }
        if (view != null && imm != null && view.getWindowToken() != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    @Override
    public void onSongClick(SongDTO song) {
        if (!isAdded() || getContext() == null) return;
        if (song == null) {
            Toast.makeText(getContext(), "Lỗi: Bài hát không hợp lệ.", Toast.LENGTH_SHORT).show();
            return;
        }
        hideKeyboard();
        if (songAdapter == null) return;

        if (isPreparingMediaPlayer) {
            releasePlayer();
        } else if (isPlaying) {
            SongDTO currentlyPlayingSong = (currentSongIndexInPlayer != -1 && currentPlaylistForPlayer != null && currentSongIndexInPlayer < currentPlaylistForPlayer.size())
                    ? currentPlaylistForPlayer.get(currentSongIndexInPlayer) : null;
            if (currentlyPlayingSong != null && !Objects.equals(currentlyPlayingSong.getId(), song.getId())) {
                stopMusic();
            } else if (currentlyPlayingSong != null && Objects.equals(currentlyPlayingSong.getId(), song.getId())) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    isPlaying = false;
                    if (playButton != null) playButton.setIconResource(R.drawable.ic_play);
                } else if (mediaPlayer != null) {
                    mediaPlayer.start();
                    isPlaying = true;
                    if (playButton != null) playButton.setIconResource(R.drawable.ic_pause);
                }
                return;
            }
        }

        currentPlaylistForPlayer = new ArrayList<>(songAdapter.getCurrentList());
        currentSongIndexInPlayer = currentPlaylistForPlayer.indexOf(song);

        if (currentSongIndexInPlayer == -1) {
            currentPlaylistForPlayer.add(0, song);
            currentSongIndexInPlayer = 0;
        }

        updatePlayerSongInfo(song);
        startMusic(song.getFileUrl());
    }

    private void updatePlayerSongInfo(SongDTO song) {
        if (!isAdded()) return;
        if (songTitleTextView == null || songArtistTextView == null) return; // Thêm kiểm tra view
        if (song == null) {
            songTitleTextView.setText("N/A");
            songArtistTextView.setText("N/A");
            return;
        }
        songTitleTextView.setText(song.getTitle());
        songArtistTextView.setText(song.getArtist() != null ? song.getArtist() : "Unknown Artist");
    }

    private void startMusic(String url) {
        if (getContext() == null || !isAdded()) return;
        if (url == null || url.isEmpty()) {
            Toast.makeText(getContext(), "Invalid song URL.", Toast.LENGTH_SHORT).show();
            return;
        }

        releasePlayer();
        isPreparingMediaPlayer = true;
        setPlayerControlsEnabled(false);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.setOnPreparedListener(mp -> {
                if (!isAdded()) {
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
                    if (songAPI == null && getContext() != null) {
                        songAPI = RetrofitService.getInstance(requireContext()).createService(SongAPI.class);
                    }
                    if (songAPI != null) { // Kiểm tra songAPI trước khi gọi
                        songAPI.incrementView(songId).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                if (!isAdded() || getContext() == null) return;
                                if (response.isSuccessful()) {
                                    Log.d(TAG, "View count updated for song id: " + songId + " (from SearchFragment player)");
                                    // << --- GỬI BROADCAST ĐỂ HOMEFRAGMENT CẬP NHẬT TRENDING --- >>
                                    Intent intent = new Intent(ACTION_UPDATE_TRENDING);
                                    LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent);
                                    Log.d(TAG, "Sent broadcast to update trending.");
                                } else {
                                    Log.w(TAG, "Failed to update view in SearchFragment. Code: " + response.code());
                                }
                            }
                            @Override
                            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                                if (!isAdded()) return;
                                Log.e(TAG, "Error updating view in SearchFragment: " + t.getMessage());
                            }
                        });
                    }
                }
            });
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                if (!isAdded() || getContext() == null) return true;
                isPreparingMediaPlayer = false;
                setPlayerControlsEnabled(true);
                releasePlayer();
                Toast.makeText(getContext(), "Không thể phát bài hát này.", Toast.LENGTH_SHORT).show();
                if (playButton != null) playButton.setIconResource(R.drawable.ic_play);
                updatePlayerSongInfo(null);
                return true;
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                if (!isAdded()) return;
                isPlaying = false;
                if (playButton != null) playButton.setIconResource(R.drawable.ic_play);
                if (playerHandler != null) playerHandler.removeCallbacksAndMessages(null); // Dừng handler khi hoàn thành
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
        } catch (IOException | IllegalStateException e) { // Gộp các Exception có thể xảy ra
            Log.e(TAG, "Exception preparing MediaPlayer: " + e.getMessage());
            isPreparingMediaPlayer = false;
            setPlayerControlsEnabled(true);
            releasePlayer();
            if (getContext() != null) Toast.makeText(getContext(), "Lỗi khi chuẩn bị phát nhạc.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setPlayerControlsEnabled(boolean enabled) {
        if (!isAdded()) return; // Thêm kiểm tra
        if (playButton != null) playButton.setEnabled(enabled);
        if (nextBtn != null) nextBtn.setEnabled(enabled);
        if (previousBtn != null) previousBtn.setEnabled(enabled);
        if (seekBar != null) seekBar.setEnabled(enabled);
    }

    private void startUpdatingSeekBar() {
        if (playerHandler == null || mediaPlayer == null || !isPlaying || !isAdded()) {
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
                releasePlayer();
                return;
            }
        }
        isPlaying = false;
        isPreparingMediaPlayer = false;
        if (playButton != null) {
            playButton.setIconResource(R.drawable.ic_play);
        }
        if (playerHandler != null) {
            playerHandler.removeCallbacksAndMessages(null);
        }
        setPlayerControlsEnabled(true);
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
                // Log error
            }
            mediaPlayer = null;
        }
        isPlaying = false;
        isPreparingMediaPlayer = false;
        if (isAdded()) { // Chỉ cập nhật UI nếu fragment còn attached
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
}