package com.example.ecommerce.Prevalent;

import com.example.ecommerce.Model.Users;

//widespread in a particular area or at a particular time.Basically contain all common data of users
//Later use this for working on forget password and remember me features.
public class Prevalent {
    //Attributes and methods belongs to the class, rather than an object
    public static Users currentOnlineUser;
    //That will store user phone, basically unique key for every user, under that key we have all users info.
    public static final String UserPhoneKey = "UserPhone";
    public static final String UserPasswordKey = "UserPassword";
}
