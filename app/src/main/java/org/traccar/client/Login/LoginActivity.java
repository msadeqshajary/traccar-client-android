package org.traccar.client.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.traccar.client.Constants;
import org.traccar.client.MainActivity;
import org.traccar.client.R;
import org.traccar.client.RequestManager;

import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        addDevice();


        TextView usernameTv = findViewById(R.id.login_username_tv);
        final EditText username = findViewById(R.id.login_username);

        TextView passwordTv = findViewById(R.id.login_password_tv);
        final EditText password = findViewById(R.id.login_password);


        Typeface sansMedium = Typeface.createFromAsset(getAssets(),"fonts/sansmedium.ttf");
        Typeface sansLight = Typeface.createFromAsset(getAssets(),"fonts/sanslight.ttf");
        Typeface sans = Typeface.createFromAsset(getAssets(),"fonts/sans.ttf");

        usernameTv.setTypeface(sansLight);
        passwordTv.setTypeface(sansLight);

        TextView title = findViewById(R.id.login_title);
        title.setTypeface(sans);

        final ProgressBar pb = findViewById(R.id.login_progress);

        Button submit = findViewById(R.id.login_submit);
        submit.setTypeface(sansMedium);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                username.setEnabled(false);
                password.setEnabled(false);

                if(username.getText().toString().length()<1 || password.getText().toString().length()<1) {
                    Toast.makeText(getApplicationContext(),"لطفا مقادیر خواسته شده را تکمیل نمائید",Toast.LENGTH_LONG).show();
                }
                else{
                    JSONObject jsonObject = new JSONObject();
                    try {
                        String url = "http://185.142.158.195:10023/api/users/login";

                        jsonObject.put("username", username.getText().toString());
                        jsonObject.put("password", password.getText().toString());
                        jsonObject.put("lang", "fa");

                        RequestManager requestManager = new RequestManager();
                        requestManager.sendRequestAsync(url, new RequestManager.RequestHandler() {
                            @Override
                            public void onComplete(boolean success) {
                                Log.e("RESULT", success + " ");
                                if (!success) {
                                    Toast.makeText(getApplicationContext(), "مشکل در ورود به نرم افزار، لطفا دوباره بررسی کنید", Toast.LENGTH_LONG).show();
                                    password.setEnabled(true);
                                    username.setEnabled(true);
                                    pb.setVisibility(View.INVISIBLE);
                                }
                            }
                        });

                        requestManager.setData(jsonObject.toString());
                        requestManager.setListener(new RequestManager.RequestListener() {
                            @Override
                            public void onResultCompleted(String result) {
                                if (result.equals("Unauthorized")) {
                                    Toast.makeText(getApplicationContext(), "نام کاربری یا رمز عبور اشتباه است.", Toast.LENGTH_LONG).show();
                                    password.setEnabled(true);
                                    username.setEnabled(true);
                                    pb.setVisibility(View.INVISIBLE);
                                } else {
                                    try {
                                        JSONObject jo = new JSONObject(result);
                                        preferences.edit().putString("UserId", jo.getString("userId")).apply();
                                        preferences.edit().putString("AccessToken", jo.getString("id")).apply();
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                                addDevice();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_toolbar);
        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setTypeface(sans);

        ImageView menu = toolbar.findViewById(R.id.toolbar_menu);
        menu.setVisibility(View.GONE);


    }

    private void addDevice(){
        if(preferences.getString("DeviceId","").equals("")) {
            JSONObject jsonObject = new JSONObject();
            try {
                String randomCode = getRandomGeneratedCode();
                preferences.edit().putString("LocalKey",randomCode).apply();
                jsonObject.put("deviceUniqueId", randomCode);
                jsonObject.put("name", "string");
                jsonObject.put("model", Constants.DEVICE_MODEL);
                jsonObject.put("brand", Constants.DEVICE_BRAND);
                jsonObject.put("icon", "string");

                String url = "http://185.142.158.195:10023/api/devices";
                RequestManager requestManager = new RequestManager();
                requestManager.sendRequestAsync(url, new RequestManager.RequestHandler() {
                    @Override
                    public void onComplete(boolean success) {
                        Log.e("SEND", success + "");
                    }
                });

                requestManager.setData(jsonObject.toString());
                requestManager.setListener(new RequestManager.RequestListener() {
                    @Override
                    public void onResultCompleted(String result) {
                        if (result != null) {
                            try {
                                JSONObject jo = new JSONObject(result);
                                String deviceId = jo.getString("id");
                                preferences.edit().putString("DeviceId", deviceId).apply();
                                Log.e("DEVICE ID", deviceId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String getRandomGeneratedCode() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        long randomLength = 8;
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
