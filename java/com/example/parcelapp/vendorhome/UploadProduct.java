package com.example.parcelapp.vendorhome;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.File;

public interface UploadProduct {
    void uploadProduct(@NonNull String productName, @NonNull String productModel, @NonNull int productPrice, @NonNull int productDiscount,
                       @NonNull int productQuantity, @NonNull String productDescription, @NonNull JSONObject vendorObj, @NonNull File prod_photo);
}
