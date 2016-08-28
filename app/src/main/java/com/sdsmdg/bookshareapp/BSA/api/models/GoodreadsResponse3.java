package com.sdsmdg.bookshareapp.BSA.api.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="GoodreadsResponse",strict = false)
public class GoodreadsResponse3 {


    @Element(name="book")
    public BookDescription bDesc;

    public BookDescription getbDesc() {
        return bDesc;
    }

    public GoodreadsResponse3(){

    }


}

