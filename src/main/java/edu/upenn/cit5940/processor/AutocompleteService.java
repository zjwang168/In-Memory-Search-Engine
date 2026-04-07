package edu.upenn.cit5940.processor;

import edu.upenn.cit5940.datastructures.Trie;
import edu.upenn.cit5940.model.Article;
import edu.upenn.cit5940.util.TextTokenizer;

import java.util.List;
import java.util.Set;

public class AutocompleteService {
    private final Trie trie = new Trie();

    public AutocompleteService(List<Article> articles, Set<String> stopWords) {
        for (Article article : articles) {
            for (String token : TextTokenizer.tokenize(article.getTitle())) {
                if (token.length() > 1 && !stopWords.contains(token)) {
                    trie.insert(token);
                }
            }
        }
    }

    public List<String> autocomplete(String prefix) {
        return trie.autocomplete(prefix, 10);
    }
}