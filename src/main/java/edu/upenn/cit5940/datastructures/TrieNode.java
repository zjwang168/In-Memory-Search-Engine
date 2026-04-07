package edu.upenn.cit5940.datastructures;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isWord;
}