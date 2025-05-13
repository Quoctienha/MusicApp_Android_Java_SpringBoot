package com.example.musicapp.Fragment;

import static android.content.ContentValues.TAG;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.adapter.SongSearchAdapter;
import com.example.musicapp.api.SongAPI;
import com.example.musicapp.dto.PagedResponseDTO;
import com.example.musicapp.dto.SongDTO;
import com.example.musicapp.ultis.RetrofitService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;

import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private EditText searchInput;
    private RecyclerView resultsRecycler;
    private SongSearchAdapter adapter;
    private List<SongDTO> searchResults = new ArrayList<>();
    private RadioGroup modeGroup;

    private int currentPage = 0;
    private int totalPages = 0;
    private boolean isLoading = false;
    private String currentKeyword = "";
    private String currentMode = "both";
    private static final int PAGE_SIZE = 1;

    private final Handler handler = new Handler();
    private Runnable searchRunnable;

    // Media Player
    private MediaPlayer mediaPlayer;
    private int currentSongIndex = -1;
    private boolean isPlaying = false;

    // Player controls
    private MaterialButton playButton, nextBtn, previousBtn;
    private Slider seekBar;
    private TextView songTitleTextView, songArtistTextView, startTimeTextView, endTimeTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        ImageButton backButton = view.findViewById(R.id.back_button);
        searchInput = view.findViewById(R.id.search_input);
        resultsRecycler = view.findViewById(R.id.search_results_recycle);
        modeGroup = view.findViewById(R.id.mode_group);

        playButton = view.findViewById(R.id.play_button);
        nextBtn = view.findViewById(R.id.next_button);
        previousBtn = view.findViewById(R.id.prev_button);
        seekBar = view.findViewById(R.id.song_progress_slider);
        startTimeTextView = view.findViewById(R.id.current_time);
        endTimeTextView = view.findViewById(R.id.total_time);
        songTitleTextView = view.findViewById(R.id.songTitleTextView);
        songArtistTextView = view.findViewById(R.id.songArtistTextView);

        adapter = new SongSearchAdapter(requireContext(), searchResults, song -> {
            if (isPlaying) stopMusic();
            currentSongIndex = searchResults.indexOf(song);
            updateSongInfo(song);
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        resultsRecycler.setLayoutManager(layoutManager);
        resultsRecycler.setAdapter(adapter);

        backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        resultsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (!recyclerView.canScrollVertically(1) && !isLoading && currentPage < totalPages - 1) {
                    loadMoreResults();
                }
            }
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) handler.removeCallbacks(searchRunnable);
                searchRunnable = () -> {
                    String keyword = s.toString().trim();
                    currentKeyword = keyword;
                    if (!keyword.isEmpty()) {
                        currentPage = 0;
                        searchResults.clear();
                        adapter.notifyDataSetChanged();
                        performSearch(keyword, currentPage);
                    } else {
                        searchResults.clear();
                        adapter.notifyDataSetChanged();
                    }
                };
                handler.postDelayed(searchRunnable, 400);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        modeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (!currentKeyword.isEmpty()) {
                currentPage = 0;
                searchResults.clear();
                adapter.notifyDataSetChanged();
                performSearch(currentKeyword, currentPage);
            }
        });

        playButton.setOnClickListener(v -> {
            if (currentSongIndex == -1 || searchResults.isEmpty()) return;
            if (mediaPlayer == null) {
                startMusic(searchResults.get(currentSongIndex).getFileUrl());
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
            if (searchResults.isEmpty()) return;
            stopMusic();
            currentSongIndex = (currentSongIndex + 1) % searchResults.size();
            updateSongInfo(searchResults.get(currentSongIndex));
            startMusic(searchResults.get(currentSongIndex).getFileUrl());
        });

        previousBtn.setOnClickListener(v -> {
            if (searchResults.isEmpty()) return;
            stopMusic();
            currentSongIndex = (currentSongIndex - 1 + searchResults.size()) % searchResults.size();
            updateSongInfo(searchResults.get(currentSongIndex));
            startMusic(searchResults.get(currentSongIndex).getFileUrl());
        });

        seekBar.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser && mediaPlayer != null) {
                int seekPosition = (int) (mediaPlayer.getDuration() * (value / 100));
                mediaPlayer.seekTo(seekPosition);
            }
        });

        return view;
    }

    private void loadMoreResults() {
        if (currentPage < totalPages - 1) {
            isLoading = true;
            adapter.showLoading(true);
            performSearch(currentKeyword, ++currentPage);
        }
    }

    private void performSearch(String keyword, int page) {
        int selectedId = modeGroup.getCheckedRadioButtonId();
        currentMode = (selectedId == R.id.mode_title) ? "title"
                : (selectedId == R.id.mode_artist) ? "artist" : "both";

        SongAPI songAPI = RetrofitService.getInstance(requireContext()).createService(SongAPI.class);
        songAPI.searchSongs(keyword, currentMode, page, PAGE_SIZE).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PagedResponseDTO<SongDTO>> call, @NonNull Response<PagedResponseDTO<SongDTO>> response) {
                adapter.showLoading(false);
                isLoading = false;

                if (response.isSuccessful() && response.body() != null) {
                    totalPages = response.body().getTotalPages();
                    List<SongDTO> newResults = response.body().getContent();
                    searchResults.addAll(newResults);
                    adapter.notifyItemRangeInserted(searchResults.size() - newResults.size(), newResults.size());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PagedResponseDTO<SongDTO>> call, @NonNull Throwable t) {
                isLoading = false;
                adapter.showLoading(false);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSongInfo(SongDTO song) {
        songTitleTextView.setText(song.getTitle());
        songArtistTextView.setText(song.getArtist());
    }

    private void startMusic(String url) {
        releasePlayer();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                isPlaying = true;
                playButton.setIconResource(R.drawable.ic_pause);

                seekBar.setValue(0);
                seekBar.setValueFrom(0);
                seekBar.setValueTo(100);

                int totalDuration = mp.getDuration();
                endTimeTextView.setText(formatTime(totalDuration));

                startUpdatingSeekBar();
                Log.d(TAG, "Playing: " + url);

                // Gọi API tăng view
                if (searchResults != null && currentSongIndex >= 0) {
                    long songId = searchResults.get(currentSongIndex).getId();
                    SongAPI songAPI = RetrofitService.getInstance(requireContext()).createService(SongAPI.class);
                    songAPI.incrementView(songId).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Log.d(TAG, "View count updated for song id: " + songId);
                            } else {
                                Log.w(TAG, "Failed to update view. Code: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e(TAG, "Error updating view: " + t.getMessage());
                        }
                    });
                }
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                isPlaying = false;
                playButton.setIconResource(R.drawable.ic_play);
                handler.removeCallbacksAndMessages(null);
                seekBar.setValue(0);
                startTimeTextView.setText(formatTime(0));
                releasePlayer();
            });

            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }


    private void startUpdatingSeekBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && isPlaying) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    int duration = mediaPlayer.getDuration();

                    if (duration > 0) {
                        float progress = (currentPosition * 100f) / duration;
                        seekBar.setValue(Math.min(progress, 100f));
                        startTimeTextView.setText(formatTime(currentPosition));
                    }
                    handler.postDelayed(this, 500);
                }
            }
        }, 0);
    }

    private String formatTime(int millis) {
        int minutes = (millis / 1000) / 60;
        int seconds = (millis / 1000) % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    private void stopMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
            playButton.setIconResource(R.drawable.ic_play);
            handler.removeCallbacksAndMessages(null);
        }
    }

    private void releasePlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
            playButton.setIconResource(R.drawable.ic_play);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releasePlayer();
        handler.removeCallbacksAndMessages(null);
    }
}
