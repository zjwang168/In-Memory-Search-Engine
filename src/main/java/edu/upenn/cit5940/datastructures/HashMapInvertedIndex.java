package edu.upenn.cit5940.datastructures;

import edu.upenn.cit5940.util.TextTokenizer;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HashMapInvertedIndex {
    private final Map<String, Set<String>> index = new HashMap<>();
    private final Set<String> stopWords;

    public HashMapInvertedIndex(Set<String> stopWords) {
        this.stopWords = stopWords == null ? Collections.emptySet() : stopWords;
    }

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
