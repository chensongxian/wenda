package com.csx.model;

/**
 * Created by csx on 2016/6/26.
 */
public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String salt;
    private String headUrl;

    private boolean sex=true;
    private String introduction="";
    private String livePlace="";


    public User() {

    }
    public User(String name) {
        this.name = name;
        this.email="";
        this.password = "";
        this.salt = "";
        this.headUrl = "";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getLivePlace() {
        return livePlace;
    }

    public void setLivePlace(String livePlace) {
        this.livePlace = livePlace;
    }
}
