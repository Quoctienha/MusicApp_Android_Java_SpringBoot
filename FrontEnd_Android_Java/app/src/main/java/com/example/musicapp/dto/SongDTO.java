// SongDTO.java
package com.example.musicapp.dto;

import java.io.Serializable;

public class SongDTO implements Serializable {
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

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getFileUrl() { return fileUrl; }
    public String getImageUrl() { return imageUrl; }
    public String getLyrics() { return lyrics; }
    public int getLikes() { return likes; }
    public int getDislikes() { return dislikes; }
    public int getViews() { return views; }

    public String getDescription(){return description;}
    public String getLicense(){return license;}

    public void setLikes(int likes) { this.likes = likes; }
    public void setDislikes(int dislikes) { this.dislikes = dislikes; }
    public void setViews(int views) { this.views = views; }
}