package edu.upenn.cit5940.processor;

import edu.upenn.cit5940.datamanagement.ArticleDataReader;
import edu.upenn.cit5940.datamanagement.CsvArticleDataReader;
import edu.upenn.cit5940.datamanagement.JsonArticleDataReader;
import edu.upenn.cit5940.logging.AppLogger;
import edu.upenn.cit5940.model.Article;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public final class DataBootstrap {
    private DataBootstrap() {
    }

    public static List<Article> loadArticles(String dataFile, String logFile) throws Exception {
        AppLogger logger = AppLogger.getInstance();
        logger.initialize(logFile);
        logger.info("Application starting");

        Path dataPath = resolveDataFilePath(dataFile);
        logger.info("Loading articles from " + dataPath);

        String name = dataPath.getFileName().toString().toLowerCase(Locale.ROOT);
        ArticleDataReader reader;
        if (name.endsWith(".csv")) {
            reader = new CsvArticleDataReader();
        } else if (name.endsWith(".json")) {
            reader = new JsonArticleDataReader();
        } else {
            throw new IllegalArgumentException("Unsupported data file extension: " + dataPath);
        }

        List<Article> articles = reader.read(dataPath, logger);
        logger.info("Loaded " + articles.size() + " articles");

        if (articles.isEmpty()) {
            throw new IllegalStateException("No valid articles found in " + dataPath);
        }

        return articles;
    }

    public static Set<String> loadStopWords(String stopWordsFile) throws IOException {
        Path stopWordsPath = resolveStopWordsPath(stopWordsFile);
        return Files.readAllLines(stopWordsPath)
                .stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(HashSet::new));
    }

    private static Path resolveDataFilePath(String configuredPath) {
        String raw = (configuredPath == null || configuredPath.isBlank())
                ? "articles.csv"
                : configuredPath;

        Path direct = Paths.get(raw);
        if (Files.exists(direct)) {
            return direct;
        }

        Path underSource = Paths.get("src/main/java/edu/upenn/cit5940").resolve(raw);
        if (Files.exists(underSource)) {
            return underSource;
        }

        if ("articles.csv".equals(raw)) {
            Path fallback = Paths.get("src/main/java/edu/upenn/cit5940/articles_small.csv");
            if (Files.exists(fallback)) {
                return fallback;
            }
        }

        throw new IllegalArgumentException("Data file not found: " + raw);
    }

    private static Path resolveStopWordsPath(String configuredPath) {
        String raw = (configuredPath == null || configuredPath.isBlank())
                ? "stop_words.txt"
                : configuredPath;

        Path direct = Paths.get(raw);
        if (Files.exists(direct)) {
            return direct;
        }

        Path underSource = Paths.get("src/main/java/edu/upenn/cit5940").resolve(raw);
        if (Files.exists(underSource)) {
            return underSource;
        }

        throw new IllegalArgumentException("Stop words file not found: " + raw);
    }
}
