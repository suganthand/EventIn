package com.example.ghp.myapp_eventin;

import java.io.Serializable;

/**
 * Created by ghp on 26-Jul-16.
 */
public class Category implements Serializable {

    public String Name="";

//    public Category(String name) {
//        this.Name = name;
//    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
