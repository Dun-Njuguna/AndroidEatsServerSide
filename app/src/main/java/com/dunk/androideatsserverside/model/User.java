package com.dunk.androideatsserverside.model;

public class User {
    private String Name;
    private String Password;
    private String Phone;
    private String IsStaff;

    public User() {}

    public User(String name, String password, String isStaff) {
        Name = name;
        Password = password;
    }


    public String getName() {
        return Name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }
}