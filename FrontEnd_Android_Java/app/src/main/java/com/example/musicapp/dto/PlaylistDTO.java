package com.example.musicapp.dto;

import java.util.List;

public class PlaylistDTO {
    private Long id;
    private String name;
    private List<Long> songIds;

    // Getters và Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Long> getSongIds() { return songIds; }
    public void setSongIds(List<Long> songIds) { this.songIds = songIds; }
}
