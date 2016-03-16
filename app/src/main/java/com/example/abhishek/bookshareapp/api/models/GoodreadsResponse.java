package com.example.abhishek.bookshareapp.api.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by abhishek on 16/3/16.
 */
@Root(name="GoodreadsResponse",strict = false)
public class GoodreadsResponse {


    @Element(name="search")
    public Search search;

    public Search getSearch(){
    	return search;
    }


    public GoodreadsResponse(){

    }


}
