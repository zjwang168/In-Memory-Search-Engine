package edu.upenn.cit5940.datastructures;

import edu.upenn.cit5940.util.TextTokenizer;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * HashMap-based inverted index for keyword search.
 *
 * This class maps each normalized token to the set of document IDs
 * in which that token appears. It is used to support efficient keyword
 * search and satisfies the HW6 refactoring requirement of replacing
 * a BST-style structure with a HashMap-based approach.
 *
 * Stop-word filtering is applied during both indexing and query processing
 * so that low-information words do not affect relevance or efficiency.
 */
public class HashMapInvertedIndex {
    /** Maps token -> set of document IDs containing that token */
    private final Map<String, Set<String>> index = new HashMap<>();

    /** Stop words excluded from indexing and searching */
    private final Set<String> stopWords;

    /**
     * Constructs an inverted index with the provided stop word set.
     *
     * @param stopWords stop words to ignore during indexing and search
     */
    public HashMapInvertedIndex(Set<String> stopWords) {
        this.stopWords = stopWords == null ? Collections.emptySet() : stopWords;
    }

    /**
     * Adds a document to the inverted index.
     *
     * The text is tokenized and normalized before insertion. Tokens that
     * are too short or appear in the stop word list are ignored.
     *
     * @param docId unique document identifier
     * @param text document text to index
     */
    public void addDocument(String docId, String text) {
        if (docId == null || docId.isBlank() || text == null || text.isBlank()) {
            return;
        }

        List<String> words = TextTokenizer.tokenize(text);
        for (String word : words) {
            if (word.length() <= 1 || stopWords.contains(word)) {
                continue;
            }

            index.computeIfAbsent(word, ignored -> new HashSet<>()).add(docId);
        }
    }

    /**
     * Searches the inverted index using AND-style keyword matching.
     *
     * Multiple query tokens are processed by intersecting their posting sets,
     * so only documents containing all query terms are returned.
     *
     * Tokens that are too short or appear in the stop word list are ignored.
     *
     * @param query user-entered search query
     * @return set of matching document IDs
     */
    public Set<String> search(String query) {
        if (query == null || query.isBlank()) {
            return Collections.emptySet();
        }

        List<String> tokens = TextTokenizer.tokenize(query);
        Set<String> result = null;

        for (String token : tokens) {
            if (token.length() <= 1 || stopWords.contains(token)) {
                continue;
            }

            Set<String> docs = index.get(token);
            if (docs == null) {
                return Collections.emptySet();
            }

            if (result == null) {
                result = new HashSet<>(docs);
            } else {
                // Intersect postings so results must contain all keywords.
                result.retainAll(docs);
                if (result.isEmpty()) {
                    return Collections.emptySet();
                }
            }
        }

        if (result == null) {
            return Collections.emptySet();
        }
        return result;
    }
}