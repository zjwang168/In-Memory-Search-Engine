package edu.upenn.cit5940.datamanagement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.upenn.cit5940.logging.AppLogger;
import edu.upenn.cit5940.model.Article;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class JsonArticleDataReader implements ArticleDataReader {
    @Override
    public List<Article> read(Path filePath, AppLogger logger) throws IOException {
        List<Article> articles = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(filePath.toFile());

        if (!root.isArray()) {
            logger.warn("JSON root is not an array, no articles loaded");
            return articles;
        }

        int index = 0;
        for (JsonNode node : root) {
            index++;
            Article article = parseNode(node, index, logger);
            if (article != null) {
                articles.add(article);
            }
        }

        return articles;
    }

    private Article parseNode(JsonNode node, int index, AppLogger logger) {
        String id = safe(node.path("uri").asText(null));
        String title = safe(node.path("title").asText(null));
        String dateRaw = safe(node.path("date").asText(null));
        String content = safe(node.path("body").asText(""));

        if (id.isBlank() || title.isBlank() || dateRaw.isBlank()) {
            logger.warn("Skipping JSON record " + index + ": missing essential field(s)");
            return null;
        }

        try {
            LocalDate date = LocalDate.parse(dateRaw);
            return new Article(id, title, date, content);
        } catch (DateTimeParseException e) {
            logger.warn("Skipping JSON record " + index + ": invalid date '" + dateRaw + "'");
            return null;
        }
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
