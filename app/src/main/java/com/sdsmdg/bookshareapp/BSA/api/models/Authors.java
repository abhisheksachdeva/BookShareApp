package com.sdsmdg.bookshareapp.BSA.api.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "authors", strict = false)
public class Authors {
    @Element(name = "author")
    public Author author;

    public Author getAuthors() {
        return author;
    }
}
