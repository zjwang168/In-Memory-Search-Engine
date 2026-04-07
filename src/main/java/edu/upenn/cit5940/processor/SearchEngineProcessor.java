package edu.upenn.cit5940.processor;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface SearchEngineProcessor {
    List<String> searchTitlesByKeywords(String query);          // 何成
    List<String> autocomplete(String prefix);                   // Mango
    List<String> getTopTopics(YearMonth period);                // 何成
    Map<YearMonth, Integer> getTopicTrends(String topic, YearMonth start, YearMonth end); // 你
    List<String> getArticlesByDateRange(LocalDate start, LocalDate end); // Mango
    String getArticleDetailsById(String id);                    // 何成
    String getStats();                                          // 何成
}