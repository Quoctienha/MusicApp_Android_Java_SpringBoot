package com.example.MusicApp.strategy;

import com.example.MusicApp.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SongSearchStrategy {
    Page<Song> search(String query, Pageable pageable);
    String getSupportedType();
}