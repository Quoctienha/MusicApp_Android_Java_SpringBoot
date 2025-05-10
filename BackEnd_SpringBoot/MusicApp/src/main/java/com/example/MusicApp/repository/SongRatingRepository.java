package com.example.MusicApp.repository;

import com.example.MusicApp.model.Account;
import com.example.MusicApp.model.Song;
import com.example.MusicApp.model.SongRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SongRatingRepository extends JpaRepository<SongRating, Long> {
    Optional<SongRating> findByAccountAndSong(Account account, Song song);

    int countBySongAndLikedTrue(Song song);   // Đếm số like
    int countBySongAndLikedFalse(Song song);  // Đếm số dislike
}

