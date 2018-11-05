package com.example.tworld.myapplication.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.tworld.myapplication.R;
import com.example.tworld.myapplication.bean.UserBean;
import com.example.tworld.myapplication.database.DBManager;

public class RegisterActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private EditText et_username;
    private EditText et_password;
    private EditText et_repeatpassword;
    private EditText et_age;
    private RadioButton rb_man;
    private RadioButton rb_woman;
    private Button bt_submit;

    private String sex;//性别
    private DBManager dbManager;//数据库

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);//注册界面
        init();

    }

    //初始化
    public void init() {
        //绑定各控件
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        et_repeatpassword = findViewById(R.id.et_repeatpassword);
        et_age = findViewById(R.id.et_age);
        rb_man = findViewById(R.id.rb_man);
        rb_woman = findViewById(R.id.rb_woman);
        bt_submit = findViewById(R.id.bt_submit);

        rb_man.setOnCheckedChangeListener(this);
        rb_woman.setOnCheckedChangeListener(this);


        dbManager = DBManager.getInstance(getApplicationContext());//初始化数据库

        //提交按钮有一个点击监听事件
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = et_username.getText().toString();
                if (!TextUtils.isEmpty(username)) {
                    String password = et_password.getText().toString();
                    if (!TextUtils.isEmpty(password)) {
                        String repeatpws = et_repeatpassword.getText().toString();
                        if (password.equals(repeatpws)) {
                            String age = et_age.getText().toString();
                            if (!TextUtils.isEmpty(age)) {
                                if (!TextUtils.isEmpty(sex)) {
                                    //将用户名、密码、年龄、性别放到UserBean里面
                                    UserBean user = new UserBean();
                                    user.setUsername(username);
                                    user.setPassword(password);
                                    user.setAge(Integer.valueOf(age));//将年龄转换成int型的
                                    user.setSex(sex);
                                    if (dbManager.saveUser(user)) {
                                        //Log.i("info","用户插入成功");
                                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent();
                                        intent.setClass(RegisterActivity.this, LoginActivity.class);//跳转到登录界面
                                        RegisterActivity.this.startActivity(intent);
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "该用户名已被注册！", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(RegisterActivity.this, "请选择性别", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(RegisterActivity.this, "请填写年龄", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "密码不一致", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "密码不能为空！", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "用户名不能为空！", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.rb_man:
                if (isChecked) {
                    rb_woman.setChecked(false);
                    sex = "男性";
                }
                break;
            case R.id.rb_woman:
                if (isChecked) {
                    rb_man.setChecked(false);
                    sex = "女性";
                }
        }
    }
}
