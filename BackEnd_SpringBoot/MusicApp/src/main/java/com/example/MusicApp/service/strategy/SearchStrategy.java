package com.example.MusicApp.service.strategy;

import com.example.MusicApp.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchStrategy {
    Page<Song> search(String keyword, Pageable pageable);
}
