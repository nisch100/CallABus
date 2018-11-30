package com.example.nisch100.call_a_bus;

public class Registerfirebase {
    public String name;
    public String email;
    public String phone;
    public String password;
    public int numBuses;

    public Registerfirebase(){

    }
    public String relName;
    public String relRel;
    public String relNum;

    public Registerfirebase(String name,String email,String phone,String password, String relName, String relRel, String relNum) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.relName = relName;
        this.relNum = relNum;
        this.relRel = relRel;
        this.numBuses = 0;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getNumBuses() {
        return numBuses;
    }

    public void setNumBuses(int numBuses) {
        this.numBuses = numBuses;
    }
}
