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

public class CsvArticleDataReader implements ArticleDataReader {

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
                    
                    logger.warn("Skipping CSV row " + lineNumber + ": unexpected error");
                }
            }
        }

        return articles;
    }

    private Article parseRow(Map<String, String> row, int lineNumber, AppLogger logger) {
        String id = safe(row.get("uri"));
        String title = safe(row.get("title"));
        String dateRaw = safe(row.get("date"));
        String content = safe(row.get("body"));

        
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

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}