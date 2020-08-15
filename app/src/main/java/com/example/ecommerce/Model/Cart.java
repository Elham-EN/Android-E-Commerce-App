package com.example.ecommerce.Model;
/*
For a properly constructed model class like the Chat class above, Firebase can perform automatic
serialization in DatabaseReference#setValue() and automatic deserialization in DataSnapshot#getValue().
(Serialization is the process of converting an object into a stream of bytes to store
 the object or transmit it to memory, a database)
* */
public class Cart {
    private String productId, productName, price, quantity, discount;

    // Needed for Firebase - which is required for Firebase's automatic data mapping.
    public Cart() {

    }

    public Cart(String productId, String productName, String price, String quantity, String discount) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.discount = discount;
    }
    //The getters and setters follow the JavaBean naming pattern which allows Firebase
    //to map the data to field names (ex: getName() provides the name field).
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
