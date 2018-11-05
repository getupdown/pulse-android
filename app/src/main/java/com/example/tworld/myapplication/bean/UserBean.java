package com.example.tworld.myapplication.bean;

public class UserBean {
    private String username;//用户名
    private String password;//密码
    private int age;//年龄
    private String sex;//性别

    public UserBean(){
        super();
    }

    //带参的构造方法
    public UserBean(String username,String password,int age,String sex){
        this.username=username;
        this.password=password;
        this.age=age;
        this.sex=sex;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

}
