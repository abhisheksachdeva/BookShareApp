package com.sdsmdg.bookshareapp.BSA.api.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "work", strict = false)
public class Book {

    @Element(name = "best_book")
    BookDetails bookDetails;

    @Element(name = "id")
    public Integer search_id;

    @Element(name = "books_count")
    public String page_count;

    @Element(name = "average_rating")
    public float rating;

    @Element(name = "ratings_count")
    public long ratingCount;

    public BookDetails getBookDetails() {
        return bookDetails;
    }

    public Integer getId() {
        return search_id;
    }

    public String getPage_count() {
        return page_count;
    }

    public float getRating() {
        return rating;
    }

    public long getRatingCount() {
        return ratingCount;
    }
}




