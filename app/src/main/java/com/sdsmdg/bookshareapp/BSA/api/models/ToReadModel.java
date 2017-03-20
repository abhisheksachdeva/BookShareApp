package com.sdsmdg.bookshareapp.BSA.api.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "books", strict = false)
public class ToReadModel {

    @Element(name = "book")
    List<BookDetailsToRead> bookDetailsToReadsList;

    public List<BookDetailsToRead> getBookDetailList() {
        return bookDetailsToReadsList;
    }
}




