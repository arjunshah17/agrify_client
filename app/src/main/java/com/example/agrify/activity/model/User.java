package com.example.agrify.activity.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class User {
    String name, email, phone;

    public User() {
    }


    public User(String name, String userProfilePhoto, String email, String phone) {

        this.name = name;

        this.email = email;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
  public Map<String,String> toUserMap()
   {
       Map<String,String> user=new HashMap<>();
       user.put("name",name);

       user.put("phone",phone);
       return user;

   }
}
