package edu.upenn.cit5940.processor;

import edu.upenn.cit5940.datastructures.HashMapInvertedIndex;
import edu.upenn.cit5940.model.Article;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchService {
    private final HashMapInvertedIndex index;
    private final Map<String, String> articleTitleById = new HashMap<>();

    public SearchService(List<Article> articles, Set<String> stopWords) {
        this.index = new HashMapInvertedIndex(stopWords);

        for (Article article : articles) {
            String id = article.getId();
            articleTitleById.put(id, article.getTitle());

            index.addDocument(id, article.getTitle());
            index.addDocument(id, article.getContent());
        }
    }

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
