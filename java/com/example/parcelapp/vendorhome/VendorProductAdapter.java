package com.example.parcelapp.vendorhome;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.parcelapp.R;
import com.example.parcelapp.customerhome.Product;
import com.example.parcelapp.customerhome.ProductAdapter;

import java.util.List;

public class VendorProductAdapter extends ArrayAdapter<Product> {

    Context ctx;
    List<Product> products;
    String ipAddress;
    SharedPreferences myPreference;
    ManageProduct manageProduct;

    VendorProductAdapter (Context context, List<Product> prods, String ipAddress, SharedPreferences myPref, ManageProduct manageProduct) {
        super(context, R.layout.vendor_product_row, prods);
        this.ctx = context;
        this.products = prods;
        this.ipAddress = ipAddress;
        this.myPreference = myPref;
        this.manageProduct = manageProduct;
    }

    static class VendorProductHolder {

        ImageView uploadedProdImg;
        TextView uploadedProdName, uploadedProdModel;
        EditText uploadedProdPrice, uploadedProdDiscount, uploadedProdQuantity;
        Button updateProdBtn, deleteProdBtn;

        Context ctx;
        String ipAddress;
        SharedPreferences myPreference;
        ManageProduct manageProduct;

        VendorProductHolder(View row, Context context, String ipAddress, SharedPreferences myPref, ManageProduct manageProduct) {

            uploadedProdImg = row.findViewById(R.id.uploadedProdImg);
            uploadedProdName = row.findViewById(R.id.uploadedProdName);
            uploadedProdModel = row.findViewById(R.id.uploadedProdModel);
            uploadedProdPrice = row.findViewById(R.id.uploadedProdPrice);
            uploadedProdDiscount = row.findViewById(R.id.uploadedProdDiscount);
            uploadedProdQuantity = row.findViewById(R.id.uploadedProdQuantity);
            updateProdBtn = row.findViewById(R.id.updateProdBtn);
            deleteProdBtn = row.findViewById(R.id.deleteProdBtn);

            this.ctx = context;
            this.ipAddress = ipAddress;
            this.myPreference = myPref;
            this.manageProduct = manageProduct;
        }

        @SuppressLint("SetTextI18n")
        void populateFrom (Product product) {

            String photoUrl = product.getProd_photo().replace("localhost", ipAddress);
            Glide.with(ctx).load(photoUrl).into(uploadedProdImg);

            uploadedProdName.setText("Name: " + product.getProd_name());
            uploadedProdModel.setText("Model: " + product.getProd_model());
            uploadedProdPrice.setText("Price: " + "₦ " + product.getProd_price());
            uploadedProdDiscount.setText("Discount: " + product.getProd_disc() + " %");
            uploadedProdQuantity.setText("Quantity: " + product.getProd_qty());




            updateProdBtn.setOnClickListener(v -> {
                String prod_price = uploadedProdPrice.getText().toString();
                String prod_disc = uploadedProdDiscount.getText().toString();
                String prod_qty = uploadedProdQuantity.getText().toString();

                if(!prod_price.equals("") && !prod_disc.equals("") && !prod_qty.equals("")) {
                    manageProduct.updateProduct(prod_price, prod_disc, prod_qty, product.getProduct_id());
                } else {
                    Toast.makeText(ctx, "Some field are blank", Toast.LENGTH_LONG).show();
                }

            });

            deleteProdBtn.setOnClickListener(v -> manageProduct.deleteProduct(product));
        }
    }

    public View getView (int position, View convertView, ViewGroup parent) {
        View row = convertView;
        VendorProductHolder holder;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = inflater.inflate(R.layout.vendor_product_row, parent, false);
            holder = new VendorProductHolder(row, ctx, ipAddress, myPreference, manageProduct);
            row.setTag(holder);
        } else {
            holder = (VendorProductHolder) row.getTag();
        }
        holder.populateFrom(products.get(position));
        return (row);
    }
}
