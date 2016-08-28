package com.sdsmdg.bookshareapp.BSA.api.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="book",strict = false)
public class BookDetailsToRead {

	@Element(name = "title")
	public String title;

	@Element(name="id")
	public Integer search_id;

	@Element(name = "image_url")
	public String image_url;

	@Element(name = "small_image_url")
	public String small_image_url;

	@Element(name = "author")
	Author author;

	@Element(name="average_rating")
	public float rating;

	@Element(name = "ratings_count")
	public String ratingCount;

    public float getRating() {
        return rating;
    }

    public String getRatingCount() {
        return ratingCount;
    }

    public Integer getId(){
		return search_id;
	}

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
