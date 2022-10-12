package com.example.parcelapp;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.parcelapp.utils.JSONApiUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Objects;


public class LoginActivity extends AppCompatActivity {

    private final String PREF_FILE = BuildConfig.APPLICATION_ID.replace(".", "_");


    Button loginBtn;
    Button regBtn;

    EditText ipAddress, userEmail, userPassword;

    RadioGroup category;

    String vendorUrl, customerUrl, courierUrl, fetchedIP, BASE_URL;

    String getStatus, getData;

    String responseBody = "";

    JSONObject ourResult;

    String alertMsg;

    String[][] reqBody;

    String ipAddString, userEmailStr, userPasswordStr, userCategoryStr;

    SharedPreferences myPreference;
    SharedPreferences.Editor myEditor;

    List<String> resBodyArr;
    String responseString;

    JSONApiUtility jsonApiUtility;

    private ConnectivityManager connectivityManager;
    public NetworkReceiver networkReceiver;
    boolean isOnline;


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkReceiver = new NetworkReceiver(connectivityManager);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        isOnline = networkInfo != null && networkInfo.isConnected();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(networkReceiver, filter);

        myPreference = this.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);

        onShowNetworkStatus();


        loginBtn = findViewById(R.id.loginBtn);
        regBtn = findViewById(R.id.regBtn);

        ipAddress = findViewById(R.id.ipAddress);
        userEmail = findViewById(R.id.userEmail);
        userPassword = findViewById(R.id.userPassword);


        category = findViewById(R.id.userCat);



        ipAddress.setOnFocusChangeListener(handleSaveIpAddress);


        ImageView logo = findViewById(R.id.logo);
        logo.setImageResource(R.drawable.parcel_ico);

        new Thread(() -> loginBtn.setOnClickListener(handleLogin)).start();
        regBtn.setOnClickListener(handleReg);
    }

    @SuppressLint("NonConstantResourceId")
    private final View.OnClickListener handleLogin = v -> {

        userEmailStr = userEmail.getText().toString();
        userPasswordStr = userPassword.getText().toString();
        ipAddString = ipAddress.getText().toString();
        responseBody = "";

        switch (category.getCheckedRadioButtonId()) {
            case R.id.customer: userCategoryStr = "Customer";
                break;
            case R.id.vendor: userCategoryStr = "Vendor";
                break;
            case R.id.courier: userCategoryStr = "Courier";
                break;
            default: userCategoryStr = "";
        }

        if (!ipAddString.equals("") && !userEmailStr.equals("") && !userCategoryStr.equals("") && !userPasswordStr.equals("")) {
            fetchedIP = myPreference.getString("ipAddress", "n/a");
            if (!fetchedIP.equals("n/a")) {
                BASE_URL = "http://" + fetchedIP + ":7000/";
                if (userCategoryStr.equals("Customer")) {
                    customerUrl = BASE_URL + "parcel_customer/customer_login_mobile/";

                    reqBody = new String[][]{{"email", userEmailStr}, {"password", userPasswordStr}};
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                jsonApiUtility = new JSONApiUtility(customerUrl, "POST", "UTF-8");
                            } catch (IOException e) {
                                e.printStackTrace();
                                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Error Connecting to Server", Toast.LENGTH_LONG).show());
                            } catch(NullPointerException npe) {
                                npe.printStackTrace();
                            }

                            try {
                                jsonApiUtility.addArrStrReqBody(reqBody);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (NullPointerException npe) {
                                npe.printStackTrace();
                            }

                            try {
                                resBodyArr = jsonApiUtility.finish();
                            } catch (IOException e) {
                                e.printStackTrace();
                                runOnUiThread(() -> Toast.makeText(LoginActivity.this,
                                        "Error Connecting to the server, check your connection",
                                        Toast.LENGTH_LONG).show());
                            } catch (NullPointerException npe) {
                                npe.printStackTrace();
                            }

                            if (resBodyArr != null) {
                                for(String line : resBodyArr) {
                                    responseString = line;
                                }
                            }

                            runOnUiThread(() -> {
                                if (responseString != null) {
                                    try {
                                        ourResult = new JSONObject(responseString);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        getStatus = ourResult.getString("status");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        getData = ourResult.getString("data");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                        switch (getStatus) {
                                            case "success":
                                                try {
                                                    myEditor = myPreference.edit();
                                                    myEditor.putString("customerData", getData);
                                                    myEditor.apply();
                                                    Intent goMain = new Intent(LoginActivity.this, CustomerActivity.class);
                                                    startActivity(goMain);
                                                } catch (NullPointerException npe) {
                                                    npe.printStackTrace();
                                                }
                                                break;
                                            case "password-error":
                                                alertMsg = getData + " Visit the website to reset your password";
                                                runOnUiThread(() -> {
                                                    if(!isFinishing()) {
                                                        new AlertDialog.Builder(LoginActivity.this)
                                                                .setCancelable(true)
                                                                .setTitle("Password-Error!")
                                                                .setMessage(alertMsg)
                                                                .setPositiveButton("Ok", null)
                                                                .setNegativeButton("Cancel", null)
                                                                .create()
                                                                .show();
                                                    }
                                                });


                                                break;
                                            case "error":
                                                runOnUiThread(() -> {
                                                    if(!isFinishing()) {
                                                        new AlertDialog.Builder(LoginActivity.this)
                                                                .setCancelable(true)
                                                                .setTitle("Login Error!")
                                                                .setMessage(getData)
                                                                .setPositiveButton("Ok", null)
                                                                .setNegativeButton("Cancel", null)
                                                                .create()
                                                                .show();
                                                    }
                                                });
                                                break;
                                        }//
                                    }
                                });
                        }
                    }).start();
                }

                if (userCategoryStr.equals("Courier")) {
                    courierUrl = BASE_URL + "parcel_backends/courier_login_mobile/";

                    reqBody = new String[][]{{"email", userEmailStr}, {"password", userPasswordStr}};

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                jsonApiUtility = new JSONApiUtility(courierUrl, "POST", "UTF-8");
                            } catch (IOException e) {
                                e.printStackTrace();
                                runOnUiThread(() -> Toast.makeText(LoginActivity.this,
                                        "Error Connecting to the server, check your connection",
                                        Toast.LENGTH_LONG).show());
                            } catch (NullPointerException npe) {
                                npe.printStackTrace();
                            }

                            try {
                                jsonApiUtility.addArrStrReqBody(reqBody);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (NullPointerException npe) {
                                npe.printStackTrace();
                            }

                            try {
                                resBodyArr = jsonApiUtility.finish();
                            } catch (IOException e) {
                                e.printStackTrace();
                                runOnUiThread(() -> Toast.makeText(LoginActivity.this,
                                        "Error Connecting to the server, check your connection",
                                        Toast.LENGTH_LONG).show());
                            } catch (NullPointerException npe) {
                                npe.printStackTrace();
                            }

                            if (resBodyArr != null) {
                                for(String line : resBodyArr) {
                                    responseString = line;
                                }
                            }
                            runOnUiThread(() -> {
                                if(responseString != null) {
                                    try {
                                        ourResult = new JSONObject(responseString);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        getStatus = ourResult.getString("status");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        getData = ourResult.getString("data");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    responseBody = "";
                                            switch (getStatus) {
                                                case "success":
                                                    try {
                                                        myEditor = myPreference.edit();
                                                        myEditor.putString("courierData", getData);
                                                        myEditor.apply();
                                                        Intent goMain = new Intent(LoginActivity.this, CourierActivity.class);
                                                        startActivity(goMain);
                                                    } catch (NullPointerException npe) {
                                                        npe.printStackTrace();
                                                    }
                                                    break;
                                                case "password-error":
                                                    alertMsg = getData + " Visit the website to reset your password";
                                                    runOnUiThread(() -> {
                                                        if(!isFinishing()) {
                                                            new AlertDialog.Builder(LoginActivity.this)
                                                                    .setCancelable(true)
                                                                    .setTitle("Password-Error!")
                                                                    .setMessage(alertMsg)
                                                                    .setPositiveButton("Ok", null)
                                                                    .setNegativeButton("Cancel", null)
                                                                    .create()
                                                                    .show();
                                                        }
                                                    });


                                                    break;
                                                case "error":
                                                    runOnUiThread(() -> {
                                                        if(!isFinishing()) {
                                                            new AlertDialog.Builder(LoginActivity.this)
                                                                    .setCancelable(true)
                                                                    .setTitle("Login Error!")
                                                                    .setMessage(getData)
                                                                    .setPositiveButton("Ok", null)
                                                                    .setNegativeButton("Cancel", null)
                                                                    .create()
                                                                    .show();
                                                        }
                                                    });
                                                    break;
                                            }//
                                }


                            });
                            }
                    }).start();
                }

                if (userCategoryStr.equals("Vendor")) {
                    vendorUrl = BASE_URL + "parcel_backends/vendor_login_mobile/";

                    reqBody = new String[][]{{"email", userEmailStr}, {"password", userPasswordStr}};
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                jsonApiUtility = new JSONApiUtility(vendorUrl, "POST", "UTF-8");
                            } catch (IOException e) {
                                e.printStackTrace();
                                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Error Connecting to Server", Toast.LENGTH_LONG).show());
                            } catch (NullPointerException npe) {
                                npe.printStackTrace();
                            }

                            try {
                                jsonApiUtility.addArrStrReqBody(reqBody);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (NullPointerException npe) {
                                npe.printStackTrace();
                            }

                            try {
                                resBodyArr = jsonApiUtility.finish();
                            } catch (IOException e) {
                                e.printStackTrace();
                                runOnUiThread(() -> Toast.makeText(LoginActivity.this,
                                        "Error Connecting to the server, check your connection",
                                        Toast.LENGTH_LONG).show());
                            } catch (NullPointerException npe) {
                                npe.printStackTrace();
                            }

                            if (resBodyArr != null) {
                                for(String line : resBodyArr) {
                                    responseString = line;
                                }
                            }

                            runOnUiThread(() -> {
                                if(responseString != null) {

                                    try {
                                        ourResult = new JSONObject(responseString);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        getStatus = ourResult.getString("status");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        getData = ourResult.getString("data");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    responseBody = "";
                                            switch (getStatus) {
                                                case "success":
                                                    try {
                                                        myEditor = myPreference.edit();
                                                        myEditor.putString("vendorData", getData);
                                                        myEditor.apply();
                                                        Intent goMain = new Intent(LoginActivity.this, VendorActivity.class);
                                                        startActivity(goMain);
                                                    } catch (NullPointerException npe) {
                                                        npe.printStackTrace();
                                                    }
                                                    break;
                                                case "password-error":
                                                    alertMsg = getData + " Visit the website to reset your password";
                                                    runOnUiThread(() -> {
                                                        if(!isFinishing()) {
                                                            new AlertDialog.Builder(LoginActivity.this)
                                                                    .setCancelable(true)
                                                                    .setTitle("Password-Error!")
                                                                    .setMessage(alertMsg)
                                                                    .setPositiveButton("Ok", null)
                                                                    .setNegativeButton("Cancel", null)
                                                                    .create()
                                                                    .show();
                                                        }
                                                    });


                                                    break;
                                                case "error":
                                                    runOnUiThread(() -> {
                                                        if(!isFinishing()) {
                                                            new AlertDialog.Builder(LoginActivity.this)
                                                                    .setCancelable(true)
                                                                    .setTitle("Login Error!")
                                                                    .setMessage(getData)
                                                                    .setPositiveButton("Ok", null)
                                                                    .setNegativeButton("Cancel", null)
                                                                    .create()
                                                                    .show();
                                                        }
                                                    });
                                                    break;
                                            }
                                }
                            });
                        }
                    }).start();
                }
            }
        } else {
            runOnUiThread(() -> {
                if(!isFinishing()) {
                    new AlertDialog.Builder(LoginActivity.this).setTitle("Important!")
                            .setCancelable(true)
                            .setMessage("Enter all required fields please.")
                            .setPositiveButton("Ok", null)
                            .setNegativeButton("Cancel", null)
                            .create()
                            .show();
                }
            });
        }
    };

    @SuppressLint("NonConstantResourceId")
    private final View.OnClickListener handleReg = v -> {
        fetchedIP = myPreference.getString("ipAddress", "n/a");
        if(fetchedIP.equals("n/a")) {
            Toast.makeText(LoginActivity.this, "Enter Ip Address please",
                    Toast.LENGTH_LONG).show();
        } else {

            switch (category.getCheckedRadioButtonId()) {
                case R.id.customer: userCategoryStr = "Customer";
                    break;
                case R.id.vendor: userCategoryStr = "Vendor";
                    break;
                case R.id.courier: userCategoryStr = "Courier";
                    break;
            }
            if (userCategoryStr != null) {
                switch (userCategoryStr) {
                    case "Customer":
                        Intent goCusReg = new Intent(LoginActivity.this, RegisterCustomerActivity.class);
                        startActivity(goCusReg);
                        break;
                    case "Vendor":
                        Intent goVenReg = new Intent(LoginActivity.this, RegisterVendorActivity.class);
                        startActivity(goVenReg);
                        break;
                    case "Courier":
                        Intent goCourReg = new Intent(LoginActivity.this, RegisterCourierActivity.class);
                        startActivity(goCourReg);
                        break;
                }
            } else {
                runOnUiThread(() -> {
                    if (!isFinishing()) {
                        new AlertDialog.Builder(LoginActivity.this).setTitle("Required!")
                                .setCancelable(true)
                                .setMessage("Choose a Category, please.")
                                .setPositiveButton("Ok", null)
                                .setNegativeButton("Cancel", null)
                                .create()
                                .show();
                    }
                });
            }
        }
    };

    private final View.OnFocusChangeListener handleSaveIpAddress = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                ipAddString = ipAddress.getText().toString();
                try {
                    myEditor = myPreference.edit();
                    myEditor.putString("ipAddress", ipAddString);
                    myEditor.apply();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }
                Toast.makeText(LoginActivity.this, "IP Address Saved", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(networkReceiver != null) {
            unregisterReceiver(networkReceiver);
        }
    }

    public void onShowNetworkStatus() {
        if(connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isConnected()) {
                Toast.makeText(LoginActivity.this, "Network Available", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(LoginActivity.this, "Network Not Available", Toast.LENGTH_LONG).show();
            }
        }
    }
}
