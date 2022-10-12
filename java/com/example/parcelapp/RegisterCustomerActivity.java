package com.example.parcelapp;


import android.content.Context;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.view.View;
import android.view.Window;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.parcelapp.utils.JSONApiUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class RegisterCustomerActivity extends AppCompatActivity {

    private final String PREF_FILE = BuildConfig.APPLICATION_ID.replace(".", "_");


    JSONApiUtility jsonApiUtility;


    Button registerCus;

    EditText cusFirName, cusLasName, cusCon, cusSta, cusStr,
            cusPhone, cusEmail, cusPassword, cusRetPass;


    SharedPreferences myPreference;
    String fetchedIP, BASE_URL, routeUrl, reqUrl;
    String charSet = "UTF-8";

    String first_name, last_name, country, state,
            street, phone_no, email, password, retPass, reg_date;

    boolean is_email_verified;

    List<String> responseBody;

    String responseString;

    JSONObject jsonRes;

    String status, data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.activity_register_customer);

        myPreference = this.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);

        registerCus = findViewById(R.id.registerCus);
        cusFirName = findViewById(R.id.cusFirName);
        cusLasName = findViewById(R.id.cusLasName);
        cusCon = findViewById(R.id.cusCon);
        cusSta = findViewById(R.id.cusSta);
        cusStr = findViewById(R.id.cusStr);
        cusPhone = findViewById(R.id.cusPhone);
        cusEmail = findViewById(R.id.cusEmail);
        cusPassword = findViewById(R.id.cusPassword);
        cusRetPass = findViewById(R.id.cusRetPass);

        registerCus.setOnClickListener(handleRegCusForm);
    }


    private int checkBlankInput(String[] inputs) {
        int count = 0;
        for(String input : inputs) {
            if(input.equals("")) count++;
        }
        return  count;
    }

    private final View.OnClickListener handleRegCusForm = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            fetchedIP = myPreference.getString("ipAddress", "n/a");
            BASE_URL = "http://" + fetchedIP + ":7000/";
            routeUrl = "parcel_customer/reg_customer_mobile/";
            reqUrl = BASE_URL + routeUrl;

            first_name = cusFirName.getText().toString(); last_name = cusLasName.getText().toString();
            country = cusCon.getText().toString(); state = cusSta.getText().toString();
            street = cusStr.getText().toString();
            phone_no = cusPhone.getText().toString(); email = cusEmail.getText().toString();
            password = cusPassword.getText().toString(); retPass = cusRetPass.getText().toString(); reg_date = "2022-08-16T15:44:343Z";
            is_email_verified = false;


            String[] courInputs = {first_name, last_name, country, state, street, phone_no, email, password, retPass, reg_date};

            int inputErrCount = checkBlankInput(courInputs);


            if (fetchedIP != null && reqUrl != null && password.equals(retPass) && inputErrCount == 0) {

                new Thread(() -> {
                    try {
                        jsonApiUtility = new JSONApiUtility(reqUrl,"POST", charSet);
                    } catch (IOException e) {
                        runOnUiThread(() -> Toast.makeText(RegisterCustomerActivity.this,
                                "Error Connecting to the server, check your connection",
                                Toast.LENGTH_LONG).show());
                        e.printStackTrace();
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }

                    String[][] reqBodyString = {{"first_name", first_name}, {"last_name", last_name}, {"country", country},
                            {"state", state}, {"street", street}, {"phone_no", phone_no}, {"email", email}, {"password", password},
                            {"reg_date", reg_date}};

                    try {
                        jsonApiUtility.addArrStrReqBody(reqBodyString);
                    }  catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }

                    try {
                        jsonApiUtility.addBoolReqBody("is_email_verified", is_email_verified);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }

                    try {
                        responseBody = jsonApiUtility.finish();
                    } catch (IOException e) {
                        runOnUiThread(() -> Toast.makeText(RegisterCustomerActivity.this,
                                "Error Connecting to the server, check your connection",
                                Toast.LENGTH_LONG).show());
                        e.printStackTrace();
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }


                    try {
                        for(String line : responseBody) {
                            responseString = line;
                        }
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }

                    if(responseString != null) {
                        try {
                            jsonRes = new JSONObject(responseString);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            status = jsonRes.getString("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(status.equals("success")) {
                            try {
                                data = jsonRes.getString("data");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (data != null) {
                                runOnUiThread(() -> runOnUiThread(() -> {
                                    if(!isFinishing()) {
                                        new AlertDialog.Builder(RegisterCustomerActivity.this)
                                                .setCancelable(true)
                                                .setTitle("Success!")
                                                .setMessage(data)
                                                .setPositiveButton("Ok", null)
                                                .setNegativeButton("Cancel", null)
                                                .create()
                                                .show();
                                    }
                                }));
                            }
                        } else if(status.equals("error")) {
                            try {
                                data = jsonRes.getString("data");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (data != null) {
                                runOnUiThread(() -> runOnUiThread(() -> {
                                    if(!isFinishing()) {
                                        new AlertDialog.Builder(RegisterCustomerActivity.this)
                                                .setCancelable(true)
                                                .setTitle("Error!")
                                                .setMessage(data)
                                                .setPositiveButton("Ok", null)
                                                .setNegativeButton("Cancel", null)
                                                .create()
                                                .show();
                                    }
                                }));
                            }
                        } else {
                            runOnUiThread(() -> runOnUiThread(() -> {
                                if(!isFinishing()) {
                                    new AlertDialog.Builder(RegisterCustomerActivity.this)
                                            .setCancelable(true)
                                            .setTitle("Error!")
                                            .setMessage("An Internal Server Error occured.")
                                            .setPositiveButton("Ok", null)
                                            .setNegativeButton("Cancel", null)
                                            .create()
                                            .show();
                                }
                            }));
                        }
                    }
                }).start();
            } else {
               if (!password.equals(retPass)) {
                    runOnUiThread(() -> {
                        if(!isFinishing()) {
                            new AlertDialog.Builder(RegisterCustomerActivity.this)
                                    .setCancelable(true)
                                    .setTitle("Error!")
                                    .setMessage("Passwords do not match!")
                                    .setPositiveButton("Ok", null)
                                    .setNegativeButton("Cancel", null)
                                    .create()
                                    .show();
                        }
                    });
                }else {
                    String message = inputErrCount + "  input fields are left blank";
                    runOnUiThread(() -> {
                        if(!isFinishing()) {
                            new AlertDialog.Builder(RegisterCustomerActivity.this)
                                    .setCancelable(true)
                                    .setTitle("Required Input!")
                                    .setMessage(message)
                                    .setPositiveButton("Ok", null)
                                    .setNegativeButton("Cancel", null)
                                    .create()
                                    .show();
                        }
                    });

                }
            }
        }
    };
}
