package com.example.parcelapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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

public class RegisterCourierActivity extends AppCompatActivity {

    private final String PREF_FILE = BuildConfig.APPLICATION_ID.replace(".", "_");

    ActivityResultLauncher<Intent> imageActivityResultLauncher;

    ImageLoaderHelper imageLoaderHelper = new ImageLoaderHelper();

    MultipartUtility multipartUtility;


    ImageView potImgCour;
    TextView imgPathCour, courPolicyLink;
    Button registerCour;

    EditText courFirName, courLasName, courBusCon, courBusSta, courBusStr, courNin,
            courPhone, courEmail, courPassword, courRetPass, courCacNo;

    CheckBox courPolicy;

    SharedPreferences myPreference;
    String fetchedIP, BASE_URL, routeUrl, reqUrl;
    String charSet = "UTF-8";

    String first_name, last_name, bus_country, bus_state,
            bus_street, cac_reg_no, nin,
            phone_no, email, password, retPass, reg_date;

    File cour_photo ;
    boolean cour_policy, is_email_verified;

    String photoPath;

    List<String> responseBody;

    String responseString;

    JSONObject jsonRes;

    String status, data;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.activity_register_courier);

        myPreference = this.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);

        potImgCour = findViewById(R.id.potImgCour);
        imgPathCour = findViewById(R.id.imgPathCour);
        registerCour = findViewById(R.id.registerCour);
        courFirName = findViewById(R.id.courFirName);
        courLasName = findViewById(R.id.courLasName);
        courBusCon = findViewById(R.id.courBusCon);
        courBusSta = findViewById(R.id.courBusSta);
        courBusStr = findViewById(R.id.courBusStr);
        courCacNo = findViewById(R.id.courCacNo);
        courNin = findViewById(R.id.courNin);
        courPhone = findViewById(R.id.courPhone);
        courEmail = findViewById(R.id.courEmail);
        courPassword = findViewById(R.id.courPassword);
        courRetPass = findViewById(R.id.courRetPass);
        courPolicy = findViewById(R.id.courPolicy);
        courPolicyLink = findViewById(R.id.courPolicyLink);

        imageActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        Uri imgUri = data.getData();
                        photoPath = imageLoaderHelper.getRealPathFromURI(imgUri, RegisterCourierActivity.this);
                        Glide.with(RegisterCourierActivity.this).load(imgUri).into(potImgCour);
                    }
                });

        imgPathCour.setOnClickListener(handleSelectPhoto);
        registerCour.setOnClickListener(handleRegCourForm);
    }

    private final View.OnClickListener handleSelectPhoto = v -> {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        imageActivityResultLauncher.launch(intent);
    };

    private int checkBlankInput(String[] inputs) {
        int count = 0;
        for(String input : inputs) {
            if(input.equals("")) count++;
        }
        return  count;
    }

    private final View.OnClickListener handleRegCourForm = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            fetchedIP = myPreference.getString("ipAddress", "n/a");
            BASE_URL = "http://" + fetchedIP + ":7000/";
            routeUrl = "parcel_backends/reg_temp_cour_mobile/";
            reqUrl = BASE_URL + routeUrl;

            first_name = courFirName.getText().toString(); last_name = courLasName.getText().toString();
            bus_country = courBusCon.getText().toString(); bus_state = courBusSta.getText().toString();
            nin = courNin.getText().toString(); bus_street = courBusStr.getText().toString();
            cac_reg_no = courCacNo.getText().toString();
            phone_no = courPhone.getText().toString(); email = courEmail.getText().toString();
            password = courPassword.getText().toString(); retPass =courRetPass.getText().toString(); reg_date = "2022-08-16T15:44:343Z";
            cour_policy = courPolicy.isChecked(); is_email_verified = false;
            cour_photo = photoPath != null ? new File(photoPath) : null;

            String[] courInputs = {first_name, last_name, bus_country, bus_state, bus_street, nin,
                    cac_reg_no, phone_no, email, password, retPass, reg_date};

            int inputErrCount = checkBlankInput(courInputs);


            if (fetchedIP != null && reqUrl != null && password.equals(retPass) && cour_photo != null && inputErrCount == 0 && cour_policy) {

                new Thread(() -> {
                    try {
                        multipartUtility = new MultipartUtility(reqUrl, charSet);
                    } catch (IOException e) {
                        runOnUiThread(() -> Toast.makeText(RegisterCourierActivity.this,
                                "Error Connecting to the server, check your connection",
                                Toast.LENGTH_LONG).show());
                        e.printStackTrace();
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }

                    String[][] reqBodyString = {{"first_name", first_name}, {"last_name", last_name}, {"bus_country", bus_country},
                            {"bus_state", bus_state}, {"bus_street", bus_street}, {"cac_reg_no", cac_reg_no},
                            {"phone_no", phone_no}, {"nin", nin}, {"email", email}, {"password", password}, {"reg_date", reg_date}};

                    for (String[] field : reqBodyString) {
                        if (field[0] != null && field[1] != null) {
                            try {
                                multipartUtility.addFormFields(field[0], field[1]);
                            } catch (IOException e) {
                                runOnUiThread(() -> Toast.makeText(RegisterCourierActivity.this,
                                        "Error Connecting to the server, check your connection",
                                        Toast.LENGTH_LONG).show());
                                e.printStackTrace();
                            } catch(NullPointerException npe) {
                                npe.printStackTrace();
                            }
                        }
                    }

                    try {
                        multipartUtility.addFormBooleanFields("cour_policy", cour_policy);
                    } catch (IOException e) {
                        runOnUiThread(() -> Toast.makeText(RegisterCourierActivity.this,
                                "Error Connecting to the server, check your connection",
                                Toast.LENGTH_LONG).show());
                        e.printStackTrace();
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }

                    try {
                        multipartUtility.addFormBooleanFields("is_email_verified", is_email_verified);
                    } catch (IOException e) {
                        runOnUiThread(() -> Toast.makeText(RegisterCourierActivity.this,
                                "Error Connecting to the server, check your connection",
                                Toast.LENGTH_LONG).show());
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }

                    try {
                        multipartUtility.addFileParts("cour_photo", cour_photo);
                    } catch (IOException e) {
                        runOnUiThread(() -> Toast.makeText(RegisterCourierActivity.this,
                                "Error Connecting to the server, check your connection",
                                Toast.LENGTH_LONG).show());
                        e.printStackTrace();
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }

                    try {
                        responseBody = multipartUtility.finish();
                    } catch (IOException e) {
                        runOnUiThread(() -> Toast.makeText(RegisterCourierActivity.this,
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
                                        new AlertDialog.Builder(RegisterCourierActivity.this)
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
                                        new AlertDialog.Builder(RegisterCourierActivity.this)
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
                if (cour_photo == null) {
                    runOnUiThread(() -> {
                        if(!isFinishing()) {
                            new AlertDialog.Builder(RegisterCourierActivity.this)
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
                            new AlertDialog.Builder(RegisterCourierActivity.this)
                                    .setCancelable(true)
                                    .setTitle("Required Input!")
                                    .setMessage("Check the Courier Policy button, please")
                                    .setPositiveButton("Ok", null)
                                    .setNegativeButton("Cancel", null)
                                    .create()
                                    .show();
                        }
                    });
                } else if (!password.equals(retPass)) {
                    runOnUiThread(() -> {
                        if(!isFinishing()) {
                            new AlertDialog.Builder(RegisterCourierActivity.this)
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
                            new AlertDialog.Builder(RegisterCourierActivity.this)
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
}
