package edu.upenn.cit5940.processor;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface SearchEngineProcessor {
    List<String> searchTitlesByKeywords(String query);          
    List<String> autocomplete(String prefix);                   
    List<String> getTopTopics(YearMonth period);                
    Map<YearMonth, Integer> getTopicTrends(String topic, YearMonth start, YearMonth end); 
    List<String> getArticlesByDateRange(LocalDate start, LocalDate end); 
    String getArticleDetailsById(String id);                    
    String getStats();                                          
}