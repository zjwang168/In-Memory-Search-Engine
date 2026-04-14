package edu.upenn.cit5940.processor;

import edu.upenn.cit5940.datastructures.HashMapInvertedIndex;
import edu.upenn.cit5940.model.Article;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service responsible for keyword-based article search.
 *
 * This class builds and queries a HashMap-based inverted index, which is a refactored
 * improvement over a BST-style search approach. It also maintains a mapping from
 * article ID to article title so that search results can be returned in user-friendly form.
 */
public class SearchService {
    /** Inverted index used for fast keyword search */
    private final HashMapInvertedIndex index;

    /** Maps article ID to article title for result display */
    private final Map<String, String> articleTitleById = new HashMap<>();

    /**
     * Builds the search service from the provided articles and stop words.
     *
     * Article titles and article content are both indexed so that keyword search
     * can match terms appearing in either field.
     *
     * @param articles article dataset
     * @param stopWords stop words excluded during indexing and query processing
     */
    public SearchService(List<Article> articles, Set<String> stopWords) {
        this.index = new HashMapInvertedIndex(stopWords);

        for (Article article : articles) {
            String id = article.getId();
            articleTitleById.put(id, article.getTitle());

            // Index both title and content to improve recall of search results.
            index.addDocument(id, article.getTitle());
            index.addDocument(id, article.getContent());
        }
    }

    /**
     * Searches the inverted index for articles matching the given query.
     *
     * Returned results are article titles sorted alphabetically for consistent output.
     *
     * @param query user-entered search query
     * @return list of matching article titles
     */
    public List<String> search(String query) {
        Set<String> ids = index.search(query);
        List<String> titles = new ArrayList<>();

        for (String id : ids) {
            String title = articleTitleById.get(id);
            if (title != null) {
                titles.add(title);
            }
        }

        titles.sort(String.CASE_INSENSITIVE_ORDER);
        return titles;
    }
}