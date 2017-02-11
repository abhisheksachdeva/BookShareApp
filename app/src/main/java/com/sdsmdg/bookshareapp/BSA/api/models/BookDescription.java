package com.sdsmdg.bookshareapp.BSA.api.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "book", strict = false)
public class BookDescription {

    @Element(name = "description")
    public String BDescription;

    public String getBDescription() {
        return BDescription;
    }
}




