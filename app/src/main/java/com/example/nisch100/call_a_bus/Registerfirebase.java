package com.example.nisch100.call_a_bus;

public class Registerfirebase {
    public String name;
    public String email;
    public String phone;
    public String password;
    public int numBuses;

    public Registerfirebase(String name,String email,String phone,String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        numBuses = 0;
    }
}
