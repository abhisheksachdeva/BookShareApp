package com.sdsmdg.bookshareapp.BSA.api.models;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "GoodreadsResponse", strict = false)
public class GoodreadsResponse3 {


    @ElementList(name = "books")
    public List<BookDetailsToRead> bookDetailsToReads;

//    public ToReadModel getToReadModelReviews() {
//        return toReadModelReviews;
//    }


    public List<BookDetailsToRead> getBookDetailsToReads() {
        return bookDetailsToReads;
    }

    public GoodreadsResponse3() {

    }


}

