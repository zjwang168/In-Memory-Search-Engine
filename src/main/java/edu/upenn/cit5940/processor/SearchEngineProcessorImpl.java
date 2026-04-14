package edu.upenn.cit5940.processor;

import edu.upenn.cit5940.model.Article;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Facade implementation of the processor layer.
 *
 * This class provides a single entry point for all user-facing features
 * while delegating the actual work to specialized service classes.
 *
 * It helps keep the UI layer simple and decoupled from internal
 * service wiring, which supports the application's n-tier architecture.
 */
public class SearchEngineProcessorImpl implements SearchEngineProcessor {
    private final SearchService searchService;
    private final ArticleLookupService articleLookupService;
    private final StatsService statsService;

    private final AutocompleteService autocompleteService;
    private final DateRangeService dateRangeService;
    private final TopicAnalysisService topicAnalysisService;

    /**
     * Initializes the processor layer by loading articles and stop words,
     * then constructing all feature-specific services.
     *
     * This centralizes setup logic in one place and ensures that the UI
     * only needs to interact with a single processor object.
     *
     * @param dataFile path to the article dataset file
     * @param logFile path to the log file
     * @throws Exception if required startup resources cannot be loaded
     */
    public SearchEngineProcessorImpl(String dataFile, String logFile) throws Exception {
        List<Article> articles = DataBootstrap.loadArticles(dataFile, logFile);
        Set<String> stopWords = DataBootstrap.loadStopWords("stop_words.txt");

        // Services responsible for search, article lookup, and statistics.
        this.searchService = new SearchService(articles, stopWords);
        this.articleLookupService = new ArticleLookupService(articles);
        this.statsService = new StatsService(articles);

        // Services responsible for autocomplete, date range queries, and topic analysis.
        this.autocompleteService = new AutocompleteService(articles, stopWords);
        this.dateRangeService = new DateRangeService(articles);
        this.topicAnalysisService = new TopicAnalysisService(articles, stopWords);
    }

    /**
     * Searches article titles using the search service.
     *
     * @param query user-entered search query
     * @return matching article titles
     */
    @Override
    public List<String> searchTitlesByKeywords(String query) {
        return searchService.search(query);
    }

    /**
     * Returns autocomplete suggestions for a prefix.
     *
     * @param prefix user-entered prefix
     * @return matching autocomplete suggestions
     */
    @Override
    public List<String> autocomplete(String prefix) {
        return autocompleteService.autocomplete(prefix);
    }

    /**
     * Returns top topics for a given year-month period.
     *
     * @param period target month for topic analysis
     * @return list of top topics
     */
    @Override
    public List<String> getTopTopics(YearMonth period) {
        return topicAnalysisService.getTopTopics(period);
    }

    /**
     * Returns the monthly frequency of a topic over a date range.
     *
     * @param topic target topic
     * @param start starting month
     * @param end ending month
     * @return map of month to topic frequency
     */
    @Override
    public Map<YearMonth, Integer> getTopicTrends(String topic, YearMonth start, YearMonth end) {
        return topicAnalysisService.getTopicTrends(topic, start, end);
    }

    /**
     * Returns article titles whose publication dates fall within the given range.
     *
     * @param start start date
     * @param end end date
     * @return article titles in the date range
     */
    @Override
    public List<String> getArticlesByDateRange(LocalDate start, LocalDate end) {
        return dateRangeService.getArticleTitlesByDateRange(start, end);
    }

    /**
     * Looks up a single article by its ID.
     *
     * @param id article identifier
     * @return formatted article details
     */
    @Override
    public String getArticleDetailsById(String id) {
        return articleLookupService.getDetails(id);
    }

    /**
     * Returns a summary of dataset statistics.
     *
     * @return formatted statistics summary
     */
    @Override
    public String getStats() {
        return statsService.getStatsSummary();
    }
}