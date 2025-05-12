package com.example.MusicApp.service;


import com.example.MusicApp.DTO.SongDTO;

import com.example.MusicApp.DTO.SongRatingResponseDTO;

import com.example.MusicApp.model.Account;

import com.example.MusicApp.model.Song;

import com.example.MusicApp.model.SongRating;

import com.example.MusicApp.repository.AccountRepository;

import com.example.MusicApp.repository.SongRatingRepository;

import com.example.MusicApp.repository.SongRepository;

import com.example.MusicApp.strategy.SearchContext; // Thêm import cho SearchContext

import com.example.MusicApp.strategy.SongSearchStrategy;

import jakarta.annotation.PostConstruct;

import jakarta.persistence.EntityNotFoundException;

import lombok.Data;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Lazy;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;


import java.util.HashMap;

import java.util.List;

import java.util.Map;

import java.util.Optional;

import java.util.stream.Collectors;


@Service

@Data

public class SongService {


    @Autowired

    private SongRepository songRepository;


    @Autowired

    private SongRatingRepository ratingRepository;


    @Autowired

    private AccountRepository accountRepository;


    private final Map<String, SongSearchStrategy> searchStrategies = new HashMap<>();

    private final List<SongSearchStrategy> strategyList;


    private SearchContext searchContext; // Thêm SearchContext


    @Autowired

    public SongService(@Lazy List<SongSearchStrategy> strategyList) {

        this.strategyList = strategyList;

    }


    @PostConstruct

    public void initializeSearchStrategies() {

        if (strategyList != null) {

            for (SongSearchStrategy strategy : strategyList) {

                if (strategy.getSupportedType() != null) {

                    searchStrategies.put(strategy.getSupportedType().toLowerCase(), strategy);

                } else {

                    System.err.println("Warning: SongSearchStrategy bean " + strategy.getClass().getName() + " has null supportedType.");

                }

            }

            System.out.println("Initialized search strategies: " + searchStrategies.keySet());

// Khởi tạo SearchContext sau khi searchStrategies được điền

            this.searchContext = new SearchContext(searchStrategies);

        } else {

            System.err.println("Warning: No SongSearchStrategy beans found to initialize.");

        }

    }


    private SongDTO convertToDTO(Song song) {

        if (song == null) {

            return null;

        }

        String artistName = (song.getArtist() != null && song.getArtist().getStageName() != null)

                ? song.getArtist().getStageName()

                : "Unknown Artist";


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


    public Page<SongDTO> searchSongs(String query, String type, Pageable pageable) {

        if (searchContext == null) {

            System.err.println("CRITICAL Error: SearchContext is not initialized. Returning empty page.");

            return Page.empty(pageable);

        }

        Page<Song> songPage = searchContext.executeSearch(query, type, pageable);

        return songPage.map(this::convertToDTO);

    }


    @Transactional

    public SongRatingResponseDTO addRating(Long songId, boolean isLike) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if (username == null || username.equals("anonymousUser")) {

            throw new IllegalStateException("User must be authenticated to rate a song.");

        }


        Account account = accountRepository.findByUsername(username)

                .orElseThrow(() -> new EntityNotFoundException("User account not found for username: " + username));


        Song song = songRepository.findById(songId)

                .orElseThrow(() -> new EntityNotFoundException("Song not found with id: " + songId));


        Optional<SongRating> existingRatingOptional = ratingRepository.findByAccountAndSong(account, song);


        SongRating rating;

        if (existingRatingOptional.isPresent()) {

            rating = existingRatingOptional.get();

            rating.setLiked(isLike);

        } else {

            rating = new SongRating();

            rating.setAccount(account);

            rating.setSong(song);

            rating.setLiked(isLike);

        }

        ratingRepository.save(rating);

        updateSongRatingCounts(song);


        String newStatus = rating.getLiked() != null ? (rating.getLiked() ? "like" : "dislike") : "none";

        return new SongRatingResponseDTO(newStatus);

    }


    private void updateSongRatingCounts(Song song) {

        int likeCount = ratingRepository.countBySongAndLikedTrue(song);

        int dislikeCount = ratingRepository.countBySongAndLikedFalse(song);


        song.setLikes(likeCount);

        song.setDislikes(dislikeCount);

        songRepository.save(song);

    }


    public String getUserRatingForSong(String username, Long songId) {

        if (username == null || username.equals("anonymousUser")) {

            return "none";

        }

        Account account = accountRepository.findByUsername(username)

                .orElseThrow(() -> new EntityNotFoundException("User account not found for username: " + username));


        Song song = songRepository.findById(songId)

                .orElseThrow(() -> new EntityNotFoundException("Song not found with id: " + songId));


        Optional<SongRating> ratingOptional = ratingRepository.findByAccountAndSong(account, song);


        if (ratingOptional.isPresent()) {

            SongRating rating = ratingOptional.get();

            if (rating.getLiked() == null) {

                return "none";

            }

            return rating.getLiked() ? "like" : "dislike";

        } else {

            return "none";

        }

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