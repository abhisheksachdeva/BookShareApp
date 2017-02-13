package com.sdsmdg.bookshareapp.BSA.api.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "GoodreadsResponse", strict = false)
public class GoodreadsResponse {


    @Element(name = "search")
    public Search search;

    public Search getSearch() {
        return search;
    }

    public GoodreadsResponse() {

    }


}
