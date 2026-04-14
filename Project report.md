# 3.1 Project Report

## Part 1: Usage Instructions

### 1) Compilation (Command Line)

1. Open a terminal and go to the project folder:

```bash
cd In-Memory-Search-Engine
```

2. Compile everything with Maven:

```bash
mvn clean compile
```

3. If you want to run from command line with dependencies, package and copy runtime libs:

```bash
mvn clean package
mvn dependency:copy-dependencies -DincludeScope=runtime
```

### 2) Execution (Command Line)

Run this from the project root:

```bash
java -cp "target/classes;target/dependency/*" edu.upenn.cit5940.Main [dataFile] [logFile]
```

Both arguments are optional:

- `dataFile`: article file path
- `logFile`: log file path

Default behavior in `Main`:

- No `dataFile` given -> uses `articles.csv`
- No `logFile` given -> uses `tech_news_search.log`

Examples:

```bash
java -cp "target/classes;target/dependency/*" edu.upenn.cit5940.Main
java -cp "target/classes;target/dependency/*" edu.upenn.cit5940.Main sample_articles.csv
java -cp "target/classes;target/dependency/*" edu.upenn.cit5940.Main sample_articles.csv app.log
```

## Part 2: System Design

### System Architecture (n-tier)

I split the app into clear layers so each part has one job:

- Presentation tier (`ui`): handles user input, menus, and command output (`CommandLineUI`, `MenuPrinter`, `CommandParser`).
- Application/service tier (`processor`): coordinates features and business logic (`SearchEngineProcessorImpl`, search/autocomplete/topic/date/stats services).
- Data management tier (`datamanagement`): loads and validates input data (`CsvArticleDataReader`, `JsonArticleDataReader`, `DataBootstrap`).
- Domain + infrastructure tier (`model`, `datastructures`, `util`, `logging`): `Article` model, indexing structures, tokenization/date helpers, and logging.

This made debugging and changes much easier. For example, I could change parsing logic without rewriting UI code.

### Data Structures and Refactoring Rationale

#### Refactor to HashMap-based inverted index

I refactored search to use `HashMapInvertedIndex` (`Map<String, Set<String>>`) instead of scanning through lists:

- Near O(1) average lookup for each token.
- Fast multi-keyword search by intersecting posting sets.
- Better performance as data size increases.

Additionally, Stop-word filtering is applied before indexing using a predefined stop word list, ensuring that low-information tokens do not pollute the inverted index.

#### Other key structures

- `Trie` for autocomplete:
  - Fast prefix matching and clean autocomplete behavior.
- `TreeMap<LocalDate, List<Article>>` for date range queries:
  - Dates stay sorted automatically, and `subMap` makes range queries easy.
- Heap for top topics:
  - In `TopicAnalysisService`, a min-heap (`PriorityQueue`) keeps only top-k topics, so we do not sort everything.

### Design Patterns

#### 1) Strategy Pattern (data reader selection)

Why it fits:

- CSV and JSON parsing are interchangeable behaviors behind one interface.
- `DataBootstrap` picks the strategy by file extension.
- Adding another format later (like XML) is straightforward.

Snippet:

```java
ArticleDataReader reader;
if (name.endsWith(".csv")) {
    reader = new CsvArticleDataReader();
} else if (name.endsWith(".json")) {
    reader = new JsonArticleDataReader();
}
List<Article> articles = reader.read(dataPath, logger);
```

#### 2) Singleton Pattern (application logger)

Why it fits:

- Logging should be globally consistent and centralized.
- A single logger instance avoids conflicting file writers.
- Access from all tiers is simple and predictable.

Snippet:

```java
public final class AppLogger {
    private static final AppLogger INSTANCE = new AppLogger();
    private AppLogger() {}
    public static AppLogger getInstance() { return INSTANCE; }
}
```

#### 3) Facade Pattern (processor interface over services)

Why it fits:

- UI should not know internal service wiring details.
- `SearchEngineProcessorImpl` exposes one clean API while internally delegating to specialized services.

Snippet:

```java
public List<String> searchTitlesByKeywords(String query) {
    return searchService.search(query);
}
```

### Logging Implementation

Logging is used throughout startup, data loading, and error handling to make the application easier to debug and maintain.

Examples of meaningful logged events include:
- application startup
- selected input data file
- total number of loaded articles
- skipped malformed CSV/JSON records
- invalid input data encountered during parsing

This logging design improves observability without mixing logging logic into the UI. It also supports robustness because malformed records are recorded in the log while the application continues processing valid records.

### Code Readability and Quality

I organized the code into small, focused classes so that each class has a single primary responsibility. Naming follows Java conventions, methods are kept short where possible, and helper methods are used to reduce duplication. This makes the project easier to understand, test, and maintain.

Examples include:
- separate reader classes for CSV and JSON
- separate service classes for autocomplete, topics, date range queries, search, and stats
- a dedicated processor layer that keeps UI code separate from parsing and data structure logic
