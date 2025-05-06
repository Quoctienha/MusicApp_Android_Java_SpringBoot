package com.example.MusicApp.controller;

import com.example.MusicApp.DTO.SongDTO;
import com.example.MusicApp.DTO.SongRatingResponseDTO;
import com.example.MusicApp.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/song")
@CrossOrigin
public class SongController {

    @Autowired
    private SongService songService;


    @GetMapping("/")
    public List<SongDTO> getAllSongs() {
        return songService.getAllSongs();
    }

    @GetMapping("/trending")
    public List<SongDTO> getTrendingSongs() {
        return songService.getTop10TrendingSongs();
    }

    @PutMapping("/{id}/like")
    public ResponseEntity<String> likeSong(@PathVariable Long id) {
        return songService.addRating(id, true);
    }

    @PutMapping("/{id}/dislike")
    public ResponseEntity<String> dislikeSong(@PathVariable Long id) {
        return songService.addRating(id, false);
    }

    @GetMapping("/{songId}/rating")
    public ResponseEntity<SongRatingResponseDTO> getUserRatingForSong(@PathVariable Long songId) {
        // Lấy username từ SecurityContext
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Gọi service để lấy rating
        String ratingStatus = songService.getUserRatingForSong(username, songId);

        return ResponseEntity.ok(new SongRatingResponseDTO(ratingStatus));
    }


    @PutMapping("/{id}/view")
    public void incrementView(@PathVariable Long id) {
        songService.incrementView(id);
    }

}
