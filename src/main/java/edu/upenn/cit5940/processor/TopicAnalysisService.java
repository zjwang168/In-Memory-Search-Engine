package edu.upenn.cit5940.processor;

import edu.upenn.cit5940.model.Article;
import edu.upenn.cit5940.util.TextTokenizer;

import java.time.YearMonth;
import java.util.*;

/**
 * Service responsible for topic analysis features.
 *
 * This class provides:
 * - top topic extraction for a given month
 * - monthly trend analysis for a given topic across a date range
 *
 * It uses tokenization and stop-word filtering to improve result quality,
 * and uses a heap-based approach for efficient top-k topic selection.
 */
public class TopicAnalysisService {
    /** Article dataset used for topic analysis */
    private final List<Article> articles;

    /** Stop words excluded from topic counting */
    private final Set<String> stopWords;

    /**
     * Constructs the topic analysis service.
     *
     * @param articles article dataset
     * @param stopWords stop words excluded during analysis
     */
    public TopicAnalysisService(List<Article> articles, Set<String> stopWords) {
        this.articles = articles;
        this.stopWords = stopWords;
    }

    /**
     * Returns the top topics for a given month.
     *
     * Topic frequency is computed from both article titles and article content.
     * A min-heap is used to keep only the top 10 results efficiently.
     *
     * @param period target year-month
     * @return list of formatted topic-frequency strings
     */
    public List<String> getTopTopics(YearMonth period) {
        Map<String, Integer> freq = new HashMap<>();

        for (Article article : articles) {
            if (YearMonth.from(article.getDate()).equals(period)) {
                countWords(article.getTitle(), freq);
                countWords(article.getContent(), freq);
            }
        }

        // Keep only the top 10 entries using a min-heap.
        PriorityQueue<Map.Entry<String, Integer>> minHeap =
                new PriorityQueue<>(Comparator
                        .comparingInt(Map.Entry<String, Integer>::getValue)
                        .thenComparing(Map.Entry::getKey));

        for (Map.Entry<String, Integer> entry : freq.entrySet()) {
            minHeap.offer(entry);
            if (minHeap.size() > 10) {
                minHeap.poll();
            }
        }

        List<Map.Entry<String, Integer>> top = new ArrayList<>(minHeap);
        top.sort(Comparator
                .comparingInt((Map.Entry<String, Integer> e) -> e.getValue()).reversed()
                .thenComparing(Map.Entry::getKey));

        List<String> results = new ArrayList<>();
        for (Map.Entry<String, Integer> e : top) {
            results.add(e.getKey() + " (" + e.getValue() + ")");
        }
        return results;
    }

    /**
     * Returns monthly topic frequency counts across a given date range.
     *
     * The returned map preserves chronological order using LinkedHashMap.
     *
     * @param topic target topic
     * @param start start year-month
     * @param end end year-month
     * @return ordered map of year-month to topic frequency
     */
    public Map<YearMonth, Integer> getTopicTrends(String topic, YearMonth start, YearMonth end) {
        Map<YearMonth, Integer> trend = new LinkedHashMap<>();
        String normalizedTopic = topic.toLowerCase();

        // Initialize all months in the requested range to zero.
        YearMonth current = start;
        while (!current.isAfter(end)) {
            trend.put(current, 0);
            current = current.plusMonths(1);
        }

        for (Article article : articles) {
            YearMonth ym = YearMonth.from(article.getDate());
            if (ym.isBefore(start) || ym.isAfter(end)) {
                continue;
            }

            int count = countOccurrences(article.getTitle(), normalizedTopic)
                    + countOccurrences(article.getContent(), normalizedTopic);

            trend.put(ym, trend.get(ym) + count);
        }

        return trend;
    }

    /**
     * Tokenizes text and updates word frequency counts.
     *
     * Very short tokens and stop words are ignored to improve topic quality.
     *
     * @param text input text
     * @param freq frequency map to update
     */
    private void countWords(String text, Map<String, Integer> freq) {
        for (String token : TextTokenizer.tokenize(text)) {
            if (token.length() <= 1) {
                continue;
            }
            if (stopWords.contains(token)) {
                continue;
            }
            freq.put(token, freq.getOrDefault(token, 0) + 1);
        }
    }

    /**
     * Counts exact token matches of a topic within a text.
     *
     * @param text input text
     * @param topic normalized topic to match
     * @return number of occurrences
     */
    private int countOccurrences(String text, String topic) {
        int count = 0;
        for (String token : TextTokenizer.tokenize(text)) {
            if (token.equals(topic)) {
                count++;
            }
        }
        return count;
    }
}