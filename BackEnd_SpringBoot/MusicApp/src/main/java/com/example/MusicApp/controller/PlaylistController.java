package com.example.MusicApp.controller;

import com.example.MusicApp.DTO.PlaylistDTO;
import com.example.MusicApp.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @GetMapping
    public List<PlaylistDTO> getAllPlaylists() {
        return playlistService.getAllPlaylists();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistDTO> getPlaylistById(@PathVariable Long id) {
        return ResponseEntity.ok(playlistService.getPlaylistById(id));
    }

    @PostMapping
    public ResponseEntity<PlaylistDTO> createPlaylist(@RequestBody PlaylistDTO dto) {
        return ResponseEntity.ok(playlistService.createPlaylist(dto));
    }

    @PutMapping("/{playlistId}/add-song/{songId}")
    public ResponseEntity<PlaylistDTO> addSongToPlaylist(@PathVariable Long playlistId, @PathVariable Long songId) {
        return ResponseEntity.ok(playlistService.addSongToPlaylist(playlistId, songId));
    }

    @PutMapping("/{playlistId}/remove-song/{songId}")
    public ResponseEntity<PlaylistDTO> removeSongFromPlaylist(@PathVariable Long playlistId, @PathVariable Long songId) {
        return ResponseEntity.ok(playlistService.removeSongFromPlaylist(playlistId, songId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlaylistDTO> updatePlaylist(@PathVariable Long id, @RequestBody PlaylistDTO dto) {
        return ResponseEntity.ok(playlistService.updatePlaylist(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlaylist(@PathVariable Long id) {
        playlistService.deletePlaylist(id);
        return ResponseEntity.ok().build();
    }
}
