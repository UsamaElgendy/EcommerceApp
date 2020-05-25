package com.elgindy.ecommerceapp.model;

public class Products {
    private String productName, description, price, image, category, productId, date, time;

    public Products() {

    }

    public Products(String productName, String description, String price, String image, String category, String productId, String date, String time) {
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.image = image;
        this.category = category;
        this.productId = productId;
        this.date = date;
        this.time = time;
    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}