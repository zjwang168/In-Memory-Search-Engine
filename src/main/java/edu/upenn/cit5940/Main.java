package edu.upenn.cit5940;

import edu.upenn.cit5940.processor.SearchEngineProcessor;
import edu.upenn.cit5940.processor.SearchEngineProcessorImpl;
import edu.upenn.cit5940.ui.CommandLineUI;

public class Main {
    public static void main(String[] args) {
        String dataFile = args.length >= 1 ? args[0] : "articles.csv";
        String logFile = args.length >= 2 ? args[1] : "tech_news_search.log";

        try {
            SearchEngineProcessor processor = new SearchEngineProcessorImpl(dataFile, logFile);
            CommandLineUI ui = new CommandLineUI(processor, dataFile);
            ui.start();
        } catch (Exception e) {
            System.out.println("Initialization failed: " + e.getMessage());
        }
    }
}