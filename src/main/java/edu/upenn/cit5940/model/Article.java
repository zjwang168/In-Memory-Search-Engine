package edu.upenn.cit5940.model;

import java.time.LocalDate;

public class Article {

    private String id;
    private String title;
    private LocalDate date;
    private String content;

    public Article(String id, String title, LocalDate date, String content) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }
}