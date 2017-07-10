package com.javacodegeeks.Bluetooth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import com.Utils.Utility;
import com.database.CommonUtilities;
import com.google.gson.JsonObject;
import com.javacodegeeks.R;

import org.json.JSONArray;
import org.json.JSONObject;

import androidRecyclerView.MessageAdapter;

import static com.Utils.Utility.userPreferenceData;


public class BluetoothChat extends ActionBarActivity {

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private EditText selected_id;
    private Button mSendButton;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;

    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;

    // Member object for the chat services
    private BluetoothChatService mChatService = null;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MessageAdapter mAdapter;
    EditText count;
    public int counter = 0;
    String user_id;
    private List<androidRecyclerView.Message> messageList = new ArrayList<androidRecyclerView.Message>();
    ArrayList<HashMap<String,String >> mylist=new ArrayList<HashMap<String,String>>();
    Context context;
    ProgressDialog myDialog;
    Button deviceId_send;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.signinupshape));
        context = getApplicationContext();
        //user_id = userPreferenceData.get("user_id");
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
       // mLayoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MessageAdapter(getBaseContext(), messageList);
        mRecyclerView.setAdapter(mAdapter);
       // mRecyclerView.smoothScrollToPosition(0);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.getLayoutManager().scrollToPosition(messageList.size()-1);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        selected_id=(EditText)findViewById(R.id.selected_id);
        count=(EditText)findViewById(R.id.count);
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
       final  RadioButton pg=(RadioButton)findViewById(R.id.pgrd);
       final RadioButton pm=(RadioButton)findViewById(R.id.pmrd);
        pg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(buttonView.isChecked()){
                    pg.setChecked(true);
                    pm.setChecked(false);
                    Utility.addChecked("PG");
                }
            }
        });
        pm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(buttonView.isChecked()){
                    pm.setChecked(true);
                    pg.setChecked(false);
                    Utility.addChecked("PM");
                }
            }
        });
        Button getDeviceId=(Button)findViewById(R.id.select_id);
        getDeviceId.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
              String str=  Utility.getSelect_Device().toString().trim();
                String str_count=count.getText().toString().trim();

                if(str.equals("NOT")){
                   Toast.makeText(BluetoothChat.this,"Please Select Device Type",Toast.LENGTH_SHORT).show();
                } else  if(str_count.isEmpty()){
                    Toast.makeText(BluetoothChat.this,"Please Select Device Range",Toast.LENGTH_SHORT).show();
                }else{
                    getDeviceIds();
                }

            }
        });
        deviceId_send=(Button)findViewById(R.id.deviceId_send);
        deviceId_send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String message=selected_id.getText().toString().trim();
                for (int i = 0; i < mylist.size(); i++) {
                    HashMap<String, String> map = mylist.get(i);
                    final String DevId = map.get("DevId").toString().trim();
                    final String EncodedSerial = map.get("EncodedSerial").toString().trim();
                    final String HWVer = map.get("HWVer").toString().trim();
                    final String ManufacDate = map.get("ManufacDate").toString().trim();
                    final String BatchNo = map.get("BatchNo").toString().trim();
                    int b=Integer.parseInt(DevId);
                    int h=Integer.parseInt(HWVer);
                    int date=Integer.parseInt(ManufacDate);
                    int batchno=Integer.parseInt(BatchNo);
                    long converts=Long.parseLong(EncodedSerial,16);
                    byte[] send = new byte[] {
                            (byte) b,
                            (byte)converts,
                            (byte)(converts >>> 8),
                            (byte)(converts >>> 16),
                            (byte)h,
                            (byte)0,
                            (byte)date,
                            (byte)(date >>> 8),
                            (byte)(date >>> 16),
                            (byte)batchno,
                            (byte)(batchno >>> 8)
                    };
                    mChatService.write(send);
                }

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            if (mChatService == null) setupChat();

        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if (mChatService != null) {
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                mChatService.start();
            }
        }
    }

    private void setupChat() {
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);
        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    public synchronized void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
    }

    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }


    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
           mChatService.write(send);
            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
           // mOutEditText.setText(mOutStringBuffer);
        }
    }

    // The action listener for the EditText widget, to listen for the return key
    private TextView.OnEditorActionListener mWriteListener =
            new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                    // If the action is a key-up event on the return key, send the message
                    if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                        String message = view.getText().toString();
                        sendMessage(message);
                    }
                    return true;
                }
            };

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mAdapter.notifyDataSetChanged();
                    messageList.add(new androidRecyclerView.Message(counter++, writeMessage, "Me"));
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                   String readMessage = new String(readBuf, 0, msg.arg1);
                   System.out.println("read message "+readMessage);
                    for (int i = 0; i < mylist.size(); i++) {
                        HashMap<String, String> map = mylist.get(i);
                        final String manufacSerialNo = map.get("manufacSerialNo").toString().trim();
                        if(readMessage.equals("0")){
                            //success on Id success: "MfgIdState::0"
                            final String successId = map.get("successId").toString().trim();
                            //sendDeviceDetails(successId,manufacSerialNo);
                        }else if(readMessage.equals("1")){
                            // failure on Id failure: 1.wrong dev type: "MfgIdState::1"
                            final String failedId = map.get("failedId").toString().trim();
                            //sendDeviceDetails(failedId,manufacSerialNo);
                        }else if(readMessage.equals("2")){
                            // memory failure  MfgIdState::2
                            final String failedId = map.get("failedId").toString().trim();
                            //sendDeviceDetails(failedId,manufacSerialNo);
                         }else {
                          //on device exit: "MfgIdState::stop"
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    messageList.add(new androidRecyclerView.Message(counter++, readMessage, mConnectedDeviceName));
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                    Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    mChatService.connect(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    public void connect(View v) {
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    public void discoverable(View v) {
        ensureDiscoverable();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.refresh){
            if (Utility.isNetworkConnected(context)) {
                getDeviceIds();
            }
            return true;
        }else if(id==R.id.aboutus){

            return true;
        }
        else if(id==R.id.logout){
            try{
             logout();

            }catch (Exception e) {
                // TODO: handle exception
            }
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        try{
            Utility.showAlertDialog(BluetoothChat.this, "Logout", "Would you like to logout?");
            userPreferenceData.logoutUser();
            finishAffinity();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void getDeviceIds() {
        try {
             String Select_device=Utility.getSelect_Device().toString();
             JsonObject loginUserJsonObject = new JsonObject();
             loginUserJsonObject.addProperty("userid", "17");
            loginUserJsonObject.addProperty("authid", "37");
            loginUserJsonObject.addProperty("search", Select_device);
            loginUserJsonObject.addProperty("fetch", 1);
            loginUserJsonObject.addProperty("limit", 1);
            if (Utility.isNetworkConnected(context)) {
                myDialog = Utility.showProgressDialog(BluetoothChat.this, "some message");
                CommonUtilities.getBaseClassService(context, "mobile/manufacture/getAutoGeneratedIds", "").getfeeders(loginUserJsonObject, new Callback<JsonObject>() {
                    @Override
                    public void success(JsonObject jsonObject, Response response) {
                        if (jsonObject != null && jsonObject.toString().length() > 0) {
                            myDialog.dismiss();
                            Utility.hideLoader();
                            showDevices(jsonObject.toString());

                        }
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        if (retrofitError != null) {
                            myDialog.dismiss();
                            Utility.hideLoader();
                           // Utility.showAlertDialog(BluetoothChat.this, "Devices", retrofitError.toString());
                        }

                    }
                });
            } else {
                Toast.makeText(BluetoothChat.this, R.string.internet, Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showDevices(String s) {
        try{
            String json_result = s.toString();
            JSONObject json = new JSONObject(json_result);
            String status = json.getString("status");
            String zero = "0".toString().trim();
            if (status.equals(zero)) {
                String error = json.getString("error");
                Utility.showAlertDialog(BluetoothChat.this, "Login", error);
                Utility.hideLoader();
            }else {
                try{
                    String data=json.getString("data");
                    JSONArray jsonArray1 = new JSONArray(data);
                    mylist.clear();
                    for (int i = 0; i < jsonArray1.length(); i++) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        JSONObject jObject = jsonArray1.getJSONObject(i);
                        map.put("manufacSerialNo", jObject.getString("manufacSerialNo"));
                        map.put("ManufacId", jObject.getString("ManufacId"));
                        map.put("ManufacDate", jObject.getString("ManufacDate"));
                        map.put("DevType", jObject.getString("DevType"));
                        map.put("DevId", jObject.getString("DevId"));
                        map.put("HWVer", jObject.getString("HWVer"));
                        map.put("BatchNo", jObject.getString("BatchNo"));
                        map.put("EncodedSerial", jObject.getString("EncodedSerial"));
                        map.put("status", jObject.getString("status"));
                        map.put("deliveryid", jObject.getString("deliveryid"));
                        map.put("deliverystatus", jObject.getString("deliverystatus"));
                        map.put("successId", jObject.getString("successId"));
                        map.put("successStatusMsg", jObject.getString("successStatusMsg"));
                        map.put("failedId", jObject.getString("failedId"));
                        map.put("failedStatusMsg", jObject.getString("failedStatusMsg"));
                        mylist.add(map);
                        selected_id.setText(jObject.getString("EncodedSerial"));
                    }

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void sendDeviceDetails(String successId,String manufacSerialNo) {
        try {
            JsonObject loginUserJsonObject = new JsonObject();
            loginUserJsonObject.addProperty("userid", "17");
            loginUserJsonObject.addProperty("authid", "37");
            loginUserJsonObject.addProperty("manufactureSNO",manufacSerialNo);
            loginUserJsonObject.addProperty("successId", successId);
           // loginUserJsonObject.addProperty("limit", 1);
            if (Utility.isNetworkConnected(context)) {
                myDialog = Utility.showProgressDialog(BluetoothChat.this, "some message");
                CommonUtilities.getBaseClassService(context, "mobile/manufacture/saveDeviceNotedInfo", "").getfeeders(loginUserJsonObject, new Callback<JsonObject>() {
                    @Override
                    public void success(JsonObject jsonObject, Response response) {
                        if (jsonObject != null && jsonObject.toString().length() > 0) {
                            myDialog.dismiss();
                            Utility.hideLoader();
                            System.out.println(jsonObject.toString());
                            statusMessage(jsonObject.toString());

                        }
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        if (retrofitError != null) {
                            myDialog.dismiss();
                            Utility.hideLoader();
                            // Utility.showAlertDialog(BluetoothChat.this, "Devices", retrofitError.toString());
                        }

                    }
                });
            } else {
                Toast.makeText(BluetoothChat.this, R.string.internet, Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void statusMessage(String s) {
        try {
            String json_result = s.toString();
            JSONObject json = new JSONObject(json_result);
            String status = json.getString("status");
            String zero = "0".toString().trim();
            if (status.equals(zero)) {
                String error = json.getString("error");
                Utility.showAlertDialog(BluetoothChat.this, "Device Saving", error);
                Utility.hideLoader();
            } else {
                String data = json.getString("data");
                Toast.makeText(BluetoothChat.this, data, Toast.LENGTH_SHORT).show();
                selected_id.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}