package com.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.Utils.UserPreferenceData;
import com.Utils.Utility;
import com.database.CommonUtilities;
import com.google.gson.JsonObject;
import com.javacodegeeks.R;
import com.javacodegeeks.Bluetooth.BluetoothChat;

import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;



public class LoginActivity extends AppCompatActivity {
  EditText LoginUserName,LoginUserPassword;
    Button sign_in;
    public UserPreferenceData userPreferenceData;
    Context context;
    ProgressDialog myDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        context=getApplicationContext();
        userPreferenceData=new UserPreferenceData(getApplicationContext());
        if (userPreferenceData.isUserLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            LoginUserName=(EditText)findViewById(R.id.user_name);
            LoginUserPassword=(EditText)findViewById(R.id.password);
            sign_in=(Button)findViewById(R.id.signin);
            sign_in.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        String LoginUserNameString = LoginUserName
                                .getText().toString().trim();
                        String LoginUserPasswordString = LoginUserPassword
                                .getText().toString().trim();
                        if (LoginUserNameString.isEmpty()) {
                            Toast.makeText(LoginActivity.this,R.string.userid,
                                    Toast.LENGTH_SHORT).show();
                        } else if(LoginUserPasswordString.isEmpty()){
                            Toast.makeText(LoginActivity.this,
                                    R.string.passwd,
                                    Toast.LENGTH_SHORT).show();
                        }else {
                          try{
                              JsonObject loginUserJsonObject = new JsonObject();
                              loginUserJsonObject.addProperty("username", LoginUserName.getText().toString().trim());
                              loginUserJsonObject.addProperty("password", LoginUserPassword.getText().toString());
                              if((Utility.isNetworkConnected(context))){
                                  myDialog = Utility.showProgressDialog(LoginActivity.this, "some message");
                                  CommonUtilities.getBaseClassService(context, "mobile/pondmother_basicmodes/login", "").loginUser(loginUserJsonObject, new Callback<JsonObject>() {
                                      @Override
                                      public void success(JsonObject jsonObject, Response response) {
                                          if (jsonObject != null && jsonObject.toString().length() > 0) {
                                              myDialog.dismiss();
                                              Utility.hideLoader();
                                              loginSaveData(jsonObject.toString());
                                          }
                                      }
                                      @Override
                                      public void failure(RetrofitError retrofitError) {
                                          if (retrofitError != null) {
                                              myDialog.dismiss();
                                              Utility.hideLoader();
                                              Utility.showAlertDialog(getApplicationContext(), "Login", retrofitError.toString());
                                          }

                                      }
                                  });

                              }else{
                                  Toast.makeText(getApplicationContext(),R.string.internet,Toast.LENGTH_SHORT).show();

                              }

                          }catch (Exception e){
                              e.printStackTrace();
                          }
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }


    }
    private void loginSaveData(String s) {
        try {
            String json_result = s.toString();
            JSONObject json = new JSONObject(json_result);
            String status = json.getString("status");
            String zero = "0".toString().trim();
            if (status.equals(zero)) {
                String error = json.getString("error");
                Utility.showAlertDialog(LoginActivity.this, "Login", error);
                Utility.hideLoader();
            }else{
                try {

                    String user_id = json.getString("user_id");
                    String user_data = json.getString("data");
                    JSONObject jsn2 = new JSONObject(user_data);
                    String FirstName = jsn2.getString("firstname");
                    String lastname = jsn2.getString("lastname");
                    String mobilenumber = jsn2.getString("mobilenumber");
                    String emailid = jsn2.getString("emailid");
                    String timezone = jsn2.getString("timezone");
                    //user shared preference data
                    userPreferenceData.put("user_id", user_id);
                    userPreferenceData.put("firstname",FirstName);
                    userPreferenceData.put("lastname",lastname);
                    userPreferenceData.put("mobilenumber",mobilenumber);
                    userPreferenceData.put("emailid",emailid);
                    userPreferenceData.put("timezone",timezone);
                    userPreferenceData.createUserLoginSession(LoginUserName.getText().toString(), LoginUserPassword.getText().toString());
                    try {
                        Intent devices = new Intent(LoginActivity.this,MainActivity.class);
                        devices.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(devices);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
