package com.example.MusicApp.service;

import com.example.MusicApp.DTO.SongDTO;
// import com.example.MusicApp.model.Artist; // <<< XÓA import này vì không dùng trực tiếp Artist nữa
import com.example.MusicApp.model.Song;
import com.example.MusicApp.model.User; // <<< THÊM import này nếu chưa có (cần cho convertToDTO)
import com.example.MusicApp.repository.SongRepository;
import com.example.MusicApp.strategy.SongSearchStrategy;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SongService {

    private final SongRepository songRepository;
    private final Map<String, SongSearchStrategy> searchStrategies = new HashMap<>();
    private final List<SongSearchStrategy> strategyList;

    @Autowired
    public SongService(SongRepository songRepository, @Lazy List<SongSearchStrategy> strategyList) {
        this.songRepository = songRepository;
        this.strategyList = strategyList;
    }

    @PostConstruct
    public void initializeSearchStrategies() {
        if (strategyList != null) {
            for (SongSearchStrategy strategy : strategyList) {
                searchStrategies.put(strategy.getSupportedType().toLowerCase(), strategy);
            }
            System.out.println("Initialized search strategies: " + searchStrategies.keySet());
        } else {
            System.err.println("Warning: No SongSearchStrategy beans found to initialize.");
        }
    }

    public Page<SongDTO> searchSongs(String query, String type, Pageable pageable) {
        String searchTypeKey = Optional.ofNullable(type)
                .map(String::toLowerCase)
                .filter(t -> !t.trim().isEmpty())
                .orElse("keyword");

        SongSearchStrategy selectedStrategy = searchStrategies.get(searchTypeKey);
        if (selectedStrategy == null) {
            System.out.println("Search strategy for type '" + searchTypeKey + "' not found. Attempting fallback to 'title'.");
            selectedStrategy = searchStrategies.get("title");
            if (selectedStrategy == null) {
                System.err.println("CRITICAL Error: Default search strategies ('keyword', 'title') are both missing!");
                return Page.empty(pageable);
            }
        }
        System.out.println("Using search strategy: " + selectedStrategy.getClass().getSimpleName()
                + " for query: '" + query + "' with effective type key: '" + selectedStrategy.getSupportedType().toLowerCase() + "'");

        Page<Song> songPage = selectedStrategy.search(query, pageable);
        return songPage.map(this::convertToDTO);
    }

    private SongDTO convertToDTO(Song song) {
        String artistName = "Unknown";

        User artistUser = song.getArtist();
        if (artistUser != null && artistUser.getFullName() != null && !artistUser.getFullName().trim().isEmpty()) {
            artistName = artistUser.getFullName();
        }
        else {
            System.out.println("Warning: Could not determine artist name for song ID: " + song.getId() + ", Title: " + song.getTitle());
        }

        return new SongDTO(
                song.getId(),
                song.getTitle(),
                artistName,
                song.getFileUrl(),
                song.getImageUrl(),
                song.getLyrics(),
                song.getDescription(),
                song.getLicense(),
                song.getLikes(),
                song.getDislikes(),
                song.getViews()
        );
    }

    public List<SongDTO> getAllSongs() {
        return songRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<SongDTO> getTop10TrendingSongs() {
        return songRepository.findTop10ByOrderByViewsDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void likeSong(Long id) {
        songRepository.findById(id).ifPresent(song -> {
            int currentLikes = Optional.ofNullable(song.getLikes()).orElse(0);
            song.setLikes(currentLikes + 1);
            songRepository.save(song);
        });
    }

    @Transactional
    public void dislikeSong(Long id) {
        songRepository.findById(id).ifPresent(song -> {
            int currentDislikes = Optional.ofNullable(song.getDislikes()).orElse(0);
            song.setDislikes(currentDislikes + 1);
            songRepository.save(song);
        });
    }

    @Transactional
    public void incrementView(Long id) {
        songRepository.findById(id).ifPresent(song -> {
            int currentViews = Optional.ofNullable(song.getViews()).orElse(0);
            song.setViews(currentViews + 1);
            songRepository.save(song);
        });
    }
}