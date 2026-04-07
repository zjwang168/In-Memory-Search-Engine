package edu.upenn.cit5940.datastructures;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Trie {
    private final TrieNode root = new TrieNode();

    public void insert(String word) {
        if (word == null || word.isBlank()) return;
        TrieNode current = root;
        for (char c : word.toLowerCase().toCharArray()) {
            current = current.children.computeIfAbsent(c, k -> new TrieNode());
        }
        current.isWord = true;
    }

    public List<String> autocomplete(String prefix, int limit) {
        List<String> results = new ArrayList<>();
        if (prefix == null) return results;

        TrieNode current = root;
        String lower = prefix.toLowerCase();

        for (char c : lower.toCharArray()) {
            current = current.children.get(c);
            if (current == null) return results;
        }

        collect(current, new StringBuilder(lower), results, limit);
        return results;
    }

    private void collect(TrieNode node, StringBuilder path, List<String> results, int limit) {
        if (results.size() >= limit) return;

        if (node.isWord) {
            results.add(path.toString());
        }

        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            path.append(entry.getKey());
            collect(entry.getValue(), path, results, limit);
            path.deleteCharAt(path.length() - 1);
            if (results.size() >= limit) return;
        }
    }
}