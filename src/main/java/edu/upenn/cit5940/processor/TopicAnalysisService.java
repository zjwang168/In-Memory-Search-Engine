package edu.upenn.cit5940.processor;

import edu.upenn.cit5940.model.Article;
import edu.upenn.cit5940.util.TextTokenizer;

import java.time.YearMonth;
import java.util.*;

public class TopicAnalysisService {
    private final List<Article> articles;
    private final Set<String> stopWords;

    public TopicAnalysisService(List<Article> articles, Set<String> stopWords) {
        this.articles = articles;
        this.stopWords = stopWords;
    }

    public List<String> getTopTopics(YearMonth period) {
        Map<String, Integer> freq = new HashMap<>();

        for (Article article : articles) {
            if (YearMonth.from(article.getDate()).equals(period)) {
                countWords(article.getTitle(), freq);
                countWords(article.getContent(), freq);
            }
        }

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

    public Map<YearMonth, Integer> getTopicTrends(String topic, YearMonth start, YearMonth end) {
        Map<YearMonth, Integer> trend = new LinkedHashMap<>();
        String normalizedTopic = topic.toLowerCase();

        YearMonth current = start;
        while (!current.isAfter(end)) {
            trend.put(current, 0);
            current = current.plusMonths(1);
        }

        for (Article article : articles) {
            YearMonth ym = YearMonth.from(article.getDate());
            if (ym.isBefore(start) || ym.isAfter(end)) continue;

            int count = countOccurrences(article.getTitle(), normalizedTopic)
                    + countOccurrences(article.getContent(), normalizedTopic);

            trend.put(ym, trend.get(ym) + count);
        }

        return trend;
    }

    private void countWords(String text, Map<String, Integer> freq) {
        for (String token : TextTokenizer.tokenize(text)) {
            if (token.length() <= 1) continue;
            if (stopWords.contains(token)) continue;
            freq.put(token, freq.getOrDefault(token, 0) + 1);
        }
    }

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