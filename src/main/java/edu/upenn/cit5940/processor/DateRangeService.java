package edu.upenn.cit5940.processor;

import edu.upenn.cit5940.model.Article;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Service responsible for date range article queries.
 *
 * Articles are indexed by publication date using a TreeMap so that:
 * - dates remain naturally sorted
 * - range queries can be performed efficiently with subMap()
 *
 * This class supports the HW8 integration requirement for TreeMap-based
 * date range lookup functionality.
 */
public class DateRangeService {
    /** Maps publication date to the list of articles published on that date */
    private final TreeMap<LocalDate, List<Article>> articleByDate = new TreeMap<>();

    /**
     * Builds a date-based index from the provided article list.
     *
     * @param articles article dataset
     */
    public DateRangeService(List<Article> articles) {
        for (Article article : articles) {
            articleByDate
                    .computeIfAbsent(article.getDate(), k -> new ArrayList<>())
                    .add(article);
        }
    }

    /**
     * Returns article titles whose publication dates fall within the given range.
     *
     * The range is inclusive of both the start and end dates.
     *
     * @param start start date
     * @param end end date
     * @return list of article titles in chronological order by date
     */
    public List<String> getArticleTitlesByDateRange(LocalDate start, LocalDate end) {
        List<String> results = new ArrayList<>();

        // TreeMap.subMap enables efficient inclusive date range retrieval.
        Map<LocalDate, List<Article>> range = articleByDate.subMap(start, true, end, true);

        for (Map.Entry<LocalDate, List<Article>> entry : range.entrySet()) {
            for (Article article : entry.getValue()) {
                results.add(article.getTitle());
            }
        }

        return results;
    }
}