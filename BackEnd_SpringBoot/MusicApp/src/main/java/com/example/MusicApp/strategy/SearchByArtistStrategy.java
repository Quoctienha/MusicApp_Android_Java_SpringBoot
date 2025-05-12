package com.example.MusicApp.strategy;

import com.example.MusicApp.model.Song;
import com.example.MusicApp.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SearchByArtistStrategy implements SongSearchStrategy {

    private final SongRepository songRepository;

    @Override
    public Page<Song> search(String query, Pageable pageable) {
        return songRepository.findByArtistFullNameContainingIgnoreCase(query, pageable);
    }

    @Override
    public String getSupportedType() {
        return "artist";
    }
}