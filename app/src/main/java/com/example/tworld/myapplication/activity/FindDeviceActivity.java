package com.example.tworld.myapplication.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.tworld.myapplication.Constants;
import com.example.tworld.myapplication.R;
import com.example.tworld.myapplication.adapter.DeviceListAdapter;
import com.example.tworld.myapplication.data.PulseVO;
import com.example.tworld.myapplication.facade.BluetoothDecoder;
import com.example.tworld.myapplication.service.BluetoothConnectionService;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class FindDeviceActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "FindDeviceActivity";

    BluetoothAdapter mBluetoothAdapter;//蓝牙适配器
    // Button btnEnableDisable_Discoverable;

    BluetoothConnectionService mBluetoothConnection;//蓝牙连接服务

    Button btnStartConnection;//连接按钮
    // Button btnSend;

    //EditText etSend;

//    //TextView incomingMessages;
//    StringBuilder messages;//数据流

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    BluetoothDevice mBTDevice;//蓝牙设备

    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();//蓝牙设备数组

    public DeviceListAdapter mDeviceListAdapter;//蓝牙列表适配器

    ListView lvNewDevices;//蓝牙列表


    // Create a BroadcastReceiver for ACTION_FOUND 为ACTION_FOUND创建一个广播
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device 当发现一个设备
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF://蓝牙已关闭
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF://蓝牙正在关闭
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON://蓝牙已开启
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON://蓝牙正在开启
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    /**
     * Broadcast Receiver for changes made to bluetooth states such as:用于蓝牙状态的更改的广播接收器，例如
     * 1) Discoverability mode on/off or expire.可发现模式开/关或过期
     */
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE://发现蓝牙能力被激活
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE://发现能力被禁用。能够接收连接
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE://发现能力被禁用。不能接收连接
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING://正在连接
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED://已连接
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }

            }
        }
    };


    /**
     * Broadcast Receiver for listing devices that are not yet paired 用于列出尚未配对的设备的广播接收机
     * -Executed by btnDiscover() method.
     */
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);//把发现的设备放到蓝牙列表里面
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);//放到蓝牙适配器里面
                lvNewDevices.setAdapter(mDeviceListAdapter);//列表显示适配器
            }
        }
    };

    /**
     * Broadcast Receiver that detects bond state changes (Pairing status changes)
     * 用于检测绑定状态变化的广播接收机（配对状态变化）
     */
    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already 已经绑定
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                    //inside BroadcastReceiver4
                    mBTDevice = mDevice;    //将设备和变量进行绑定
                }
                //case2: creating a bone 创建绑定
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond 解绑
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };


    private List<BroadcastReceiver> broadcastReceivers = Lists.newArrayList(
            mBroadcastReceiver1,
            mBroadcastReceiver2,
            mBroadcastReceiver3,
            mBroadcastReceiver4
    );

    //摧毁上面的四种广播
    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();

        // 取消所有receiver
        for (BroadcastReceiver broadcastReceiver : broadcastReceivers) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_device);//activity_find_device.xml界面

        lvNewDevices = (ListView) findViewById(R.id.lvNewDevices);//设备列表
        mBTDevices = new ArrayList<>();//设备数组
        btnStartConnection = (Button) findViewById(R.id.btnStartConnection);//连接按钮
        //   btnSend = (Button) findViewById(R.id.btnSend);
        //   etSend = (EditText) findViewById(R.id.editText);
//     //   incomingMessages=(TextView)findViewById(R.id.incomingMessage);
//        messages=new StringBuilder();//数据，可拼接的字符串
//       LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,new IntentFilter("incomingMessage"));


        //Broadcasts when bond state changes (ie:pairing) 当绑定状态改变时进行广播（配对）
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        lvNewDevices.setOnItemClickListener(FindDeviceActivity.this);//点击设备列表，实现一个监听

//
//        btnONOFF.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(TAG, "onClick: enabling/disabling bluetooth.");
//                enableDisableBT();
//            }
//        });


        btnStartConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startConnection();//开始连接，258

                Intent intent = new Intent(FindDeviceActivity.this, DrawPulseActivity.class);
                startActivity(intent);

            }
        });

//        btnSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                byte[] bytes = etSend.getText().toString().getBytes(Charset.defaultCharset());
//                mBluetoothConnection.write(bytes);
//                 etSend.setText("");
//            }
//        });

    }


//    BroadcastReceiver mReceiver=new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String text = intent.getStringExtra("theMessage");
//      //      messages.append(text+"\n");
//    //        incomingMessages.setText(messages);
//        }
//    };

    //create method for starting connection 创建方法开始连接
//***remember the conncction will fail and app will crash if you haven't paired first
    //记住，如果你没有配对，连接会失败，应用程序会崩溃
    public void startConnection() {
        startBTConnection(mBTDevice, MY_UUID_INSECURE);
    }//266

    /**
     * starting chat service method 启动聊天服务方法
     */
    public void startBTConnection(BluetoothDevice device, UUID uuid) {
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");
        //开始蓝牙连接
        mBluetoothConnection.startClient(device, uuid);
    }


    public void enableDisableBT() {
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "enableDisableBT: enabling BT.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void btnDiscover(View view) {

        Log.d(TAG, "onClick: enabling/disabling bluetooth.");
        enableDisableBT();

        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
        if (!mBluetoothAdapter.isDiscovering()) {

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
    }

    /**
     * This method is required for all devices running API23+
     * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
     * in the manifest is not enough.
     * <p>
     * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        } else {
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    //点击listview里的项目
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //first cancel discovery because its very memory intensive.首先取消发现，因为它非常占内存
        mBluetoothAdapter.cancelDiscovery();

        Log.d(TAG, "onItemClick: You Clicked on a device.");
        String deviceName = mBTDevices.get(i).getName();
        String deviceAddress = mBTDevices.get(i).getAddress();

        Log.d(TAG, "onItemClick: deviceName = " + deviceName);
        Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

        //create the bond.创建绑定
        //NOTE: Requires API 17+? I think this is JellyBean
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Log.d(TAG, "Trying to pair with " + deviceName);
            mBTDevices.get(i).createBond();

            mBTDevice = mBTDevices.get(i);
            mBluetoothConnection = new BluetoothConnectionService(FindDeviceActivity.this);//service建立连接
        }
    }
}