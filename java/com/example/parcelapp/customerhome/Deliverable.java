package com.example.parcelapp.customerhome;

import java.io.Serializable;
import java.util.List;

public class Deliverable implements Serializable {
    private int order_id = 0;
    private int customer_id = 0;
    private String customer_name = "";
    private int total_items = 0;
    private int total_price = 0;
    private boolean is_customer = false;
    private String zip_code = "";
    private String shipping_method = "";
    private boolean is_delivered = false;
    private boolean is_received = false;
    private boolean handled_dispatch = false;
    private int courier_id = 0;
    private String courier_email = "";
    private String courier_phone = "";
    private String courier_name = "";
    private String address = "";
    private String phone_no = "";
    private String email = "";
    private String created_at = "";
    private String updated_at = "";
    private List<DeliverableProduct> products;

    public Deliverable () {

    }

    public Deliverable(int order_id, int customer_id, String customer_name, int total_items, int total_price, boolean is_customer, String zip_code, String shipping_method, boolean is_delivered, boolean is_received, boolean handled_dispatch, int courier_id, String courier_email, String courier_phone, String courier_name, String address, String phone_no, String email, String created_at, String updated_at, List<DeliverableProduct> products) {
        this.order_id = order_id;
        this.customer_id = customer_id;
        this.customer_name = customer_name;
        this.total_items = total_items;
        this.total_price = total_price;
        this.is_customer = is_customer;
        this.zip_code = zip_code;
        this.shipping_method = shipping_method;
        this.is_delivered = is_delivered;
        this.is_received = is_received;
        this.handled_dispatch = handled_dispatch;
        this.courier_id = courier_id;
        this.courier_email = courier_email;
        this.courier_phone = courier_phone;
        this.courier_name = courier_name;
        this.address = address;
        this.phone_no = phone_no;
        this.email = email;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.products = products;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public int getTotal_items() {
        return total_items;
    }

    public void setTotal_items(int total_items) {
        this.total_items = total_items;
    }

    public int getTotal_price() {
        return total_price;
    }

    public void setTotal_price(int total_price) {
        this.total_price = total_price;
    }

    public boolean getIs_customer() {
        return is_customer;
    }

    public void setIs_customer(boolean is_customer) {
        this.is_customer = is_customer;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }

    public String getShipping_method() {
        return shipping_method;
    }

    public void setShipping_method(String shipping_method) {
        this.shipping_method = shipping_method;
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

    public boolean getHandled_dispatch() {
        return handled_dispatch;
    }

    public void setHandled_dispatch(boolean handled_dispatch) {
        this.handled_dispatch = handled_dispatch;
    }

    public int getCourier_id() {
        return courier_id;
    }

    public void setCourier_id(int courier_id) {
        this.courier_id = courier_id;
    }

    public String getCourier_email() {
        return courier_email;
    }

    public void setCourier_email(String courier_email) {
        this.courier_email = courier_email;
    }

    public String getCourier_phone() {
        return courier_phone;
    }

    public void setCourier_phone(String courier_phone) {
        this.courier_phone = courier_phone;
    }

    public String getCourier_name() {
        return courier_name;
    }

    public void setCourier_name(String courier_name) {
        this.courier_name = courier_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public List<DeliverableProduct> getProducts() {
        return products;
    }

    public void setProducts(List<DeliverableProduct> products) {
        this.products = products;
    }
}
