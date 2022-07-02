package com.example.budgetme;

public class User {

    public String name, email;

    public User() {

    }

    public User(String inputName, String inputEmail){
        this.name = inputName;
        this.email= inputEmail;
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
}
