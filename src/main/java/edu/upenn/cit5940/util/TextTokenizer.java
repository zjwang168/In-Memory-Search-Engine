package edu.upenn.cit5940.util;

import java.util.ArrayList;
import java.util.List;

public class TextTokenizer {
    public static List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        if (text == null || text.isBlank()) return tokens;

        String[] parts = text.toLowerCase().split("[^a-z0-9]+");
        for (String p : parts) {
            if (!p.isBlank()) {
                tokens.add(p);
            }
        }
        return tokens;
    }
}