package com.example.abhishek.bookshareapp.api.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="GoodreadsResponse",strict = false)
public class GoodreadsResponse2 {


    @Element(name="book")
    public Book2 bk2;
    public  Book2 getBook(){return bk2;}


    public GoodreadsResponse2(){

    }


}
