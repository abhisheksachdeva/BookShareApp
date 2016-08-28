package com.sdsmdg.bookshareapp.BSA.api.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="review",strict = false)
public class ToRead {

	@Element(name="book")
	BookDetailsToRead bookDetailsToRead;

	public BookDetailsToRead getBookDetailsToRead() {
		return bookDetailsToRead;
	}
}




