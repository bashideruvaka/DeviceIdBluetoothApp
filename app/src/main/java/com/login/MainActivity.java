package com.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import com.Utils.Utility;
import com.database.CommonUtilities;
import com.database.DBHelper;
import com.google.gson.JsonObject;
import com.javacodegeeks.R;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import androidRecyclerView.DeviceIds;
import androidRecyclerView.DevicesIDAdapter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    EditText home_count;
    RadioButton home_pgrd,home_pmrd;
    Button home_get_id;
    Context context;
    ProgressDialog myDialog;
    ArrayList<HashMap<String,String >> pg_list=new ArrayList<HashMap<String,String>>();
    ArrayList<HashMap<String,String >> pm_list=new ArrayList<HashMap<String,String>>();
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private DevicesIDAdapter mAdapter;
    private List<DeviceIds> deviceidsList = new ArrayList<DeviceIds>();
    public int counter = 0;
    DBHelper helper;
    SQLiteDatabase database;
    SQLiteStatement statement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.signinupshape));
        context = getApplicationContext();
        home_pgrd=(RadioButton)findViewById(R.id.home_pgrd);
        home_pmrd=(RadioButton)findViewById(R.id.home_pmrd);
        home_count=(EditText)findViewById(R.id.home_count);
        home_get_id=(Button)findViewById(R.id.home_get_id);
        mRecyclerView = (RecyclerView) findViewById(R.id.devices_recycler_view );
        // mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        home_pgrd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(buttonView.isChecked()){
                    home_pgrd.setChecked(true);
                    home_pmrd.setChecked(false);
                    Utility.addChecked("2");
                }
            }
        });
        home_pmrd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(buttonView.isChecked()){
                    home_pmrd.setChecked(true);
                    home_pgrd.setChecked(false);
                    Utility.addChecked("1");
                }
            }
        });
        home_get_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String device_type=  Utility.getSelect_Device().toString().trim();
                String str_count=home_count.getText().toString().trim();
                if(device_type.equals("NOT")){
                    Toast.makeText(MainActivity.this,"Please Select Device Type",Toast.LENGTH_SHORT).show();
                } else  if(str_count.isEmpty()){
                    Toast.makeText(MainActivity.this,"Please Select Device Range",Toast.LENGTH_SHORT).show();
                }else {
                    //pm checked
                    if(device_type.equals("1")){
                        try {
                            helper = new DBHelper(MainActivity.this);
                            database = helper.getReadableDatabase();
                            String query = ("select * from pm_deviceIds");
                            Cursor cursor = database.rawQuery(query, null);
                            System.out.println(cursor.getCount());
                            int j=cursor.getCount();
                            if (cursor.getCount() > 0) {
                                home_count.setText(Integer.toString(j));
                                show_PmData();
                            } else {
                                getDeviceIds();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else if(device_type.equals("2")){
                        //pg checked
                        try {
                            helper = new DBHelper(MainActivity.this);
                            database = helper.getReadableDatabase();
                            String query1 = ("select * from pg_deviceIds");
                            Cursor cursor1 = database.rawQuery(query1, null);
                            System.out.println(cursor1.getCount());
                            int j=cursor1.getCount();
                            if (cursor1.getCount() > 0) {
                                home_count.setText(Integer.toString(j));
                                show_PgData();
                            } else {
                                getDeviceIds();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }

            }
        });
    }
    private void show_PgData() {
        try{
            deviceidsList.clear();
            helper=new DBHelper(MainActivity.this);
            database=helper.getReadableDatabase();
            String query = ("select * from pg_deviceIds");
            Cursor cursor = database.rawQuery(query, null);
            if(cursor != null){
                if(cursor.moveToFirst()){
                    do {
                        // get  the  data into array,or class variable
                        String EncodedSerial = cursor.getString(cursor.getColumnIndex("EncodedSerial"));
                        String status = cursor.getString(cursor.getColumnIndex("status"));
                        String DevType = cursor.getString(cursor.getColumnIndex("DevId"));
                        Utility.addChecked(DevType);
                        deviceidsList.add(new androidRecyclerView.DeviceIds(EncodedSerial, status));
                    } while (cursor.moveToNext());
                }
            }
            // add data to recyclerview
            mAdapter = new DevicesIDAdapter(this, deviceidsList);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void show_PmData() {
        try{
            deviceidsList.clear();
            helper=new DBHelper(MainActivity.this);
            database=helper.getReadableDatabase();
            String query = ("select * from pm_deviceIds");
            Cursor cursor = database.rawQuery(query, null);
            if(cursor != null){
                if(cursor.moveToFirst()){
                    do {
                        // get  the  data into array,or class variable
                        String EncodedSerial = cursor.getString(cursor.getColumnIndex("EncodedSerial"));
                        String status = cursor.getString(cursor.getColumnIndex("status"));
                        String DevType = cursor.getString(cursor.getColumnIndex("DevId"));
                        Utility.addChecked(DevType);
                        deviceidsList.add(new androidRecyclerView.DeviceIds(EncodedSerial, status));
                    } while (cursor.moveToNext());
                }
            }
            // add data to recyclerview
            mAdapter = new DevicesIDAdapter(this, deviceidsList);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_get_id:
                break;
        }
    }
    private void getDeviceIds() {
        try {
            String Select_device=Utility.getSelect_Device().toString();
            String str_count=home_count.getText().toString().trim();
            JsonObject loginUserJsonObject = new JsonObject();
            loginUserJsonObject.addProperty("limit", str_count);
            loginUserJsonObject.addProperty("fetch", "0");
            loginUserJsonObject.addProperty("search", Select_device);
            loginUserJsonObject.addProperty("manfacid", "");
            loginUserJsonObject.addProperty("devicetype", Select_device);
            loginUserJsonObject.addProperty("hwversion", "");
            loginUserJsonObject.addProperty("batchno", "");
            loginUserJsonObject.addProperty("manfacdate", "0");
            loginUserJsonObject.addProperty("dispatchdate", "");
            loginUserJsonObject.addProperty("currentstatus","");
            loginUserJsonObject.addProperty("userid", "");
            loginUserJsonObject.addProperty("authid", "");
            loginUserJsonObject.addProperty("sptype","2");
            if (Utility.isNetworkConnected(context)) {
                myDialog = Utility.showProgressDialog(MainActivity.this, "some message");
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

                        }

                    }
                });
            } else {
                Toast.makeText(MainActivity.this, R.string.internet, Toast.LENGTH_SHORT).show();
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
                Utility.showAlertDialog(MainActivity.this, "Home", error);
                Utility.hideLoader();
            }else {
                try{
                    String data=json.getString("data");
                    JSONArray jsonArray1 = new JSONArray(data);
                    String Select_device=Utility.getSelect_Device().toString();
                    if(Select_device.equals("2")){
                        pg_list.clear();
                        PG_DeleteDataBase();
                    }else if(Select_device.equals("1")){
                        pm_list.clear();
                        PM_DeleteDataBase();
                    }
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
                        if(jObject.getString("DevId").equals("2")){
                            pg_list.add(map);
                            Utility.addChecked("2");
                            System.out.println("2"+jObject.getString("DevId"));
                            try{
                                helper = new DBHelper(MainActivity.this);
                                database = helper.getReadableDatabase();
                                statement = database.compileStatement("insert into pg_deviceIds values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                                statement.bindString(2, jObject.getString("manufacSerialNo"));
                                statement.bindString(3, jObject.getString("ManufacId"));
                                statement.bindString(4, jObject.getString("ManufacDate"));
                                statement.bindString(5, jObject.getString("DevType"));
                                statement.bindString(6, jObject.getString("DevId"));
                                statement.bindString(7, jObject.getString("HWVer"));
                                statement.bindString(8, jObject.getString("BatchNo"));
                                statement.bindString(9, jObject.getString("EncodedSerial"));
                                statement.bindString(10, jObject.getString("status"));
                                statement.bindString(11, jObject.getString("deliveryid"));
                                statement.bindString(12, jObject.getString("deliverystatus"));
                                statement.bindString(13, jObject.getString("successId"));
                                statement.bindString(14, jObject.getString("successStatusMsg"));
                                statement.bindString(15, jObject.getString("failedId"));
                                statement.bindString(16, jObject.getString("failedStatusMsg"));
                                statement.executeInsert();
                                database.close();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else if(jObject.getString("DevId").equals("1")){
                            pm_list.add(map);
                            Utility.addChecked("1");
                            System.out.println("1"+jObject.getString("DevId"));
                            try{
                                helper = new DBHelper(MainActivity.this);
                                database = helper.getReadableDatabase();
                                statement = database.compileStatement("insert into pm_deviceIds values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                                statement.bindString(2, jObject.getString("manufacSerialNo"));
                                statement.bindString(3, jObject.getString("ManufacId"));
                                statement.bindString(4, jObject.getString("ManufacDate"));
                                statement.bindString(5, jObject.getString("DevType"));
                                statement.bindString(6, jObject.getString("DevId"));
                                statement.bindString(7, jObject.getString("HWVer"));
                                statement.bindString(8, jObject.getString("BatchNo"));
                                statement.bindString(9, jObject.getString("EncodedSerial"));
                                statement.bindString(10, jObject.getString("status"));
                                statement.bindString(11, jObject.getString("deliveryid"));
                                statement.bindString(12, jObject.getString("deliverystatus"));
                                statement.bindString(13, jObject.getString("successId"));
                                statement.bindString(14, jObject.getString("successStatusMsg"));
                                statement.bindString(15, jObject.getString("failedId"));
                                statement.bindString(16, jObject.getString("failedStatusMsg"));
                                statement.executeInsert();
                                database.close();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                    if(Select_device.equals("2")){

                        show_PgData();
                    }else if(Select_device.equals("1")){

                        show_PmData();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void PG_DeleteDataBase() {
        try {
            helper = new DBHelper(getApplicationContext());
            database = helper.getReadableDatabase();
            database.delete("pg_deviceIds", null, null);
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void PM_DeleteDataBase() {
        try {
            helper = new DBHelper(getApplicationContext());
            database = helper.getReadableDatabase();
            database.delete("pm_deviceIds", null, null);
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public SimpleDividerItemDecoration(Context context) {
            mDivider = context.getResources().getDrawable(R.drawable.seprator3);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

}
