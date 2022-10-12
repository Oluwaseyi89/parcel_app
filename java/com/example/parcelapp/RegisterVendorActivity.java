package com.example.parcelapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.parcelapp.utils.ImageLoaderHelper;
import com.example.parcelapp.utils.MultipartUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.Objects;


public class RegisterVendorActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private final String PREF_FILE = BuildConfig.APPLICATION_ID.replace(".", "_");

    ActivityResultLauncher<Intent>  imageActivityResultLauncher;

    ImageLoaderHelper imageLoaderHelper = new ImageLoaderHelper();

    MultipartUtility multipartUtility;


    ImageView potImg;
    TextView imgPath, venPolicyLink;
    Button registerVen;

    EditText venFirName, venLasName, venBusCon, venBusSta, venBusStr, venNin,
            venPhone, venEmail, venPassword, venRetPass, venCacNo;

    CheckBox venPolicy;

    SharedPreferences myPreference;
    String fetchedIP, BASE_URL, routeUrl, reqUrl;
    String charSet = "UTF-8";

    String first_name, last_name, bus_country, bus_state,
            bus_street, bus_category, cac_reg_no, nin,
            phone_no, email, password, retPass, reg_date;

    File vend_photo ;
    boolean ven_policy, is_email_verified;

    String photoPath;

    List<String> responseBody;

    String responseString;

    JSONObject jsonRes;

    String status, data;

    Spinner venBusCat;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.activity_register_vendor);

        myPreference = this.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);

        potImg = findViewById(R.id.potImg);
        imgPath = findViewById(R.id.imgPath);
        registerVen = findViewById(R.id.registerVen);
        venBusCat = findViewById(R.id.venbusCategory);
        venFirName = findViewById(R.id.venFirName);
        venLasName = findViewById(R.id.venLasName);
        venBusCon = findViewById(R.id.venBusCon);
        venBusSta = findViewById(R.id.venBusSta);
        venBusStr = findViewById(R.id.venBusStr);
        venCacNo = findViewById(R.id.venCacNo);
        venNin = findViewById(R.id.venNin);
        venPhone = findViewById(R.id.venPhone);
        venEmail = findViewById(R.id.venEmail);
        venPassword = findViewById(R.id.venPassword);
        venRetPass = findViewById(R.id.venRetPass);
        venPolicy = findViewById(R.id.venPolicy);
        venPolicyLink = findViewById(R.id.venPolicyLink);

        ArrayAdapter<CharSequence> busCatAdpt = ArrayAdapter.createFromResource(RegisterVendorActivity.this,
                R.array.business_categories, android.R.layout.simple_spinner_item);

        busCatAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        venBusCat.setAdapter(busCatAdpt);
        venBusCat.setOnItemSelectedListener(this);



        imageActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        Uri imgUri = data.getData();
                        photoPath = imageLoaderHelper.getRealPathFromURI(imgUri, RegisterVendorActivity.this);
                        Glide.with(RegisterVendorActivity.this).load(imgUri).into(potImg);
                    }
                });

        imgPath.setOnClickListener(handleSelectPhoto);
        registerVen.setOnClickListener(handleRegVenForm);
    }

    private final View.OnClickListener handleSelectPhoto = v -> {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        imageActivityResultLauncher.launch(intent);
    };

    private final View.OnClickListener handleRegVenForm = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            fetchedIP = myPreference.getString("ipAddress", "n/a");
            BASE_URL = "http://" + fetchedIP + ":7000/";
            routeUrl = "parcel_backends/reg_temp_ven_mobile/";
            reqUrl = BASE_URL + routeUrl;

            first_name = venFirName.getText().toString(); last_name = venLasName.getText().toString();
            bus_country = venBusCon.getText().toString(); bus_state = venBusSta.getText().toString();
            nin = venNin.getText().toString(); bus_street = venBusStr.getText().toString();
            bus_category = venBusCat.getSelectedItem().toString(); cac_reg_no = venCacNo.getText().toString();
            phone_no = venPhone.getText().toString(); email = venEmail.getText().toString();
            password = venPassword.getText().toString(); retPass =venRetPass.getText().toString(); reg_date = "2022-08-16T15:44:343Z";
            ven_policy = venPolicy.isChecked(); is_email_verified = false;
            vend_photo = photoPath != null ? new File(photoPath) : null;

            String[] venInputs = {first_name, last_name, bus_country, bus_state, bus_street, nin, bus_category,
                                cac_reg_no, phone_no, email, password, retPass, reg_date};

            int inputErrCount = checkBlankInput(venInputs);


            if (fetchedIP != null && reqUrl != null && password.equals(retPass) && vend_photo != null && ven_policy && inputErrCount == 0) {

                new Thread(() -> {
                    try {
                        multipartUtility = new MultipartUtility(reqUrl, charSet);
                    } catch (IOException e) {
                        runOnUiThread(() -> Toast.makeText(RegisterVendorActivity.this,
                                "Error Connecting to the server, check your connection",
                                Toast.LENGTH_LONG).show());
                        e.printStackTrace();
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }

                    String[][] reqBodyString = {{"first_name", first_name}, {"last_name", last_name}, {"bus_country", bus_country},
                            {"bus_state", bus_state}, {"bus_street", bus_street}, {"bus_category", bus_category}, {"cac_reg_no", cac_reg_no},
                            {"phone_no", phone_no}, {"nin", nin}, {"email", email}, {"password", password}, {"reg_date", reg_date}};

                    for (String[] field : reqBodyString) {
                        if (field[0] != null && field[1] != null) {
                            try {
                                multipartUtility.addFormFields(field[0], field[1]);
                            } catch (IOException e) {
                                runOnUiThread(() -> Toast.makeText(RegisterVendorActivity.this,
                                        "Error Connecting to the server, check your connection",
                                        Toast.LENGTH_LONG).show());
                                e.printStackTrace();
                            } catch (NullPointerException npe) {
                                npe.printStackTrace();
                            }
                        }
                    }

                    try {
                        multipartUtility.addFormBooleanFields("ven_policy", ven_policy);
                    } catch (IOException e) {
                        runOnUiThread(() -> Toast.makeText(RegisterVendorActivity.this,
                                "Error Connecting to the server, check your connection",
                                Toast.LENGTH_LONG).show());
                        e.printStackTrace();
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }

                    try {
                        multipartUtility.addFormBooleanFields("is_email_verified", is_email_verified);
                    } catch (IOException e) {
                        runOnUiThread(() -> Toast.makeText(RegisterVendorActivity.this,
                                "Error Connecting to the server, check your connection",
                                Toast.LENGTH_LONG).show());
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }

                    try {
                        multipartUtility.addFileParts("vend_photo", vend_photo);
                    } catch (IOException e) {
                        runOnUiThread(() -> Toast.makeText(RegisterVendorActivity.this,
                                "Error Connecting to the server, check your connection",
                                Toast.LENGTH_LONG).show());
                        e.printStackTrace();
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }

                    try {
                        responseBody = multipartUtility.finish();
                    } catch (IOException e) {
                        runOnUiThread(() -> Toast.makeText(RegisterVendorActivity.this,
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
                                        new AlertDialog.Builder(RegisterVendorActivity.this)
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
                                        new AlertDialog.Builder(RegisterVendorActivity.this)
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
                        }
                    }
                }).start();
            } else {
                if (vend_photo == null) {
                    runOnUiThread(() -> {
                        if(!isFinishing()) {
                            new AlertDialog.Builder(RegisterVendorActivity.this)
                                    .setCancelable(true)
                                    .setTitle("Required Input!")
                                    .setMessage("Upload a photo, please")
                                    .setPositiveButton("Ok", null)
                                    .setNegativeButton("Cancel", null)
                                    .create()
                                    .show();
                        }
                    });
                } else if(inputErrCount == 0 && password.equals(retPass)) {
                    runOnUiThread(() -> {
                        if(!isFinishing()) {
                            new AlertDialog.Builder(RegisterVendorActivity.this)
                                    .setCancelable(true)
                                    .setTitle("Required Input!")
                                    .setMessage("Check the Vendor Policy button, please")
                                    .setPositiveButton("Ok", null)
                                    .setNegativeButton("Cancel", null)
                                    .create()
                                    .show();
                        }
                    });
                } else if (!password.equals(retPass)) {
                    runOnUiThread(() -> {
                        if(!isFinishing()) {
                            new AlertDialog.Builder(RegisterVendorActivity.this)
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
                            new AlertDialog.Builder(RegisterVendorActivity.this)
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
            }//
        }
    };

    private int checkBlankInput(String[] inputs) {
        int count = 0;
        for(String input : inputs) {
            if(input.equals("")) count++;
        }
        return  count;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        Toast.makeText(RegisterVendorActivity.this, item, Toast.LENGTH_LONG).show();
        bus_category = item;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
