package com.example.MusicApp.service;

import com.example.MusicApp.DTO.ArtistDTO;
import com.example.MusicApp.model.Artist;
import com.example.MusicApp.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;

@Service
public class ArtistService {

    @Autowired
    private ArtistRepository artistRepository;

    public List<ArtistDTO> getTop10Artists() {
        return artistRepository.findAllByOrderByStageNameAsc(PageRequest.of(0, 10))
                .stream()
                .map(a -> new ArtistDTO(a.getId(), a.getStageName()))
                .collect(Collectors.toList());
    }

}
