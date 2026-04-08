package edu.upenn.cit5940.datamanagement;

import edu.upenn.cit5940.logging.AppLogger;
import edu.upenn.cit5940.model.Article;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface ArticleDataReader {
    List<Article> read(Path filePath, AppLogger logger) throws IOException;
}
