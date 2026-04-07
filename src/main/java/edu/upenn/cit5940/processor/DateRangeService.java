package edu.upenn.cit5940.processor;

import edu.upenn.cit5940.model.Article;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DateRangeService {
    private final TreeMap<LocalDate, List<Article>> articleByDate = new TreeMap<>();

    public DateRangeService(List<Article> articles) {
        for (Article article : articles) {
            articleByDate
                .computeIfAbsent(article.getDate(), k -> new ArrayList<>())
                .add(article);
        }
    }

    public List<String> getArticleTitlesByDateRange(LocalDate start, LocalDate end) {
        List<String> results = new ArrayList<>();
        Map<LocalDate, List<Article>> range = articleByDate.subMap(start, true, end, true);

        for (Map.Entry<LocalDate, List<Article>> entry : range.entrySet()) {
            for (Article article : entry.getValue()) {
                results.add(article.getTitle());
            }
        }
        return results;
    }
}