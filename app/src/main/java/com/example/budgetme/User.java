package com.example.budgetme;

public class User {

    public String Name, email;

    public User() {

    }

    public User(String Name, String email){
        this.Name = Name;
        this.email= email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
