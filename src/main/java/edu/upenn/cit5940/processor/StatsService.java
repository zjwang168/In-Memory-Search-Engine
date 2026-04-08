package edu.upenn.cit5940.processor;

import edu.upenn.cit5940.model.Article;

import java.time.LocalDate;
import java.util.List;

public class StatsService {
    private final int totalArticles;
    private final LocalDate earliest;
    private final LocalDate latest;

    public StatsService(List<Article> articles) {
        this.totalArticles = articles.size();

        LocalDate minDate = null;
        LocalDate maxDate = null;
        for (Article article : articles) {
            LocalDate d = article.getDate();
            if (minDate == null || d.isBefore(minDate)) {
                minDate = d;
            }
            if (maxDate == null || d.isAfter(maxDate)) {
                maxDate = d;
            }
        }

        this.earliest = minDate;
        this.latest = maxDate;
    }

    public String getStatsSummary() {
        return "Total articles: " + totalArticles + System.lineSeparator()
                + "Date range: " + earliest + " to " + latest;
    }
}
