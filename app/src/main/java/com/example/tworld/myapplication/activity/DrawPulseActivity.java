package com.example.tworld.myapplication.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.tworld.myapplication.Constants;
import com.example.tworld.myapplication.R;
import com.example.tworld.myapplication.data.PulseVO;
import com.example.tworld.myapplication.view.CardiographView;
import com.example.tworld.myapplication.view.PathView;
import com.example.tworld.myapplication.view.WH_ECGView;

import java.util.ArrayList;

public class DrawPulseActivity extends AppCompatActivity {

    private CardiographView cardiographView;
    private PathView pathView;
    private TextView tv_count;
    private TextView tv_type;
    private Button bt_draw;
    private Button bt_savedata;


    TextView incomingMessages;
    StringBuilder messages;//数据流


    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // 接受数据

            ArrayList<PulseVO> pulseVOS = intent.getParcelableArrayListExtra(Constants.BLUETOOTH_INCOMING_MSG_KEY);

            for (PulseVO pulseVO : pulseVOS) {
                pathView.receiveNewLocation(pulseVO.getSignal());
            }



//            String text = intent.getStringExtra("theMessage");
//            messages.append(text + "\n");
//            incomingMessages.setText(messages);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_pulse);

        incomingMessages = (TextView) findViewById(R.id.incomingmessages);
        messages = new StringBuilder();//数据，可拼接的字符串
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(Constants.BLUETOOTH_INTENT));

        //  Log.d(TAG, "InputStream: " + messages);
        init();
    }

    public void init() {
        tv_count = findViewById(R.id.tv_count);//脉搏数
        tv_type = findViewById(R.id.tv_type);//脉搏类型
        bt_draw = findViewById(R.id.bt_draw);//采集按钮，点击开始接收数据并画图
        bt_savedata = findViewById(R.id.bt_savedata);//保存按钮，将接收到的数据保存到数据库
        pathView = findViewById(R.id.pathView);
    }
}
