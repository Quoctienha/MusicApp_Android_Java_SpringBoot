package com.example.MusicApp.strategy;

import com.example.MusicApp.model.Song;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.Optional;

/**
 * Context class for managing search strategies using the Strategy Pattern.
 * It selects the appropriate search strategy based on the type and executes the search.
 */
@RequiredArgsConstructor
public class SearchContext {

    private final Map<String, SongSearchStrategy> searchStrategies;

    /**
     * Executes the search using the appropriate strategy based on the type.
     *
     * @param query    The search query.
     * @param type     The type of search (e.g., "keyword", "title", "artist").
     * @param pageable The pagination information.
     * @return A page of songs matching the query.
     */
    public Page<Song> executeSearch(String query, String type, Pageable pageable) {
        SongSearchStrategy strategy = selectStrategy(type);
        return strategy.search(query, pageable);
    }

    /**
     * Selects the appropriate search strategy based on the type.
     * Falls back to "keyword" strategy if the type is invalid or not found.
     *
     * @param type The type of search.
     * @return The selected SongSearchStrategy.
     */
    private SongSearchStrategy selectStrategy(String type) {
        String searchTypeKey = Optional.ofNullable(type)
                .map(String::toLowerCase)
                .filter(t -> !t.trim().isEmpty())
                .orElse("keyword");

        SongSearchStrategy selectedStrategy = searchStrategies.get(searchTypeKey);
        if (selectedStrategy == null) {
            selectedStrategy = searchStrategies.get("keyword");
            if (selectedStrategy == null) {
                throw new IllegalStateException("No search strategy available for type: " + searchTypeKey);
            }
        }
        return selectedStrategy;
    }
}