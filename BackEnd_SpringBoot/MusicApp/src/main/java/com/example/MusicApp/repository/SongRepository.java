package com.example.MusicApp.repository;

import com.example.MusicApp.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findTop10ByOrderByViewsDesc();
    Page<Song> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Song> findByArtist_StageNameContainingIgnoreCase(String artistStageName, Pageable pageable);

    Page<Song> findByTitleContainingIgnoreCaseOrArtist_StageNameContainingIgnoreCase(
            String title, String artistStageName, Pageable pageable
    );
}

