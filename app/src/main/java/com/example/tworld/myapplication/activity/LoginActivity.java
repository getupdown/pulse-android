package com.example.tworld.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tworld.myapplication.R;
import com.example.tworld.myapplication.bean.UserBean;
import com.example.tworld.myapplication.database.DBManager;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private EditText et_username;
    private EditText et_password;
    private Button bt_login;
    private FloatingActionButton fab;

    private DBManager dbManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);//登录界面
        init();
    }

    public void init() {
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        bt_login = findViewById(R.id.bt_login);
        fab = findViewById(R.id.fab);

        dbManager = DBManager.getInstance(getApplicationContext());//初始化数据库

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);//跳转到注册界面
                LoginActivity.this.startActivity(intent);
            }
        });

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = et_username.getText().toString();//取得输入的用户名
                String password = et_password.getText().toString();//取得输入的密码
                ArrayList<UserBean> user_list = (ArrayList<UserBean>) dbManager.getUsers();
                boolean can_login = false;
                for (UserBean user : user_list) {
                    if (username.equals(user.getUsername()) && password.equals(user.getPassword())) {
                        can_login = true;
                        break;
                    }
                }

                if (can_login) {
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this, MainActivity.class);//跳转到注册界面
                    LoginActivity.this.startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
