package com.example.batchprocessing.dto;

public class Book {
    private String isin;
    private Integer quantity;
    private Double price;
    private String author;

    public Book( String author,String isin) {
        this.isin = isin;
        this.author = author;
    }

    public Book() {
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Book{" +
                "isin='" + isin + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", author='" + author + '\'' +
                '}';
    }
}
