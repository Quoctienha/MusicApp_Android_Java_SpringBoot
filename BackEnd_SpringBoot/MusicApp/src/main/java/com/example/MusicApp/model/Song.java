package com.example.MusicApp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "song")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne
    @JoinColumn(name = "artist_id") // FK tới User (Artist)
    private Artist artist;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "lyrics", columnDefinition = "TEXT")
    private String lyrics;

    private int likes;
    private int dislikes;
    private int views;


}