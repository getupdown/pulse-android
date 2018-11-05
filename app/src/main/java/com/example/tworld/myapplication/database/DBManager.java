package com.example.tworld.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.tworld.myapplication.bean.UserBean;

import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private DBHelper helper;//数据库帮助者
    private SQLiteDatabase db;//数据库
    private Cursor cursor;//游标
    private static DBManager dbManager;

    //创建数据库
    public DBManager(Context context){
        //创建了一个名为mydb.db的数据库，有user表
        helper=new DBHelper(context);
        //连接数据库，变成可写入状态
        db=helper.getWritableDatabase();
    }

    //获取dbManager实例
    public synchronized static DBManager getInstance(Context context){
        if(dbManager==null){
            dbManager=new DBManager(context);
        }
        return dbManager;
    }

    //关闭数据库
    public void closeDB(){
        db.close();
    }

    /*
    //添加用户
    public  boolean addUser(UserBean user){
        ContentValues values=new ContentValues();
        values.put("username",user.getUsername());
        values.put("password",user.getPassword());
        values.put("age",user.getAge());
        values.put("sex",user.getSex());
        db.insert("user",null,values);
        return true;
    }
    */

    //添加用户
    public boolean saveUser(UserBean user){
        if(user!=null){
            Cursor cursor = db.rawQuery("select * from user where username=?", new String[]{user.getUsername().toString()});
            if(cursor.getCount()>0){
                return false;
            }else{
                try {
                    ContentValues values=new ContentValues();
                    values.put("username",user.getUsername());
                    values.put("password",user.getPassword());
                    values.put("age",user.getAge());
                    values.put("sex",user.getSex());
                    db.insert("user",null,values);
                }catch (Exception e){
                    Log.d("插入错误",e.getMessage().toString());
                }
                return true;
            }
        }
        return false;
    }

    //查询用户
    public List<UserBean> getUsers(){
        ArrayList<UserBean> user_list=new ArrayList<UserBean>();
        Cursor cursor=null;
        String sql="select * from user";
        cursor=db.rawQuery(sql,null);
        while(cursor.moveToNext()){
            UserBean user=new UserBean();
            user.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            user_list.add(user);
        }
        cursor.close();//关闭游标
        return user_list;
    }

}
