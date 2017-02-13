package com.sdsmdg.bookshareapp.BSA.api.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "author", strict = false)
public class Author {
    @Element(name = "name")
    public String author_name;

    public String getAuthor_name() {
        return author_name;
    }
}
