package com.jun.rxdemo.bean;

import java.util.ArrayList;
import java.util.List;

public class Person extends Object{

    private String name;
    private int age;
    private List<String> books = new ArrayList<>();

    public Person(){

    }

    public Person(String name,int age,String... args){
        this.name = name;
        this.age = age;
        for(String s:args){
            this.books.add(s);
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<String> getBooks() {
        return books;
    }

    public void setBooks(List<String> books) {
        this.books = books;
    }

}
