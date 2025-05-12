package com.example.MusicApp.repository;

import com.example.MusicApp.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {

    List<Song> findTop10ByOrderByViewsDesc();

    Page<Song> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query("SELECT s FROM Song s JOIN s.artist u WHERE LOWER(u.fullName) LIKE LOWER(concat('%', :artistName, '%'))")
    Page<Song> findByArtistFullNameContainingIgnoreCase(@Param("artistName") String artistName, Pageable pageable);
    @Query("SELECT s FROM Song s JOIN s.artist u WHERE " +
            "LOWER(s.title) LIKE LOWER(concat('%', :keyword, '%')) OR " +
            "LOWER(u.fullName) LIKE LOWER(concat('%', :keyword, '%')) OR " +
            "LOWER(u.stageName) LIKE LOWER(concat('%', :keyword, '%'))")
    Page<Song> findByKeywordIgnoreCase(@Param("keyword") String keyword, Pageable pageable);

}