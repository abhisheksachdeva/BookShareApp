package com.example.abhishek.bookshareapp.api.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="best_book",strict = false)
	public class BookDetails{

	@Element(name = "title")
	public String title;

	@Element(name = "image_url")
	public String image_url;

	@Element(name = "small_image_url")
	public String small_image_url;

	@Element(name = "author")
	Author author;


	public String getTitle(){
		return title;
	}

	public String getImage_url(){
		return  image_url;
	}

	public String getSmall_image_url(){
		return  small_image_url;
	}

	public Author getAuthor(){
		return author;
	}


		}
