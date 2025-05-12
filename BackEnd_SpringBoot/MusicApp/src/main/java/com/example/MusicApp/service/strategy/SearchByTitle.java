package com.example.MusicApp.service.strategy;

import com.example.MusicApp.model.Song;
import com.example.MusicApp.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SearchByTitle implements SearchStrategy {

    @Autowired
    private SongRepository songRepository;

    @Override
    public Page<Song> search(String keyword, Pageable pageable) {
        return songRepository.findByTitleContainingIgnoreCase(keyword, pageable);
    }
}
