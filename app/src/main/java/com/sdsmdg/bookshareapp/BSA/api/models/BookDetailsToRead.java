package com.sdsmdg.bookshareapp.BSA.api.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "book", strict = false)
public class BookDetailsToRead {

    @Element(name = "title")
    public String title;

    @Element(name = "image_url")
    public String image_url;

    @Element(name = "small_image_url")
    public String small_image_url;

    @Element(name = "authors")
    public Authors authors;

    @Element(name = "average_rating")
    public float rating;

    @Element(name = "ratings_count")
    public String ratingCount;

    public float getRating() {
        return rating;
    }

    public String getRatingCount() {
        return ratingCount;
    }


    public String getTitle() {
        return title;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getSmall_image_url() {
        return small_image_url;
    }

    public Authors getAuthor() {
        return authors;
    }


}
