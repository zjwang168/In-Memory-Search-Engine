package edu.upenn.cit5940.processor;

import edu.upenn.cit5940.model.Article;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArticleLookupService {
    private final Map<String, Article> articleById = new HashMap<>();

    public ArticleLookupService(List<Article> articles) {
        for (Article article : articles) {
            articleById.put(article.getId(), article);
        }
    }

    public String getDetails(String id) {
        if (id == null || id.isBlank()) {
            return "Error: article id is required.";
        }

        Article article = articleById.get(id);
        if (article == null) {
            return "No article found for id: " + id;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(article.getId()).append(System.lineSeparator());
        sb.append("Date: ").append(article.getDate()).append(System.lineSeparator());
        sb.append("Title: ").append(article.getTitle()).append(System.lineSeparator());
        sb.append("Content: ").append(article.getContent());
        return sb.toString();
    }
}
