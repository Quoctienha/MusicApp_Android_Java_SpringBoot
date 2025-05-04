package com.example.MusicApp.controller;

import com.example.MusicApp.DTO.SongDTO;
import com.example.MusicApp.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@RequestMapping("/api/songs")
@CrossOrigin
public class SongController {

    @Autowired
    private SongService songService;

    @GetMapping("/song")
    public List<SongDTO> getAllSongs() {
        return songService.getAllSongs();
    }

    @GetMapping("/song/trending")
    public List<SongDTO> getTrendingSongs() {
        return songService.getTop10TrendingSongs();
    }

    @PutMapping("/song/{id}/like")
    public void likeSong(@PathVariable Long id) {
        songService.likeSong(id);
    }

    @PutMapping("/song/{id}/dislike")
    public void dislikeSong(@PathVariable Long id) {
        songService.dislikeSong(id);
    }

    @PutMapping("/song/{id}/view")
    public void incrementView(@PathVariable Long id) {
        songService.incrementView(id);
    }

}
