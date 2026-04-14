package edu.upenn.cit5940.datamanagement;

import edu.upenn.cit5940.logging.AppLogger;
import edu.upenn.cit5940.model.Article;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Strategy interface for reading article data from different file formats.
 *
 * Implementations are responsible for:
 * - parsing the input file
 * - validating data
 * - converting records into Article objects
 *
 * This interface enables flexible data source handling and supports
 * the Strategy design pattern used in DataBootstrap.
 */
public interface ArticleDataReader {

    /**
     * Reads article data from the given file path.
     *
     * @param filePath the path to the input data file
     * @param logger   application logger for recording parsing issues
     * @return list of valid Article objects
     * @throws IOException if file access fails
     */
    List<Article> read(Path filePath, AppLogger logger) throws IOException;
}
