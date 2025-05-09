package com.example.MusicApp.controller;

import com.example.MusicApp.DTO.SongDTO;
import com.example.MusicApp.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@CrossOrigin
public class SongController {

    private final SongService songService;

    @Autowired
    public SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping("/songs/search")
    public ResponseEntity<Page<SongDTO>> searchSongs(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "title") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            Page<SongDTO> songPage = songService.searchSongs(query, type, pageable);
            return ResponseEntity.ok(songPage);
        } catch (Exception e) {
            System.err.println("Error during song search: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/song")
    public List<SongDTO> getAllSongs() {
        return songService.getAllSongs();
    }

    @GetMapping("/song/trending")
    public List<SongDTO> getTrendingSongs() {
        return songService.getTop10TrendingSongs();
    }

    @PutMapping("/song/{id}/like")
    public ResponseEntity<Void> likeSong(@PathVariable Long id) {
        try {
            songService.likeSong(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Error liking song " + id + ": " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/song/{id}/dislike")
    public ResponseEntity<Void> dislikeSong(@PathVariable Long id) {
        try {
            songService.dislikeSong(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Error disliking song " + id + ": " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/song/{id}/view")
    public ResponseEntity<Void> incrementView(@PathVariable Long id) {
        try {
            songService.incrementView(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Error incrementing view for song " + id + ": " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}