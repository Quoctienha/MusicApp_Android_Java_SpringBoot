package com.example.MusicApp.DTO;

import com.example.MusicApp.model.Song;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongDTO {
    private Long id;
    private String title;
    private String artist;
    private String fileUrl;
    private String imageUrl;
    private String lyrics;
    private String description;
    private String license;
    private int likes;
    private int dislikes;
    private int views;

}

