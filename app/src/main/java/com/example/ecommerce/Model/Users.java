package com.example.ecommerce.Model;
//Get Name, Password and Phone from the firebase database
public class Users {
    //Declared private instance variables
    private String name, phone, password, image,  address;

    //Constructor is used to initialize objects or set initial values for object attributes
    public Users() { //Default constructor with no parameter

    }//end of Users() no parameter

    //Constructor with parameters
    public Users(String name, String phone, String password, String image, String address) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.image = image;
        this.address = address;
    }//end of Users() parameter

    /*get and set methods to access and update the value of a private variable*/
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


}//end of class Users
