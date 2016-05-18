	package com.example.abhishek.bookshareapp.api.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

	@Root(name="book",strict = false)
    public class Book2 {



        @Element(name="title")
        String title;

        @Element(name="description")
        String desc;

        @Element(name = "image_url")
        public String image_url;

        @Element(name="average_rating")
        public float rating;

        public String getTitle(){
            return title;
        }

        public float getRating(){
            return rating;
        }


        public String getDesc(){
            return desc;
        }
        public String getImage_url(){
            return image_url;
        }

    }




