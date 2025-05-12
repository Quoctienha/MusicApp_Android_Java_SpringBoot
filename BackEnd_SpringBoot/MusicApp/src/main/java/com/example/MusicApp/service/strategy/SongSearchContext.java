package com.example.MusicApp.service.strategy;

import com.example.MusicApp.model.Song;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@Data
public class SongSearchContext {
    private SearchStrategy strategy;

    public Page<Song> search(String keyword, Pageable pageable) {
        return strategy.search(keyword, pageable);
    }
}
