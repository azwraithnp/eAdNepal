package com.azwraithnp.eadnepal.main.Models;

public class UserModel {

    String f_name, l_name, email, phone, location, college, level, filed_of_study, company, post, interest, id, reg_date, balance, age, sex;

    public UserModel(String f_name, String l_name, String email, String phone, String location, String college, String level, String filed_of_study, String company, String post, String interest, String id, String reg_date, String balance, String age, String sex) {
        this.f_name = f_name;
        this.l_name = l_name;
        this.email = email;
        this.phone = phone;
        this.location = location;
        this.college = college;
        this.level = level;
        this.filed_of_study = filed_of_study;
        this.company = company;
        this.post = post;
        this.interest = interest;
        this.id = id;
        this.reg_date = reg_date;
        this.balance = balance;
        this.age = age;
        this.sex = sex;
    }

    public String getF_name() {
        return f_name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReg_date() {
        return reg_date;
    }

    public void setReg_date(String reg_date) {
        this.reg_date = reg_date;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    public String getL_name() {
        return l_name;
    }

    public void setL_name(String l_name) {
        this.l_name = l_name;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getFiled_of_study() {
        return filed_of_study;
    }

    public void setFiled_of_study(String filed_of_study) {
        this.filed_of_study = filed_of_study;
    }

}


