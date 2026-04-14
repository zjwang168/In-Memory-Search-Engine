package edu.upenn.cit5940.processor;

import edu.upenn.cit5940.model.Article;

import java.time.LocalDate;
import java.util.List;

/**
 * Service responsible for computing basic dataset statistics.
 *
 * This class calculates:
 * - total number of articles
 * - earliest publication date
 * - latest publication date
 *
 * Results are precomputed during initialization for efficient retrieval.
 */
public class StatsService {
    /** Total number of articles */
    private final int totalArticles;

    /** Earliest publication date in dataset */
    private final LocalDate earliest;

    /** Latest publication date in dataset */
    private final LocalDate latest;

    /**
     * Constructs the statistics service and precomputes values.
     *
     * @param articles article dataset
     */
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

    /**
     * Returns a formatted summary of dataset statistics.
     *
     * @return formatted statistics string
     */
    public String getStatsSummary() {
        return "Total articles: " + totalArticles + System.lineSeparator()
                + "Date range: " + earliest + " to " + latest;
    }
}