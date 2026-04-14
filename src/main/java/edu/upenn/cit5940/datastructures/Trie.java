package edu.upenn.cit5940.datastructures;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Trie data structure used for prefix-based autocomplete.
 *
 * This class supports:
 * - inserting normalized words
 * - retrieving autocomplete suggestions by prefix
 * - limiting the number of returned results
 *
 * It is used to satisfy the HW8 integration requirement for efficient
 * autocomplete functionality.
 */
public class Trie {
    /** Root node of the trie */
    private final TrieNode root = new TrieNode();

    /**
     * Inserts a word into the trie.
     *
     * Words are normalized to lowercase before insertion so that
     * autocomplete is case-insensitive.
     *
     * @param word word to insert
     */
    public void insert(String word) {
        if (word == null || word.isBlank()) {
            return;
        }

        TrieNode current = root;
        for (char c : word.toLowerCase().toCharArray()) {
            current = current.children.computeIfAbsent(c, k -> new TrieNode());
        }
        current.isWord = true;
    }

    /**
     * Returns autocomplete suggestions for a given prefix.
     *
     * The search is case-insensitive and returns at most {@code limit} results.
     *
     * @param prefix prefix to search for
     * @param limit maximum number of suggestions to return
     * @return list of autocomplete suggestions
     */
    public List<String> autocomplete(String prefix, int limit) {
        List<String> results = new ArrayList<>();
        if (prefix == null) {
            return results;
        }

        TrieNode current = root;
        String lower = prefix.toLowerCase();

        // Traverse the trie to the node matching the prefix.
        for (char c : lower.toCharArray()) {
            current = current.children.get(c);
            if (current == null) {
                return results;
            }
        }

        collect(current, new StringBuilder(lower), results, limit);
        return results;
    }

    /**
     * Recursively collects autocomplete suggestions from the current trie node.
     *
     * Depth-first traversal is used to gather matching words until the
     * requested result limit is reached.
     *
     * @param node current trie node
     * @param path current prefix path
     * @param results collected autocomplete suggestions
     * @param limit maximum number of suggestions to collect
     */
    private void collect(TrieNode node, StringBuilder path, List<String> results, int limit) {
        if (results.size() >= limit) {
            return;
        }

        if (node.isWord) {
            results.add(path.toString());
        }

        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            path.append(entry.getKey());
            collect(entry.getValue(), path, results, limit);
            path.deleteCharAt(path.length() - 1);

            if (results.size() >= limit) {
                return;
            }
        }
    }
}