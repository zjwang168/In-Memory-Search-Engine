package edu.upenn.cit5940;

import edu.upenn.cit5940.processor.SearchEngineProcessor;
import edu.upenn.cit5940.processor.SearchEngineProcessorImpl;
import edu.upenn.cit5940.ui.CommandLineUI;

/**
 * Application entry point for the Tech News Search Engine.
 *
 * Main is responsible only for:
 * - reading optional command-line arguments
 * - initializing the processor layer
 * - launching the UI layer
 *
 * This keeps startup logic minimal and preserves separation of concerns.
 */
public class Main {

    /**
     * Starts the application.
     *
     * Command-line arguments:
     * args[0] -> optional data file path
     * args[1] -> optional log file path
     *
     * If no arguments are provided, default file names are used.
     *
     * @param args optional command-line arguments
     */
    public static void main(String[] args) {
        String dataFile = args.length >= 1 ? args[0] : "articles.csv";
        String logFile = args.length >= 2 ? args[1] : "tech_news_search.log";

        try {
            // Initialize the processor layer and pass it to the UI.
            SearchEngineProcessor processor = new SearchEngineProcessorImpl(dataFile, logFile);
            CommandLineUI ui = new CommandLineUI(processor, dataFile);
            ui.start();
        } catch (Exception e) {
            // Startup failures are reported clearly to the user instead of crashing.
            System.out.println("Initialization failed: " + e.getMessage());
        }
    }
}