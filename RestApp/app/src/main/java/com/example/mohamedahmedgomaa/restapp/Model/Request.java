package com.example.mohamedahmedgomaa.restapp.Model;

import java.util.List;

public class Request {

    private  String Phone,Name,Address, Total,status;

    private String date;
    private String time;
    public String getStatus() {
        return status;
    }


    private List<Order> foods;



    public Request(String phone, String name, String address, String total,String date,String time, List<Order> foods) {
        this.Phone = phone;
        this.Name = name;
        this.Address = address;
        this.Total = total;
        this.foods = foods;
        this.date=date;
        this.time=time;
        this.status="0";
    }


    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Request() {
    }
    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }
}
