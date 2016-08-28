package com.sdsmdg.bookshareapp.BSA.GRLogin;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="GoodreadsResponse",strict = false)
public class GoodreadsOAuthResponse {


    @Element(name="user")
    public String id;
    public String getId(){
        return id;
    }

    public GoodreadsOAuthResponse(){

    }


}
