package com.example.abhishek.bookshareapp.api.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by abhishek on 30/1/16.
 */
@Root(name="work",strict = false)
public class Book {

	@Element(name="best_book")
	BookDetails bookDetails;

	@Element(name="books_count")
	public String page_count;

	@Element(name="average_rating")	
	public float rating;

	public BookDetails getBookDetails(){
		return bookDetails;
	}

	public String  getPage_count(){
		return page_count;
	}
	public float getRating(){
		return rating;
	}

}




