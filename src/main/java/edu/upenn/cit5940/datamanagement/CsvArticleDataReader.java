package edu.upenn.cit5940.datamanagement;

import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.CSVReaderHeaderAwareBuilder;
import edu.upenn.cit5940.logging.AppLogger;
import edu.upenn.cit5940.model.Article;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Reads article data from a CSV file and converts valid rows into Article objects.
 *
 * This class is a concrete Strategy implementation of ArticleDataReader.
 * It is responsible for:
 * - reading CSV records
 * - validating essential fields
 * - converting valid rows into Article objects
 * - skipping malformed rows while logging warnings
 */
public class CsvArticleDataReader implements ArticleDataReader {

    /**
     * Reads article data from the given CSV file.
     *
     * Malformed rows do not cause the application to fail. Instead, they are
     * logged and skipped so that valid records can still be loaded.
     *
     * @param filePath path to the CSV input file
     * @param logger application logger used to record parsing warnings
     * @return list of valid Article objects parsed from the CSV file
     * @throws IOException if the file cannot be opened or read
     */
    @Override
    public List<Article> read(Path filePath, AppLogger logger) throws IOException {
        List<Article> articles = new ArrayList<>();

        try (CSVReaderHeaderAware reader =
                     new CSVReaderHeaderAwareBuilder(new FileReader(filePath.toFile())).build()) {

            Map<String, String> row;
            int lineNumber = 1;

            while (true) {
                try {
                    row = reader.readMap();
                } catch (Exception e) {
                    // Skip malformed CSV rows instead of failing the entire application.
                    logger.warn("Skipping CSV row " + lineNumber + ": invalid CSV format");
                    lineNumber++;
                    continue;
                }

                if (row == null) {
                    break;
                }

                lineNumber++;

                try {
                    Article article = parseRow(row, lineNumber, logger);
                    if (article != null) {
                        articles.add(article);
                    }
                } catch (Exception e) {
                    // Defensive fallback in case an unexpected parsing issue occurs.
                    logger.warn("Skipping CSV row " + lineNumber + ": unexpected error");
                }
            }
        }

        return articles;
    }

    /**
     * Parses a single CSV row into an Article object.
     *
     * A row is considered invalid if it is missing essential fields
     * (id, title, or date) or if the date cannot be parsed.
     *
     * @param row the current CSV row as a header-value map
     * @param lineNumber current CSV line number for logging
     * @param logger application logger used to record validation issues
     * @return a valid Article object, or null if the row is invalid
     */
    private Article parseRow(Map<String, String> row, int lineNumber, AppLogger logger) {
        String id = safe(row.get("uri"));
        String title = safe(row.get("title"));
        String dateRaw = safe(row.get("date"));
        String content = safe(row.get("body"));

        // Essential fields are required to construct a meaningful Article object.
        if (id.isBlank() || title.isBlank() || dateRaw.isBlank()) {
            logger.warn("Skipping CSV row " + lineNumber + ": missing essential field(s)");
            return null;
        }

        try {
            LocalDate date = LocalDate.parse(dateRaw);
            return new Article(id, title, date, content);
        } catch (DateTimeParseException e) {
            logger.warn("Skipping CSV row " + lineNumber + ": invalid date '" + dateRaw + "'");
            return null;
        }
    }

    /**
     * Returns a trimmed string value, or an empty string if the input is null.
     *
     * @param value raw field value from the CSV row
     * @return trimmed non-null string
     */
    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}