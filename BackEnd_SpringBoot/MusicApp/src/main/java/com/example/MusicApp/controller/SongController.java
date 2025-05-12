package com.example.MusicApp.controller;

import com.example.MusicApp.DTO.SongDTO;
import com.example.MusicApp.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page; // Thêm import cho Page
import org.springframework.data.domain.PageRequest; // Thêm import cho PageRequest
import org.springframework.data.domain.Pageable; // Thêm import cho Pageable
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/song") // Giữ nguyên từ Đoạn 2
@CrossOrigin
public class SongController {

    @Autowired // Giữ nguyên field injection từ Đoạn 2
    private SongService songService;

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
    @GetMapping("/trending")
    public List<SongDTO> getTrendingSongs() {
        return songService.getTop10TrendingSongs();
    }


    @GetMapping("/search")
    public ResponseEntity<Page<SongDTO>> searchSongs(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "title") String type, // 'type' có thể là title, artist, keyword
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<SongDTO> songPage = songService.searchSongs(query, type, pageable);
            return ResponseEntity.ok(songPage);
        } catch (Exception e) {
            // Log lỗi ở phía server để dễ dàng debug
            System.err.println("Lỗi trong quá trình tìm kiếm bài hát: query=" + query + ", type=" + type + ", page=" + page + ", size=" + size + " - Exception: " + e.getMessage());
            e.printStackTrace(); // In stack trace ra console của server
            // Trả về lỗi 500 cho client
            return ResponseEntity.internalServerError().body(null); // Có thể trả về một đối tượng lỗi cụ thể nếu muốn
        }
    }

    @PutMapping("/{id}/like")
    public ResponseEntity<SongRatingResponseDTO> likeSong(@PathVariable Long id) {
        return ResponseEntity.ok(songService.addRating(id, true));
    }

    /**
     * Không thích một bài hát.
     * Từ Đoạn 2.
     */
    @PutMapping("/{id}/dislike")
    public ResponseEntity<SongRatingResponseDTO> dislikeSong(@PathVariable Long id) {
        return ResponseEntity.ok(songService.addRating(id, false));
    }

    /**
     * Lấy trạng thái like/dislike của người dùng hiện tại cho một bài hát.
     * Từ Đoạn 2.
     */
    @GetMapping("/{songId}/rating") // Đường dẫn này từ Đoạn 2
    public ResponseEntity<SongRatingResponseDTO> getUserRatingForSong(@PathVariable Long songId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String ratingStatus = songService.getUserRatingForSong(username, songId);
        return ResponseEntity.ok(new SongRatingResponseDTO(ratingStatus));
    }

    /**
     * Tăng lượt xem cho một bài hát.
     * Từ Đoạn 2 (giữ nguyên kiểu trả về void).
     */
    @PutMapping("/{id}/view")
    public void incrementView(@PathVariable Long id) {
        songService.incrementView(id);
    }
}

