package com.example.MusicApp.repository;

import com.example.MusicApp.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
    List<Artist> findAllByOrderByStageNameAsc(Pageable pageable);
}
