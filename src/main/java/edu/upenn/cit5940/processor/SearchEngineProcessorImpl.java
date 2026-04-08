package edu.upenn.cit5940.processor;

import edu.upenn.cit5940.model.Article;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchEngineProcessorImpl implements SearchEngineProcessor {
    private final SearchService searchService;
    private final ArticleLookupService articleLookupService;
    private final StatsService statsService;

    private final AutocompleteService autocompleteService;
    private final DateRangeService dateRangeService;
    private final TopicAnalysisService topicAnalysisService;

    public SearchEngineProcessorImpl(String dataFile, String logFile) throws Exception {
        List<Article> articles = DataBootstrap.loadArticles(dataFile, logFile);
        Set<String> stopWords = DataBootstrap.loadStopWords("stop_words.txt");

        this.searchService = new SearchService(articles, stopWords);
        this.articleLookupService = new ArticleLookupService(articles);
        this.statsService = new StatsService(articles);

        this.autocompleteService = new AutocompleteService(articles, stopWords);
        this.dateRangeService = new DateRangeService(articles);
        this.topicAnalysisService = new TopicAnalysisService(articles, stopWords);
    }

    @Override
    public List<String> searchTitlesByKeywords(String query) {
        return searchService.search(query);
    }

    @Override
    public List<String> autocomplete(String prefix) {
        return autocompleteService.autocomplete(prefix);
    }

    @Override
    public List<String> getTopTopics(YearMonth period) {
        return topicAnalysisService.getTopTopics(period);
    }

    @Override
    public Map<YearMonth, Integer> getTopicTrends(String topic, YearMonth start, YearMonth end) {
        return topicAnalysisService.getTopicTrends(topic, start, end);
    }

    @Override
    public List<String> getArticlesByDateRange(LocalDate start, LocalDate end) {
        return dateRangeService.getArticleTitlesByDateRange(start, end);
    }

    @Override
    public String getArticleDetailsById(String id) {
        return articleLookupService.getDetails(id);
    }

    @Override
    public String getStats() {
        return statsService.getStatsSummary();
    }
}