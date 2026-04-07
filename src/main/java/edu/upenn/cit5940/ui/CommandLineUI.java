package edu.upenn.cit5940.ui;

import edu.upenn.cit5940.processor.SearchEngineProcessor;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CommandLineUI {
    private final SearchEngineProcessor processor;
    private final Scanner scanner;
    private final String dataFilePath;

    public CommandLineUI(SearchEngineProcessor processor, String dataFilePath) {
        this.processor = processor;
        this.dataFilePath = dataFilePath;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        printStartupMessages();
        mainMenuLoop();
    }

    private void printStartupMessages() {
        System.out.println("=== Tech News Search Engine ===");
        System.out.println("Initializing n-tier architecture...");
        System.out.println("Loading articles from: " + dataFilePath);
        // 这里最好从processor拿 count，更稳
        System.out.println("Articles loaded successfully.");
        System.out.println("Architecture initialization complete!");
    }

    private void mainMenuLoop() {
        while (true) {
            MenuPrinter.printMainMenu();
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Please enter a choice (1-4):");
                continue;
            }

            switch (input) {
                case "1" -> interactiveMode();
                case "2" -> commandMode();
                case "3" -> showHelp();
                case "4" -> {
                    exitProgram();
                    return;
                }
                default -> {
                    if (isNumeric(input)) {
                        System.out.println("Invalid choice. Please enter 1-4:");
                    } else {
                        System.out.println("Please enter a valid number (1-4):");
                    }
                }
            }
        }
    }

    private void interactiveMode() {
        while (true) {
            MenuPrinter.printInteractiveMenu();
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Please enter a choice (1-8):");
                continue;
            }

            switch (input) {
                case "1" -> doInteractiveSearch();
                case "2" -> doInteractiveAutocomplete();
                case "3" -> doInteractiveTopics();
                case "4" -> doInteractiveTrends();
                case "5" -> doInteractiveArticles();
                case "6" -> doInteractiveArticle();
                case "7" -> doInteractiveStats();
                case "8" -> { return; }
                default -> {
                    if (isNumeric(input)) {
                        System.out.println("Invalid choice. Please enter 1-8:");
                    } else {
                        System.out.println("Please enter a valid number (1-8):");
                    }
                }
            }
        }
    }

    private void commandMode() {
        MenuPrinter.printCommandModeHeader();

        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();

            if (line.isEmpty()) {
                System.out.println("Unknown command. Type 'help' for available commands.");
                continue;
            }

            if (line.equalsIgnoreCase("menu")) {
                return;
            }

            if (line.equalsIgnoreCase("help")) {
                MenuPrinter.printCommandHelp();
                continue;
            }

            handleCommand(line);
        }
    }

    private void handleCommand(String line) {
        String[] parts = line.split("\\s+");
        String cmd = parts[0].toLowerCase();

        try {
            switch (cmd) {
                case "search" -> {
                    if (parts.length < 2) {
                        System.out.println("Usage: search <keyword>");
                        return;
                    }
                    printLines(processor.searchTitlesByKeywords(line.substring(7).trim()));
                }
                case "autocomplete" -> {
                    if (parts.length < 2) {
                        System.out.println("Usage: autocomplete <prefix>");
                        return;
                    }
                    printLines(processor.autocomplete(parts[1]));
                }
                case "topics" -> {
                    if (parts.length != 2) {
                        System.out.println("Usage: topics <period>");
                        return;
                    }
                    YearMonth period = YearMonth.parse(parts[1]);
                    printLines(processor.getTopTopics(period));
                }
                case "trends" -> {
                    if (parts.length != 4) {
                        System.out.println("Usage: trends <topic> <start> <end>");
                        return;
                    }
                    String topic = parts[1];
                    YearMonth start = YearMonth.parse(parts[2]);
                    YearMonth end = YearMonth.parse(parts[3]);

                    if (start.isAfter(end)) {
                        System.out.println("Error: start period cannot be after end period.");
                        return;
                    }

                    Map<YearMonth, Integer> trends = processor.getTopicTrends(topic, start, end);
                    if (trends.isEmpty()) {
                        System.out.println("No data found.");
                    } else {
                        for (Map.Entry<YearMonth, Integer> e : trends.entrySet()) {
                            System.out.println(e.getKey() + ": " + e.getValue());
                        }
                    }
                }
                case "articles" -> {
                    if (parts.length != 3) {
                        System.out.println("Usage: articles <start_date> <end_date>");
                        return;
                    }
                    LocalDate start = LocalDate.parse(parts[1]);
                    LocalDate end = LocalDate.parse(parts[2]);

                    if (start.isAfter(end)) {
                        System.out.println("Error: start date cannot be after end date.");
                        return;
                    }

                    printLines(processor.getArticlesByDateRange(start, end));
                }
                case "article" -> {
                    if (parts.length != 2) {
                        System.out.println("Usage: article <id>");
                        return;
                    }
                    System.out.println(processor.getArticleDetailsById(parts[1]));
                }
                case "stats" -> System.out.println(processor.getStats());
                default -> System.out.println("Unknown command. Type 'help' for available commands.");
            }
        } catch (DateTimeParseException e) {
            System.out.println("Error: Invalid date provided.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void doInteractiveSearch() {
        System.out.println("Enter search keyword(s):");
        String keyword = scanner.nextLine().trim();
        System.out.println("Search results:");
        printLines(processor.searchTitlesByKeywords(keyword));
        promptEnterToReturnInteractive();
    }

    private void doInteractiveAutocomplete() {
        System.out.println("Enter prefix for autocomplete:");
        String prefix = scanner.nextLine().trim();
        System.out.println("Autocomplete suggestions:");
        printLines(processor.autocomplete(prefix));
        promptEnterToReturnInteractive();
    }

    private void doInteractiveTopics() {
        try {
            System.out.println("Enter period (YYYY-MM):");
            YearMonth period = YearMonth.parse(scanner.nextLine().trim());
            System.out.println("Top topics:");
            printLines(processor.getTopTopics(period));
        } catch (Exception e) {
            System.out.println("Error: Invalid date provided.");
        }
        promptEnterToReturnInteractive();
    }

    private void doInteractiveTrends() {
        try {
            System.out.println("Enter topic:");
            String topic = scanner.nextLine().trim();
            System.out.println("Enter start period (YYYY-MM):");
            YearMonth start = YearMonth.parse(scanner.nextLine().trim());
            System.out.println("Enter end period (YYYY-MM):");
            YearMonth end = YearMonth.parse(scanner.nextLine().trim());

            if (start.isAfter(end)) {
                System.out.println("Error: start period cannot be after end period.");
            } else {
                Map<YearMonth, Integer> trends = processor.getTopicTrends(topic, start, end);
                for (Map.Entry<YearMonth, Integer> e : trends.entrySet()) {
                    System.out.println(e.getKey() + ": " + e.getValue());
                }
            }
        } catch (Exception e) {
            System.out.println("Error: Invalid date provided.");
        }
        promptEnterToReturnInteractive();
    }

    private void doInteractiveArticles() {
        try {
            System.out.println("Enter start date (YYYY-MM-DD):");
            LocalDate start = LocalDate.parse(scanner.nextLine().trim());
            System.out.println("Enter end date (YYYY-MM-DD):");
            LocalDate end = LocalDate.parse(scanner.nextLine().trim());

            if (start.isAfter(end)) {
                System.out.println("Error: start date cannot be after end date.");
            } else {
                System.out.println("Articles results:");
                printLines(processor.getArticlesByDateRange(start, end));
            }
        } catch (Exception e) {
            System.out.println("Error: Invalid date provided.");
        }
        promptEnterToReturnInteractive();
    }

    private void doInteractiveArticle() {
        System.out.println("Enter article ID:");
        String id = scanner.nextLine().trim();
        System.out.println(processor.getArticleDetailsById(id));
        promptEnterToReturnInteractive();
    }

    private void doInteractiveStats() {
        System.out.println(processor.getStats());
        promptEnterToReturnInteractive();
    }

    private void showHelp() {
        MenuPrinter.printHelpMenu();
        scanner.nextLine();
    }

    private void exitProgram() {
        System.out.println("Thank you for using the Tech News Search Engine!");
        System.out.println("Goodbye!");
    }

    private void promptEnterToReturnInteractive() {
        System.out.println("Press ENTER to return to the main Interactive Mode menu");
        scanner.nextLine();
    }

    private void printLines(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            System.out.println("No articles found.");
            return;
        }
        for (String line : lines) {
            System.out.println(line);
        }
    }

    private boolean isNumeric(String s) {
        return s.matches("\\d+");
    }
}