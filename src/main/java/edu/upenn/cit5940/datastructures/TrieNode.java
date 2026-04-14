package edu.upenn.cit5940.datastructures;

import java.util.HashMap;
import java.util.Map;

/**
 * Node used in the Trie data structure.
 *
 * Each node represents a single character and contains:
 * - a map of child nodes (next characters)
 * - a flag indicating whether this node completes a valid word
 */
public class TrieNode {

    /** Mapping from character to child TrieNode */
    Map<Character, TrieNode> children = new HashMap<>();

    /** Indicates whether this node marks the end of a valid word */
    boolean isWord;
}