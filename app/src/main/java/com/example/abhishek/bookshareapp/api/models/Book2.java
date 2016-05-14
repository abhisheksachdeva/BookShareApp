	package com.example.abhishek.bookshareapp.api.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

	@Root(name="book",strict = false)
    public class Book2 {

        @Element(name="work")
        Book bk;

        @Element(name="title")
        String title;

        @Element(name="description")
        String desc;


        public Book getBk(){
            return bk;
        }
        public String getTitle(){
            return title;
        }

        public String getDesc(){
            return desc;
        }

    }




