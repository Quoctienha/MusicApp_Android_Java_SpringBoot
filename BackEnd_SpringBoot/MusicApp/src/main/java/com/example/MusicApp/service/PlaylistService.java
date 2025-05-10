package com.example.MusicApp.service;

import com.example.MusicApp.DTO.PlaylistDTO;
import com.example.MusicApp.DTO.SongDTO;
import com.example.MusicApp.model.Customer;
import com.example.MusicApp.model.Playlist;
import com.example.MusicApp.model.Song;
import com.example.MusicApp.repository.CustomerRepository;
import com.example.MusicApp.repository.PlaylistRepository;
import com.example.MusicApp.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;
    private final CustomerRepository customerRepository;

    private Customer getLoggedInCustomer() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return customerRepository.findByAccountUsername(username)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    public List<PlaylistDTO> getAllPlaylists() {
        Customer customer = getLoggedInCustomer();
        return playlistRepository.findByCustomerId(customer.getId()).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public PlaylistDTO getPlaylistById(Long id) {
        Playlist playlist = getPlaylistByOwner(id);
        return convertToDTO(playlist);
    }

    @Transactional
    public PlaylistDTO createPlaylist(PlaylistDTO dto) {
        Customer customer = getLoggedInCustomer();
        List<Song> songs = songRepository.findAllById(dto.getSongIds());

        Playlist playlist = new Playlist();
        playlist.setName(dto.getName());
        playlist.setSongs(songs);
        playlist.setCustomer(customer);

        playlist = playlistRepository.save(playlist);
        return convertToDTO(playlist);
    }

    @Transactional
    public PlaylistDTO updatePlaylist(Long id, PlaylistDTO dto) {
        Playlist playlist = getPlaylistByOwner(id);
        playlist.setName(dto.getName());
        playlist.setSongs(songRepository.findAllById(dto.getSongIds()));
        return convertToDTO(playlistRepository.save(playlist));
    }

    @Transactional
    public void deletePlaylist(Long id) {
        Playlist playlist = getPlaylistByOwner(id);

        playlist.getSongs().clear();
        playlistRepository.save(playlist);

        playlistRepository.delete(playlist);
    }

    private Playlist getPlaylistByOwner(Long id) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        Customer current = getLoggedInCustomer();
        if (!playlist.getCustomer().getId().equals(current.getId())) {
            throw new RuntimeException("Access denied: Not your playlist");
        }
        return playlist;
    }

    private PlaylistDTO convertToDTO(Playlist playlist) {
        PlaylistDTO dto = new PlaylistDTO();
        dto.setId(playlist.getId());
        dto.setName(playlist.getName());
        dto.setSongIds(playlist.getSongs().stream().map(Song::getId).collect(Collectors.toList()));
        return dto;
    }

    @Transactional
    public PlaylistDTO addSongToPlaylist(Long playlistId, Long songId) {
        Playlist playlist = getPlaylistByOwner(playlistId);
        Song song = songRepository.findById(songId).orElseThrow(() -> new RuntimeException("Song not found"));

        if (!playlist.getSongs().contains(song)) {
            playlist.getSongs().add(song);
        }

        return convertToDTO(playlistRepository.save(playlist));
    }

    @Transactional
    public PlaylistDTO removeSongFromPlaylist(Long playlistId, Long songId) {
        Playlist playlist = getPlaylistByOwner(playlistId);
        Song song = songRepository.findById(songId).orElseThrow(() -> new RuntimeException("Song not found"));

        playlist.getSongs().remove(song);

        return convertToDTO(playlistRepository.save(playlist));
    }

    public List<SongDTO> getSongsInPlaylist(Long playlistId) {
        Playlist playlist = getPlaylistByOwner(playlistId);

        return playlist.getSongs().stream()
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



}
