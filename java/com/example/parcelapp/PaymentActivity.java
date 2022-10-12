package com.example.parcelapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.parcelapp.utils.JSONGetApiUtility;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.exceptions.AuthenticationException;
import co.paystack.android.exceptions.InvalidAmountException;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;

public class PaymentActivity extends AppCompatActivity {

    private final String PREF_FILE = BuildConfig.APPLICATION_ID.replace(".", "_");

    SharedPreferences myPreference;

    EditText mCardNumber;
    EditText mCardExpiry;
    EditText mCardCVV;
    Button mPayButton;
    TextView amountTag, shippingTag, totAmtTag;

    ConnectivityManager connectivityManager;
    NetworkReceiver networkReceiver;
    boolean isOnline;
    String email, shipping_method, ipAddress;
    int cAmount, order_id, shipping_fee;
    JSONGetApiUtility jsonGetApiUtility;
    List<String> resArr;
    String responseString;
    Intent intent;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.activity_payment);

        intent = getIntent();
        try{
            email = intent.getStringExtra("customerEmail");
            cAmount = intent.getIntExtra("chargeAmount", 0);
            shipping_method = intent.getStringExtra("shipping_method");
            order_id = intent.getIntExtra("order_id", 0);
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        amountTag = findViewById(R.id.amountTag);
        shippingTag = findViewById(R.id.shippingTag);
        totAmtTag = findViewById(R.id.totAmtTag);
        try {
            shipping_fee = shipping_method.equals("Delivery") ? 500 : 0;
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        amountTag.setText("Amount: " + "₦ " + cAmount);
        shippingTag.setText("Shipping fee: " + "₦ " + shipping_fee);
        totAmtTag.setText("Total Amount: " + "₦ " + (cAmount + shipping_fee));

        String toastMessage = "Customer E-Mail is " + email + " and Order Id is " + order_id;

        Toast.makeText(PaymentActivity.this, toastMessage, Toast.LENGTH_LONG).show();

        myPreference = this.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);

        try {
            initializePaystack();
            initializeFormVariables();
        } catch (NullPointerException e) {
            e.printStackTrace();
            runOnUiThread(() -> {
                if(!isFinishing()) {
                    new AlertDialog.Builder(PaymentActivity.this)
                            .setCancelable(true)
                            .setTitle("Error!")
                            .setMessage(e.getMessage())
                            .setPositiveButton("Ok", null)
                            .setNegativeButton("Cancel", null)
                            .create()
                            .show();
                }
            });
        }

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkReceiver = new NetworkReceiver(connectivityManager);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        isOnline = networkInfo != null && networkInfo.isConnected();
    }

    private void initializePaystack() {
        PaystackSdk.initialize(PaymentActivity.this);
        PaystackSdk.setPublicKey("sk_test_a07a45baef63500fbf6ab432fffee907e2300e98");
    }

    private void initializeFormVariables() {
        mCardNumber = findViewById(R.id.mCardNumber);
        mCardExpiry = findViewById(R.id.mCardExpiry);
        mCardCVV = findViewById(R.id.mCardCVV);
        mPayButton = findViewById(R.id.mPayButton);

        mCardExpiry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length() == 2 && !s.toString().contains("/")) {
                    s.append("/");
                }
            }
        });

        mCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length() == 4 && !s.toString().contains("-")) {
                    s.append("-");
                }

                if(s.toString().length() == 9) {
                    s.append("-");
                }

                if(s.toString().length() == 14) {
                    s.append("-");
                }
            }
        });


            mPayButton.setOnClickListener(v -> performCharge());
    }

    private void performCharge() {

        try {
            email = intent.getStringExtra("customerEmail");
            cAmount = intent.getIntExtra("chargeAmount", 0);
            shipping_method = intent.getStringExtra("shipping_method");
            order_id = intent.getIntExtra("order_id", 0);
            shipping_fee = shipping_method.equals("Delivery") ? 500 : 0;
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        String cardNumber = mCardNumber.getText().toString();
        String[] cardNumStrArr = cardNumber.split("-");
        String propCardNum = cardNumStrArr[0] + cardNumStrArr[1] + cardNumStrArr[2] + cardNumStrArr[3];
        String cardExpiry = mCardExpiry.getText().toString();
        String cvv = mCardCVV.getText().toString();

        String[] cardExpiryArray = cardExpiry.split("/");
        int expiryMonth = Integer.parseInt(cardExpiryArray[0]);
        int expiryYear = Integer.parseInt(cardExpiryArray[1]);
        int amount = cAmount + shipping_fee;
        amount *= 100;

        Card card = new Card(propCardNum, expiryMonth, expiryYear, cvv);

        if(card.isValid()) {
            Charge charge = new Charge();
            try {
                charge.setAmount(amount == 0 ? 10000 : amount);
                charge.setEmail(email != null ? email : "example@gmail.com");
                charge.setCard(card);
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            } catch (InvalidAmountException iae) {
                iae.printStackTrace();
                Toast.makeText(PaymentActivity.this, iae.getMessage(), Toast.LENGTH_LONG).show();
            }

            try {
                PaystackSdk.chargeCard(this, charge, new Paystack.TransactionCallback() {
                    @Override
                    public void onSuccess(Transaction transaction) {
                        handleOnSuccessCallback(transaction.getReference());
                    }

                    @Override
                    public void beforeValidate(Transaction transaction) {

                    }

                    @Override
                    public void onError(Throwable error, Transaction transaction) {
                        runOnUiThread(() -> {
                            if(!isFinishing()) {
                                new AlertDialog.Builder(PaymentActivity.this)
                                        .setCancelable(true)
                                        .setTitle("Login Error!")
                                        .setMessage(error.getMessage())
                                        .setPositiveButton("Ok", null)
                                        .setNegativeButton("Cancel", null)
                                        .create()
                                        .show();
                            }
                        });
                    }
                });
            } catch (AuthenticationException ae) {
                ae.printStackTrace();
                runOnUiThread(() -> {
                    if(!isFinishing()) {
                        new AlertDialog.Builder(PaymentActivity.this)
                                .setCancelable(true)
                                .setTitle("Error!")
                                .setMessage(ae.getMessage())
                                .setPositiveButton("Ok", null)
                                .setNegativeButton("Cancel", null)
                                .create()
                                .show();
                    }
                });
            }
        } else if (!card.validCVC()) {
            runOnUiThread(() -> {
                if(!isFinishing()) {
                    new AlertDialog.Builder(PaymentActivity.this)
                            .setCancelable(true)
                            .setTitle("Error!")
                            .setMessage("Invalid Cvv")
                            .setPositiveButton("Ok", null)
                            .setNegativeButton("Cancel", null)
                            .create()
                            .show();
                }
            });
        } else if(!card.validExpiryDate()) {
            runOnUiThread(() -> {
                if(!isFinishing()) {
                    new AlertDialog.Builder(PaymentActivity.this)
                            .setCancelable(true)
                            .setTitle("Error!")
                            .setMessage("Card had expired")
                            .setPositiveButton("Ok", null)
                            .setNegativeButton("Cancel", null)
                            .create()
                            .show();
                }
            });
        } else if(!card.validNumber()) {
            runOnUiThread(() -> {
                if(!isFinishing()) {
                    new AlertDialog.Builder(PaymentActivity.this)
                            .setCancelable(true)
                            .setTitle("Error!")
                            .setMessage("Invalid Card Number")
                            .setPositiveButton("Ok", null)
                            .setNegativeButton("Cancel", null)
                            .create()
                            .show();
                }
            });
        } else {
            runOnUiThread(() -> {
                if(!isFinishing()) {
                    new AlertDialog.Builder(PaymentActivity.this)
                            .setCancelable(true)
                            .setTitle("Error!")
                            .setMessage("Invalid card details supplied")
                            .setPositiveButton("Ok", null)
                            .setNegativeButton("Cancel", null)
                            .create()
                            .show();
                }
            });
        }
    }

    private void handleOnSuccessCallback(String trans_ref) {
        ipAddress= myPreference.getString("ipAddress", "n/a");
        String payVerUrl = "http://" + ipAddress + ":8080/v1/verifypayment/" + trans_ref;
        new Thread(() -> {
            try{
                jsonGetApiUtility = new JSONGetApiUtility(payVerUrl);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(PaymentActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }

            try {
                resArr = jsonGetApiUtility.finish();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }

            if(resArr != null) {
                for(String line : resArr) {
                    responseString = line;
                }
            }

            if(responseString != null) {
                if(responseString.equals("true")) {
                    runOnUiThread(() -> {
                        if(!isFinishing()) {
                            new AlertDialog.Builder(PaymentActivity.this)
                                    .setCancelable(true)
                                    .setTitle("Success!")
                                    .setMessage("Payment Verified Successfully...")
                                    .setPositiveButton("Ok", null)
                                    .setNegativeButton("Cancel", null)
                                    .create()
                                    .show();
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        if(!isFinishing()) {
                            new AlertDialog.Builder(PaymentActivity.this)
                                    .setCancelable(true)
                                    .setTitle("Error!")
                                    .setMessage("Payment verification failed...")
                                    .setPositiveButton("Ok", null)
                                    .setNegativeButton("Cancel", null)
                                    .create()
                                    .show();
                        }
                    });

                }
            } //End of request
        }).start();    }
}
