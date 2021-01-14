package com.codestown.examinoo.model;

import java.util.ArrayList;

public class Category {
    private String id,name,image,maxLevel,noOfCate,message,date,ttlQues;
    public ArrayList<SubCategory> subCategoryList;

    public Category() {
    }

    public Category(String id, String name , String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getImage() {
        return image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNoOfCate() {
        return noOfCate;
    }

    public void setNoOfCate(String noOfCate) {
        this.noOfCate = noOfCate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(String maxLevel) {
        this.maxLevel = maxLevel;
    }

    public String getTtlQues() {
        return ttlQues;
    }

    public void setTtlQues(String ttlQues) {
        this.ttlQues = ttlQues;
    }

    public ArrayList<SubCategory> getSubCategoryList() {
        return subCategoryList;
    }
    public void setSubCategoryList(ArrayList<SubCategory> subCategoryList) {
        this.subCategoryList = subCategoryList;
    }
}
