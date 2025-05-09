package com.example.musicapp.Fragment;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchFragment extends Fragment implements SongAdapter.OnSongClickListener {


    private static final String TAG = "SearchFragment";
    private static final String ARG_QUERY = "initial_query"; // Key for initial query argument


    // --- ĐỊNH NGHĨA INTERFACE ---
    public interface OnSearchResultSelectedListener {
        void onSearchResultSelected(SongDTO selectedSong, List<SongDTO> searchResultList);
    }
    // --- KẾT THÚC INTERFACE ---


    // Views - Tìm kiếm
    private SearchView searchView;
    private RecyclerView recyclerViewSearchResults;
    private ProgressBar progressBarSearch;
    private TextView textViewNoResults;
    private ImageView searchFragmentBackIcon; // THÊM BIẾN CHO ICON BACK

    // Views - Phát nhạc
    private MediaPlayer mediaPlayer;
    private MaterialButton playButton, nextBtn, previousBtn;
    private Slider seekBar;
    private TextView songTitleTextView;
    private TextView songArtistTextView;
    private TextView startTimeTextView, endTimeTextView;
    private Handler handler = new Handler();


    // Logic components
    private SongAdapter songAdapter;
    private SongAPI songAPI;
    private LinearLayoutManager layoutManager;
    private OnSearchResultSelectedListener listener;

    // State variables - Tìm kiếm
    private String currentQuery = "";
    private boolean isLoading = false;
    private int currentPage = 0;
    private int totalPages = 1;
    private boolean isLastPage = false;
    private static final int PAGE_SIZE = 10;
    private static final int VISIBLE_THRESHOLD = 5;

    // State variables - Phát nhạc
    private List<SongDTO> currentPlaylist;
    private int currentSongIndex = -1;
    private boolean isPlaying = false;

    private boolean isPreparingMediaPlayer = false;
    private String currentPreparingUrl = null;

    /**
     * Static factory method to create a new instance of SearchFragment
     * with an initial search query.
     * @param query The initial search query.
     * @return A new instance of SearchFragment.
     */
    public static SearchFragment newInstance(String query) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUERY, query);
        fragment.setArguments(args);
        return fragment;
    }

    // --- onAttach VÀ onDetach ---
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSearchResultSelectedListener) {
            listener = (OnSearchResultSelectedListener) context;
            Log.d(TAG, "OnSearchResultSelectedListener attached.");
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSearchResultSelectedListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        Log.d(TAG, "OnSearchResultSelectedListener detached.");
    }
    // --- KẾT THÚC onAttach/onDetach ---


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Đảm bảo requireContext() không null khi Fragment chưa được attach hoàn toàn
        if (isAdded() && getContext() != null) {
            songAPI = RetrofitService.getInstance(requireContext()).createService(SongAPI.class);
        } else {
            // Hoãn việc khởi tạo songAPI hoặc xử lý lỗi nếu cần
            Log.e(TAG, "onCreate: Context is null, songAPI not initialized yet.");
        }

        if (getArguments() != null) {
            String initialQuery = getArguments().getString(ARG_QUERY);
            if (initialQuery != null && !initialQuery.trim().isEmpty()) {
                currentQuery = initialQuery.trim();
                Log.d(TAG, "onCreate: Initial query set from arguments: " + currentQuery);
            }
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Khởi tạo songAPI ở đây nếu chưa được khởi tạo trong onCreate do context null
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

        playButton = view.findViewById(R.id.play_button);
        nextBtn = view.findViewById(R.id.next_button);
        previousBtn = view.findViewById(R.id.prev_button);
        seekBar = view.findViewById(R.id.song_progress_slider);
        startTimeTextView = view.findViewById(R.id.current_time);
        endTimeTextView = view.findViewById(R.id.total_time);
        songTitleTextView = view.findViewById(R.id.songTitleTextView);
        songArtistTextView = view.findViewById(R.id.songArtistTextView);

        // TÌM ImageView CỦA NÚT BACK
        searchFragmentBackIcon = view.findViewById(R.id.search_fragment_back_icon); // ID từ file XML layout

        setupRecyclerView();
        setupSearchView();
        setupPlayerControls();

        // GẮN OnClickListener CHO NÚT BACK
        if (searchFragmentBackIcon != null) {
            searchFragmentBackIcon.setOnClickListener(v -> {
                // Ẩn bàn phím (nếu đang mở) trước khi quay lại
                hideKeyboard();

                // Gọi hành động onBackPressed của Activity chứa Fragment này
                if (getActivity() != null) {
                    Log.d(TAG, "Nút Back trên thanh tìm kiếm được nhấn, gọi onBackPressed của Activity.");
                    getActivity().onBackPressed();
                }
            });
        }
        // Handle initial query logic
        if (currentQuery != null && !currentQuery.isEmpty()) {
            if (searchView != null) {
                // Post to ensure SearchView is fully initialized
                searchView.post(() -> searchView.setQuery(currentQuery, false)); // Set query without submitting
            }
            Log.d(TAG, "onViewCreated: Performing search with initial query: " + currentQuery);
            resetSearchStateAndSearch(); // Perform search with the initial query
        } else {
            // No initial query, so focus the search bar for user input
            showKeyboardAndFocus();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releasePlayer();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    private void setupRecyclerView() {
        if (getContext() == null) return;
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
                                && totalItemCount > 0 // Ensure totalItemCount is positive
                                && currentPage < totalPages - 1) // Ensure not exceeding total pages
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
        // searchView.setIconified(false); // Đã được xử lý trong logic showKeyboardAndFocus hoặc khi có initialQuery

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { // Search initiated from Fragment's own SearchView
                if (query != null && !query.trim().isEmpty()) {
                    String newQuery = query.trim();
                    Log.d(TAG, "Search submitted from fragment's SearchView: " + newQuery);
                    currentQuery = newQuery; // Update currentQuery
                    resetSearchStateAndSearch();
                } else {
                    resetSearchState(); // Clear results if query is empty
                }
                hideKeyboard();
                if (searchView != null) searchView.clearFocus();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null && newText.trim().isEmpty() && (songAdapter != null && songAdapter.getItemCount() > 0)) {
                    Log.d(TAG, "Search text cleared in fragment's SearchView, resetting state.");
                    currentQuery = "";
                    resetSearchState();
                }
                return false;
            }
        });
    }

    private void setupPlayerControls() {
        playButton.setOnClickListener(v -> {
            if (currentSongIndex == -1 || currentPlaylist == null || currentPlaylist.isEmpty()) {
                if(getContext() != null) Toast.makeText(getContext(), "Vui lòng chọn bài hát!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isPreparingMediaPlayer) {
                Log.d(TAG, "MediaPlayer đang được chuẩn bị, đợi xử lý hoàn tất");
                return;
            }

            if (mediaPlayer == null) {
                String fileUrl = currentPlaylist.get(currentSongIndex).getFileUrl();
                startMusic(fileUrl);
            } else if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                isPlaying = false;
                playButton.setIconResource(R.drawable.ic_play);
                Log.d(TAG, "Paused");
            } else {
                mediaPlayer.start();
                isPlaying = true;
                playButton.setIconResource(R.drawable.ic_pause);
                startUpdatingSeekBar();
            }
        });

        nextBtn.setOnClickListener(v -> {
            if (currentPlaylist == null || currentPlaylist.isEmpty()) return;
            if (isPreparingMediaPlayer) {
                Log.d(TAG, "Đang chuẩn bị MediaPlayer, không thể chuyển bài");
                return;
            }
            if (mediaPlayer != null) {
                stopMusic();
            }
            currentSongIndex = (currentSongIndex + 1) % currentPlaylist.size();
            updateSongInfo(currentPlaylist.get(currentSongIndex));
            startMusic(currentPlaylist.get(currentSongIndex).getFileUrl());
        });

        previousBtn.setOnClickListener(v -> {
            if (currentPlaylist == null || currentPlaylist.isEmpty()) return;
            if (isPreparingMediaPlayer) {
                Log.d(TAG, "Đang chuẩn bị MediaPlayer, không thể chuyển bài");
                return;
            }
            if (mediaPlayer != null) {
                stopMusic();
            }
            currentSongIndex = (currentSongIndex - 1 + currentPlaylist.size()) % currentPlaylist.size();
            updateSongInfo(currentPlaylist.get(currentSongIndex));
            startMusic(currentPlaylist.get(currentSongIndex).getFileUrl());
        });

        seekBar.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser && mediaPlayer != null && mediaPlayer.getDuration() > 0) {
                int seekPosition = (int) (mediaPlayer.getDuration() * (value / 100f));
                mediaPlayer.seekTo(seekPosition);
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
        // performSearch sẽ hiển thị progressBar nếu page == 0
        performSearch(currentQuery, 0);
    }

    public void performNewSearch(String newQuery) {
        if (newQuery == null) {
            Log.w(TAG, "performNewSearch called with null query.");
            return;
        }
        Log.i(TAG, "performNewSearch called by Activity with query: " + newQuery);
        currentQuery = newQuery.trim();

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
            Log.d(TAG, "Search already in progress, ignoring request for query '" + queryToSearch + "' page " + page);
            return;
        }
        if (queryToSearch == null || queryToSearch.isEmpty()) {
            Log.d(TAG, "Empty query, ignoring search request. Clearing results.");
            resetSearchState();
            return;
        }
        // Kiểm tra songAPI đã được khởi tạo chưa
        if (songAPI == null) {
            if (getContext() != null) {
                songAPI = RetrofitService.getInstance(requireContext()).createService(SongAPI.class);
            } else {
                Log.e(TAG, "performSearch: songAPI is null and getContext() is null. Cannot perform search.");
                if (isAdded()) Toast.makeText(getActivity(), "Lỗi kết nối, vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                isLoading = false; // Reset loading flag
                return;
            }
        }


        Log.i(TAG, "Performing search - Query: '" + queryToSearch + "', Requesting Page: " + page);
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

                    Log.i(TAG, "Search response SUCCESS - Query: '" + queryToSearch + "', CurrentPage: " + currentPage +
                            ", TotalPages: " + totalPages + ", IsLast: " + isLastPage +
                            ", SongsOnPage: " + (songs != null ? songs.size() : 0));

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
                    Log.e(TAG, "Search API response ERROR - Query: '" + queryToSearch + "', Code: " + response.code() + ", Message: " + response.message());
                    if (page == 0) {
                        if (textViewNoResults != null) textViewNoResults.setVisibility(View.VISIBLE);
                        if (songAdapter != null) songAdapter.clear();
                    }
                    isLastPage = true; // Prevent further loading on error for this query
                    if(getContext() != null) Toast.makeText(getContext(), "Lỗi khi tải dữ liệu tìm kiếm.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PageWrapper<SongDTO>> call, @NonNull Throwable t) {
                if (!isAdded() || getContext() == null) return;
                isLoading = false;
                if (progressBarSearch != null) progressBarSearch.setVisibility(View.GONE);
                Log.e(TAG, "Search API call FAILURE - Query: '" + queryToSearch + "': " + t.getMessage(), t);
                if (page == 0) {
                    if (textViewNoResults != null) textViewNoResults.setVisibility(View.VISIBLE);
                    if (songAdapter != null) songAdapter.clear();
                }
                isLastPage = true; // Prevent further loading on failure for this query
                if(getContext() != null) Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showKeyboardAndFocus() {
        if (searchView == null || !isAdded() || getActivity() == null || !searchView.isAttachedToWindow()) {
            Log.w(TAG, "showKeyboardAndFocus: Conditions not met (searchView null, not added/attached, or activity null).");
            return;
        }
        searchView.post(() -> {
            if (!isAdded() || getActivity() == null || !searchView.isFocusable()) return;
            searchView.setIconified(false);
            if (searchView.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(searchView.findFocus(), InputMethodManager.SHOW_IMPLICIT);
                } else {
                    Log.w(TAG, "InputMethodManager is null");
                }
            } else {
                Log.w(TAG, "searchView.requestFocus() failed");
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
        } else {
            Log.w(TAG, "hideKeyboard: Conditions not met (imm null, view null, or window token null).");
        }
    }

    // --- XỬ LÝ CLICK VÀO BÀI HÁT ---
    @Override
    public void onSongClick(SongDTO song) {
        if(song == null) {
            Log.e(TAG, "onSongClick: song is null!");
            return;
        }
        Log.i(TAG, "Song clicked: ID=" + song.getId() + ", Title=" + song.getTitle());
        hideKeyboard();

        if (songAdapter == null) {
            Log.e(TAG, "onSongClick: songAdapter is null!");
            return;
        }

        currentPlaylist = songAdapter.getCurrentList();
        currentSongIndex = currentPlaylist.indexOf(song);

        if (currentSongIndex == -1) {
            Log.e(TAG, "onSongClick: Clicked song not found in current adapter list.");
            if(getContext() != null) Toast.makeText(getContext(), "Lỗi chọn bài hát.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isPlaying || isPreparingMediaPlayer) {
            stopMusic();
        }
        updateSongInfo(song);
        startMusic(song.getFileUrl());

        if (listener != null) {
            Log.d(TAG, "Notifying listener: onSearchResultSelected for " + song.getTitle());
            listener.onSearchResultSelected(song, currentPlaylist);
        }
    }
    // --- KẾT THÚC XỬ LÝ CLICK VÀO BÀI HÁT ---

    // --- LOGIC PHÁT NHẠC ---
    private void updateSongInfo(SongDTO song) {
        if (songTitleTextView != null) songTitleTextView.setText(song.getTitle());
        if (songArtistTextView != null) songArtistTextView.setText(song.getArtist());
    }

    private void startMusic(String url) {
        if (getContext() == null || !isAdded()) {
            Log.e(TAG, "startMusic: Context is null or fragment not added.");
            return;
        }
        if (url == null || url.isEmpty()) {
            Log.e(TAG, "startMusic: URL is null or empty.");
            if(getContext() != null) Toast.makeText(getContext(), "Không tìm thấy link nhạc.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isPreparingMediaPlayer && Objects.equals(currentPreparingUrl, url)) {
            Log.d(TAG, "Đã đang chuẩn bị phát URL này: " + url);
            return;
        }
        releasePlayer();
        isPreparingMediaPlayer = true;
        currentPreparingUrl = url;

        if (playButton != null) playButton.setEnabled(false);
        if (nextBtn != null) nextBtn.setEnabled(false);
        if (previousBtn != null) previousBtn.setEnabled(false);
        Log.d(TAG, "Bắt đầu chuẩn bị MediaPlayer cho: " + url);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.setOnPreparedListener(mp -> {
                if (!isAdded() || getContext() == null) {
                    isPreparingMediaPlayer = false;
                    currentPreparingUrl = null;
                    releasePlayer();
                    return;
                }
                isPreparingMediaPlayer = false;
                currentPreparingUrl = null;
                if (playButton != null) playButton.setEnabled(true);
                if (nextBtn != null) nextBtn.setEnabled(true);
                if (previousBtn != null) previousBtn.setEnabled(true);
                mp.start();
                isPlaying = true;
                if (playButton != null) playButton.setIconResource(R.drawable.ic_pause);
                if (seekBar != null) {
                    seekBar.setValue(0);
                    seekBar.setValueFrom(0);
                    seekBar.setValueTo(100);
                }
                int totalDuration = mp.getDuration();
                if (endTimeTextView != null) endTimeTextView.setText(formatTime(totalDuration));
                if (startTimeTextView != null) startTimeTextView.setText(formatTime(0));
                startUpdatingSeekBar();
                Log.d(TAG, "MediaPlayer đã sẵn sàng và đang phát: " + url);

                if (currentPlaylist != null && currentSongIndex >= 0 && currentSongIndex < currentPlaylist.size() && getContext() != null) {
                    long songId = currentPlaylist.get(currentSongIndex).getId();
                    // Đảm bảo songAPI được khởi tạo
                    if (songAPI == null) {
                        if (getContext() != null) {
                            songAPI = RetrofitService.getInstance(requireContext()).createService(SongAPI.class);
                        } else {
                            Log.e(TAG, "Cannot increment view: songAPI is null and getContext() is null.");
                            return; // Không thể gọi API nếu songAPI null
                        }
                    }
                    songAPI.incrementView(songId).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Log.d(TAG, "View count updated for song id: " + songId);
                            } else {
                                Log.w(TAG, "Failed to update view for song id: " + songId + ". Code: " + response.code());
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e(TAG, "Error updating view for song id: " + songId + ": " + t.getMessage());
                        }
                    });
                }
            });
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "MediaPlayer error: what=" + what + ", extra=" + extra + " for URL: " + (currentPreparingUrl != null ? currentPreparingUrl : "unknown"));
                isPreparingMediaPlayer = false;
                currentPreparingUrl = null;
                if (playButton != null) playButton.setEnabled(true);
                if (nextBtn != null) nextBtn.setEnabled(true);
                if (previousBtn != null) previousBtn.setEnabled(true);
                if (getContext() != null) Toast.makeText(getContext(), "Không thể phát bài hát này.", Toast.LENGTH_SHORT).show();
                releasePlayer();
                return true;
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                if (!isAdded()) return;
                isPlaying = false;
                if (playButton != null) playButton.setIconResource(R.drawable.ic_play);
                if (handler != null) handler.removeCallbacksAndMessages(null);
                if (seekBar != null) seekBar.setValue(0);
                if (startTimeTextView != null) startTimeTextView.setText(formatTime(0));
                if (currentPlaylist != null && !currentPlaylist.isEmpty()) {
                    Log.d(TAG, "Song completed. Playing next.");
                    currentSongIndex = (currentSongIndex + 1) % currentPlaylist.size();
                    updateSongInfo(currentPlaylist.get(currentSongIndex));
                    startMusic(currentPlaylist.get(currentSongIndex).getFileUrl());
                } else {
                    Log.d(TAG, "Song completed. No playlist or playlist empty. Releasing player.");
                    releasePlayer();
                }
            });
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e(TAG, "Exception during MediaPlayer setup for URL " + (url != null ? url : "null") + ": " + e.getMessage(), e);
            isPreparingMediaPlayer = false;
            currentPreparingUrl = null;
            if (playButton != null) playButton.setEnabled(true);
            if (nextBtn != null) nextBtn.setEnabled(true);
            if (previousBtn != null) previousBtn.setEnabled(true);
            if (getContext() != null) Toast.makeText(getContext(), "Lỗi khi chuẩn bị phát nhạc.", Toast.LENGTH_SHORT).show();
            releasePlayer();
        }
    }

    private void startUpdatingSeekBar() {
        if (handler == null || mediaPlayer == null || !isPlaying) { // Đã có isPlaying check
            return;
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && isPlaying && isAdded()) {
                    try {
                        int currentPosition = mediaPlayer.getCurrentPosition();
                        int duration = mediaPlayer.getDuration();
                        if (duration > 0) {
                            float progress = (currentPosition * 100f) / duration;
                            if (seekBar != null) seekBar.setValue(Math.min(progress, 100f));
                            if (startTimeTextView != null) startTimeTextView.setText(formatTime(currentPosition));
                        }
                        if(isPlaying) handler.postDelayed(this, 500);
                    } catch (IllegalStateException e) {
                        Log.e(TAG, "Error updating seek bar, MediaPlayer might be in an invalid state: " + e.getMessage());
                    }
                } else {
                    Log.d(TAG, "Stopping seek bar update. Player state valid: " + (mediaPlayer != null) + ", isPlaying: " + isPlaying + ", isAdded: " + isAdded());
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
        Log.d(TAG, "stopMusic called.");
        isPreparingMediaPlayer = false;
        currentPreparingUrl = null;
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.reset();
            } catch (IllegalStateException e) {
                Log.e(TAG, "IllegalStateException during stop/reset of MediaPlayer: " + e.getMessage());
            }
            mediaPlayer = null;
        }
        isPlaying = false;

        if (playButton != null) {
            playButton.setIconResource(R.drawable.ic_play);
            playButton.setEnabled(true);
        }
        if (nextBtn != null) nextBtn.setEnabled(true);
        if (previousBtn != null) previousBtn.setEnabled(true);
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (seekBar != null) seekBar.setValue(0);
        if (startTimeTextView != null) startTimeTextView.setText(formatTime(0));
    }

    private void releasePlayer() {
        Log.d(TAG, "releasePlayer called.");
        isPreparingMediaPlayer = false;
        currentPreparingUrl = null;
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.reset();
                mediaPlayer.release();
                Log.d(TAG, "MediaPlayer released.");
            } catch (Exception e) {
                Log.e(TAG, "Exception during MediaPlayer release: " + e.getMessage());
            }
            mediaPlayer = null;
        }
        isPlaying = false;
        if (playButton != null) {
            playButton.setIconResource(R.drawable.ic_play);
            playButton.setEnabled(true);
        }
        if (nextBtn != null) nextBtn.setEnabled(true);
        if (previousBtn != null) previousBtn.setEnabled(true);
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
    // --- KẾT THÚC LOGIC PHÁT NHẠC ---
}