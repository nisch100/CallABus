package com.example.nisch100.call_a_bus;

public class Registerfirebase {
    public String name;
    public String email;
    public String phone;
    public String password;
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
    }
}
