package com.example.parcelapp.customerhome;

import java.io.Serializable;

public class DeliverableProduct implements Serializable {
    private int order_id = 0;
    private int product_id = 0;
    private String product_name = "";
    private int quantity = 0;
    private boolean is_supply_ready = false;
    private boolean is_supply_received = false;
    private boolean is_delivered = false;
    private boolean is_received = false;
    private int prod_price = 0;
    private String prod_model = "";
    private String vendor_phone = "";
    private String vendor_name = "";
    private String vendor_email = "";
    private String prod_photo = "";
    private int total_amount = 0;
    private String vendor_address = "";
    private String created_at = "";
    private String updated_at = "";

    public DeliverableProduct() {

    }

    public DeliverableProduct(int order_id, int product_id, String product_name, int quantity, boolean is_supply_ready, boolean is_supply_received, boolean is_delivered, boolean is_received, int prod_price, String prod_model, String vendor_phone, String vendor_name, String vendor_email, String prod_photo, int total_amount, String vendor_address, String created_at, String updated_at) {
        this.order_id = order_id;
        this.product_id = product_id;
        this.product_name = product_name;
        this.quantity = quantity;
        this.is_supply_ready = is_supply_ready;
        this.is_supply_received = is_supply_received;
        this.is_delivered = is_delivered;
        this.is_received = is_received;
        this.prod_price = prod_price;
        this.prod_model = prod_model;
        this.vendor_phone = vendor_phone;
        this.vendor_name = vendor_name;
        this.vendor_email = vendor_email;
        this.prod_photo = prod_photo;
        this.total_amount = total_amount;
        this.vendor_address = vendor_address;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean getIs_supply_ready() {
        return is_supply_ready;
    }

    public void setIs_supply_ready(boolean is_supply_ready) {
        this.is_supply_ready = is_supply_ready;
    }

    public boolean getIs_supply_received() {
        return is_supply_received;
    }

    public void setIs_supply_received(boolean is_supply_received) {
        this.is_supply_received = is_supply_received;
    }

    public boolean getIs_delivered() {
        return is_delivered;
    }

    public void setIs_delivered(boolean is_delivered) {
        this.is_delivered = is_delivered;
    }

    public boolean getIs_received() {
        return is_received;
    }

    public void setIs_received(boolean is_received) {
        this.is_received = is_received;
    }

    public int getProd_price() {
        return prod_price;
    }

    public void setProd_price(int prod_price) {
        this.prod_price = prod_price;
    }

    public String getProd_model() {
        return prod_model;
    }

    public void setProd_model(String prod_model) {
        this.prod_model = prod_model;
    }

    public String getVendor_phone() {
        return vendor_phone;
    }

    public void setVendor_phone(String vendor_phone) {
        this.vendor_phone = vendor_phone;
    }

    public String getVendor_name() {
        return vendor_name;
    }

    public void setVendor_name(String vendor_name) {
        this.vendor_name = vendor_name;
    }

    public String getVendor_email() {
        return vendor_email;
    }

    public void setVendor_email(String vendor_email) {
        this.vendor_email = vendor_email;
    }

    public String getProd_photo() {
        return prod_photo;
    }

    public void setProd_photo(String prod_photo) {
        this.prod_photo = prod_photo;
    }

    public int getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(int total_amount) {
        this.total_amount = total_amount;
    }

    public String getVendor_address() {
        return vendor_address;
    }

    public void setVendor_address(String vendor_address) {
        this.vendor_address = vendor_address;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
