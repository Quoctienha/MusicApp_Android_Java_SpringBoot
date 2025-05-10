package com.example.MusicApp.service;

import com.example.MusicApp.DTO.SongDTO;
import com.example.MusicApp.DTO.SongRatingResponseDTO;
import com.example.MusicApp.model.Account;
import com.example.MusicApp.model.Song;
import com.example.MusicApp.model.SongRating;
import com.example.MusicApp.repository.AccountRepository;
import com.example.MusicApp.repository.SongRatingRepository;
import com.example.MusicApp.repository.SongRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Data
public class SongService {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private SongRatingRepository ratingRepository;

    @Autowired
    private AccountRepository accountRepository;

    public List<SongDTO> getAllSongs() {
        return songRepository.findAll()
                .stream()
                .map(song -> new SongDTO(
                        song.getId(),
                        song.getTitle(),
                        song.getArtist() != null ? song.getArtist().getStageName() : "Unknown",
                        song.getFileUrl(),
                        song.getImageUrl(),
                        song.getLyrics(),
                        song.getDescription(),
                        song.getLicense(),
                        song.getLikes(),
                        song.getDislikes(),
                        song.getViews()
                ))
                .collect(Collectors.toList());
    }

    public List<SongDTO> getTop10TrendingSongs() {
        return songRepository.findTop10ByOrderByViewsDesc()
                .stream()
                .map(song -> new SongDTO(
                        song.getId(),
                        song.getTitle(),
                        song.getArtist() != null ? song.getArtist().getStageName() : "Unknown",
                        song.getFileUrl(),
                        song.getImageUrl(),
                        song.getLyrics(),
                        song.getDescription(),
                        song.getLicense(),
                        song.getLikes(),
                        song.getDislikes(),
                        song.getViews()
                ))
                .collect(Collectors.toList());
    }

    public SongRatingResponseDTO addRating(Long songId, boolean isLike) {
        //Lấy username từ SecurityContext
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //Tìm Account từ repository
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Kiểm tra bài hát
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new EntityNotFoundException("Song not found"));

        // Kiểm tra xem người dùng đã đánh giá bài hát chưa
        Optional<SongRating> existingRatingOptional = ratingRepository.findByAccountAndSong(account, song);

        if (existingRatingOptional.isPresent()) {
            // Nếu đã đánh giá, cập nhật lại rating
            SongRating existingRating = existingRatingOptional.get();
            existingRating.setLiked(isLike);
            ratingRepository.save(existingRating);
        } else {
            // Nếu chưa đánh giá, tạo mới rating
            SongRating newRating = new SongRating();
            newRating.setAccount(account);
            newRating.setSong(song);
            newRating.setLiked(isLike);
            ratingRepository.save(newRating);
        }

        // Cập nhật lại tổng số like/dislike cho bài hát
        updateSongRatingCounts(song);

        return new SongRatingResponseDTO("Rating updated");
    }

    private void updateSongRatingCounts(Song song) {
        int likeCount = ratingRepository.countBySongAndLikedTrue(song);
        int dislikeCount = ratingRepository.countBySongAndLikedFalse(song);

        song.setLikes(likeCount);
        song.setDislikes(dislikeCount);
        songRepository.save(song);
    }

    public String getUserRatingForSong(String username, Long songId) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new IllegalStateException("Song not found"));

        Optional<SongRating> ratingOptional = ratingRepository.findByAccountAndSong(account, song);

        if (ratingOptional.isPresent()) {
            SongRating rating = ratingOptional.get();
            // Kiểm tra null cho 'liked'
            if (rating.getLiked() == null) {
                return "none"; // Xử lý trường hợp null
            }
            return rating.getLiked() ? "like" : "dislike";
        } else {
            return "none"; // chưa đánh giá
        }
    }

    public void incrementView(Long id) {
        songRepository.findById(id).ifPresent(song -> {
            song.setViews(song.getViews() + 1);
            songRepository.save(song);
        });
    }

}