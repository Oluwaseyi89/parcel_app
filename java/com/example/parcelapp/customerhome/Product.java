package com.example.parcelapp.customerhome;

import java.io.Serializable;

public class Product implements Serializable {
    private int product_id = 0;
    private String vendor_name = "";
    private String vend_photo = "";
    private String prod_cat = "";
    private String prod_name = "";
    private String prod_model = "";
    private String prod_photo = "";
    private String prod_desc = "";
    private int prod_price = 0;
    private int prod_disc = 0;
    private int prod_qty = 0;
    private int addedQty = 0;


    public Product(int product_id, String vendor_name, String vend_photo, String prod_cat,
                   String prod_name, String prod_model, String prod_photo, String prod_desc,
                   int prod_price, int prod_disc, int prod_qty){
        this.product_id = product_id;
        this.vendor_name = vendor_name;
        this.vend_photo = vend_photo;
        this.prod_cat = prod_cat;
        this.prod_name = prod_name;
        this.prod_model = prod_model;
        this.prod_photo = prod_photo;
        this.prod_desc = prod_desc;
        this.prod_price = prod_price;
        this.prod_disc = prod_disc;
        this.prod_qty = prod_qty;
    }

    public Product() {

    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getVendor_name() {
        return vendor_name;
    }

    public void setVendor_name(String vendor_name) {
        this.vendor_name = vendor_name;
    }

    public String getVend_photo() {
        return vend_photo;
    }

    public void setVend_photo(String vend_photo) {
        this.vend_photo = vend_photo;
    }

    public String getProd_cat() {
        return prod_cat;
    }

    public void setProd_cat(String prod_cat) {
        this.prod_cat = prod_cat;
    }

    public String getProd_name() {
        return prod_name;
    }

    public void setProd_name(String prod_name) {
        this.prod_name = prod_name;
    }

    public String getProd_model() {
        return prod_model;
    }

    public void setProd_model(String prod_model) {
        this.prod_model = prod_model;
    }

    public String getProd_photo() {
        return prod_photo;
    }

    public void setProd_photo(String prod_photo) {
        this.prod_photo = prod_photo;
    }

    public String getProd_desc() {
        return prod_desc;
    }

    public void setProd_desc(String prod_desc) {
        this.prod_desc = prod_desc;
    }

    public int getProd_price() {
        return prod_price;
    }

    public void setProd_price(int prod_price) {
        this.prod_price = prod_price;
    }

    public int getProd_disc() {
        return prod_disc;
    }

    public void setProd_disc(int prod_disc) {
        this.prod_disc = prod_disc;
    }

    public int getProd_qty() {
        return prod_qty;
    }

    public void setProd_qty(int prod_qty) {
        this.prod_qty = prod_qty;
    }

    public int getAddedQty() {
        return addedQty;
    }

    public void setAddedQty(int addedQty) {
        this.addedQty = addedQty;
    }
}
