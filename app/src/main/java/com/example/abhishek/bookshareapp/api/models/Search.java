package com.example.abhishek.bookshareapp.api.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by abhishek on 16/3/16.
 */
@Root(name = "search",strict = false)
public class Search {
    @Element(name="source")
    String source;

    @ElementList(name="results")
    List<Book> books;


    public String getSource(){
    	return source;
    }

    public List<Book> getBooks(){
    	return books;
    }



}
                                                                                                                                                                                                                                                                                                                                                                                                                                                