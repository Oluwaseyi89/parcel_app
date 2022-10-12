package com.example.parcelapp.vendorhome;

import com.example.parcelapp.customerhome.Product;

public interface ManageProduct {
    void updateProduct(String price, String discount, String quantity, int product_id);
    void deleteProduct(Product product);
}
