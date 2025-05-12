package com.example.MusicApp.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PagedResponseDTO<T> {
    private List<T> content;
    private int page;
    private int totalPages;
    private long totalElements;
    private boolean isLast;
}
