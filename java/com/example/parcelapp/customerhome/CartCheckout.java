package com.example.parcelapp.customerhome;

import androidx.annotation.NonNull;

public interface CartCheckout {
    void checkOut(@NonNull String shipping_method, @NonNull String zip_code);
}
