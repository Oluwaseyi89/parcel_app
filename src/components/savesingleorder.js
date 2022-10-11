import { UseFetchJSON } from "./useFetch";
export const savesingleorder = (first_name, last_name, country, state, shipping_method,
    zip_code, street, phone_no, email, reg_date, logcus, cartTot, 
    totPrice, buySingle, customer_name, setProdSus, setProdErr, history) => {
    // e.preventDefault();
    // if (first_name && last_name && country && state &&
    //      shipping_method && zip_code &&
    //     street && phone_no && email) {
    if (logcus) {
        //block for registerd customers
        // console.log(logcus.first_name + " is online");
        if (shipping_method && zip_code) {
            let cust_name = logcus.last_name + " " + logcus.first_name;
            let id = logcus.id
            let order_detail = {
                "customer_id": id,
                "customer_name": cust_name,
                "total_items": cartTot,
                "total_price": totPrice,
                "shipping_method": shipping_method,
                "zip_code": zip_code,                
                "is_customer": true,
                "is_completed": false,
                "created_at": new Date().toISOString(),
                "updated_at": new Date().toISOString() 
            }
            let orderUrl = `http://localhost:7000/parcel_order/order_save/${cust_name}/`;
                            let saveOrder = UseFetchJSON(orderUrl, 'POST', order_detail);
                            saveOrder.then((res) => {
                                if (res.status === "success") {
                                    let orderId = res.data;
                                    // setSessionId(0);
                                    // setSessionId(res.data);
                                    if(orderId) {

                                        localStorage.setItem('curOrder', orderId);
                                        // console.log(session_id);
                                        
                                            let product_name = buySingle.prod_name;
                                            let product_id = buySingle.id;
                                            let quantity = buySingle.purchased_qty
                                            let order_item = {
                                                "order_id": orderId,
                                                "product_id": product_id,
                                                "product_name": product_name,
                                                "quantity": quantity,
                                                "is_customer": true,
                                                "is_completed": false,
                                                "created_at": new Date().toISOString(),
                                                "updated_at": new Date().toISOString()
                                            }//originally save
                                            let orderItemUrl = `http://localhost:7000/parcel_order/order_item_update/${orderId}/${product_id}/`;
                                            let saveOrderItem = UseFetchJSON(orderItemUrl, 'PATCH', order_item);
                                            saveOrderItem.then((res) => {
                                                if (res.status === "success") {
                                                    setProdSus(res.data);
                                                } else if (res.status === "error") {
                                                    setProdErr(res.data);
                                                } else if (res.status === "invalid") {
                                                    setProdErr(res.data);
                                                } else {
                                                    setProdErr("An error occured.");
                                                }
                                            }).catch((err) => console.log(err));
                                        

                                        let payment_detail = {
                                            "order_id": orderId,
                                            "customer_id": id,
                                            "customer_name": cust_name,
                                            "is_customer": true,
                                            "amount": totPrice,
                                            "created_at": new Date().toISOString(),
                                            "updated_at": new Date().toISOString()
                                        }

                                        let payUrl = `http://localhost:7000/parcel_order/payment_save/${orderId}/`;

                                        let savePayment = UseFetchJSON(payUrl, "POST", payment_detail);
                                        savePayment.then((res) => {
                                            if(res.status === "success") {
                                                setProdSus(res.data);
                                                history.push('/payment');
                                            } 
                                        }).catch((err) => console.log(err));

                                    } //stop buySingle
                                } else if (res.status === "error") {
                                    // console.log(res.data);
                                    let order_detail = {
                                        "customer_id": id,
                                        "customer_name": cust_name,
                                        "total_items": cartTot,
                                        "total_price": totPrice,
                                        "shipping_method": shipping_method,
                                        "zip_code": zip_code,                                
                                        "is_customer": true,
                                        "is_completed": false,
                                        "updated_at": new Date().toISOString() 
                                    }

                                    let orderUrl = `http://localhost:7000/parcel_order/order_update/${cust_name}/`;
                                    let updatedOrder = UseFetchJSON(orderUrl, 'PATCH', order_detail);
                                    updatedOrder.then((res) => {
                                        if (res.status === "success") {
                                            let ordId = res.data;
                                            console.log(ordId);
                                            // setSessionId(0);
                                            // setSessionId(res.data);
                                            if(ordId) {
                                                localStorage.setItem('curOrder', ordId);
                                                // console.log(session_id);
                                                
                                                    let product_name = buySingle.prod_name;
                                                    let product_id = buySingle.id;
                                                    let quantity = buySingle.purchased_qty
                                                    let order_item = {
                                                        "order_id": ordId,
                                                        "product_id": product_id,
                                                        "product_name": product_name,
                                                        "quantity": quantity,
                                                        "is_customer": true,
                                                        "is_completed": false,
                                                        "updated_at": new Date().toISOString()
                                                    }
                                                    let ordItemUrl = `http://localhost:7000/parcel_order/order_item_update/${ordId}/${product_id}/`;
                                                    let saveOrderItem = UseFetchJSON(ordItemUrl, 'PATCH', order_item);
                                                    saveOrderItem.then((res) => {
                                                        if (res.status === "success") {
                                                            setProdSus(res.data);
                                                        } else if (res.status === "expired") {
                                                            setProdErr(res.data);
                                                        } else if (res.status === "invalid") {
                                                            setProdErr(res.data);
                                                        } else if (res.status === "non-exist") {
                                                            setProdErr(res.data);
                                                        } else {
                                                            setProdErr("An error occured.");
                                                        }
                                                    }).catch((err) => console.log(err));
                                                

                                                let payment_detail = {
                                                    "order_id": ordId,
                                                    "customer_id": id,
                                                    "customer_name": cust_name,
                                                    "is_customer": true,
                                                    "amount": totPrice,                                                    
                                                    "updated_at": new Date().toISOString()
                                                }
        
                                                let payUrl = `http://localhost:7000/parcel_order/payment_update/${ordId}/`;
        
                                                let savePayment = UseFetchJSON(payUrl, "PATCH", payment_detail);
                                                savePayment.then((res) => {
                                                    if(res.status === "success") {
                                                        setProdSus(res.data);
                                                        history.push('/payment');
                                                    } else if (res.status === "error") {
                                                        setProdErr(res.data);
                                                    } else {
                                                        setProdErr("An error occured.");
                                                    }
                                                }).catch((err) => console.log(err));                                            
                                            } //stop buySingle
                                        } else if (res.status === "error") {
                                            setProdErr(res.data);
                                        } else if (res.status === "invalid") {
                                            setProdErr(res.data);
                                        } else {
                                            setProdErr("An error occured.");
                                        }
                                    });
                                } else if (res.status === "invalid") {
                                    setProdErr(res.data);
                                } else {
                                    setProdErr("An error occured.");
                                }
                            });
        } else {
            setProdErr("Some fields are blank");
        }

    } else {
        //block for anonymous customers
        if (first_name && last_name && country && state &&
                 shipping_method && zip_code &&
                street && phone_no && email) {
                let anonymous_customer = {
                    "first_name": first_name,
                    "last_name": last_name,
                    "country": country,
                    "state": state,
                    "street": street,
                    "zip_code": zip_code,
                    "email": email,
                    "phone_no": phone_no,
                    "reg_date": reg_date
                }

                let anonUrl = "http://localhost:7000/parcel_customer/anonymous_save/";
                let saveAnony = UseFetchJSON(anonUrl, 'POST', anonymous_customer);
                saveAnony.then((res)=>{
                    if(res.status === "success") {
                        // New Anonymous 
                        let anaCusId = res.data;
                        // setCustomerId(0);
                        // setCustomerId(res.data);
                        if(anaCusId) {
                            // console.log(customer_id);
                            let order_detail = {
                                "customer_id": anaCusId,
                                "customer_name": customer_name,
                                "total_items": cartTot,
                                "total_price": totPrice,
                                "shipping_method": shipping_method,
                                "zip_code": zip_code,
                                "is_customer": false,
                                "is_completed": false,
                                "created_at": new Date().toISOString(),
                                "updated_at": new Date().toISOString() 
                            }
                            let orderUrl = `http://localhost:7000/parcel_order/order_save/${customer_name}/`;
                            let saveOrder = UseFetchJSON(orderUrl, 'POST', order_detail);
                            saveOrder.then((res) => {
                                if (res.status === "success") {
                                    let anaOrdId = res.data;
                                    // setSessionId(0);
                                    // setSessionId(res.data);
                                    if(anaOrdId) {
                                        // console.log(session_id);
                                        localStorage.setItem('curOrder', anaOrdId);
                                        
                                            let product_name = buySingle.prod_name;
                                            let product_id = buySingle.id;
                                            let quantity = buySingle.purchased_qty
                                            let order_item = {
                                                "order_id": anaOrdId,
                                                "product_id": product_id,
                                                "product_name": product_name,
                                                "quantity": quantity,
                                                "is_customer": false,
                                                "is_completed": false,
                                                "created_at": new Date().toISOString(),
                                                "updated_at": new Date().toISOString()
                                            }//originally save
                                            let orderItemUrl = `http://localhost:7000/parcel_order/order_item_update/${anaOrdId}/${product_id}/`;
                                            let saveOrderItem = UseFetchJSON(orderItemUrl, 'PATCH', order_item);
                                            saveOrderItem.then((res) => {
                                                if (res.status === "success") {
                                                    setProdSus(res.data);
                                                } else if (res.status === "error") {
                                                    setProdErr(res.data);
                                                } else if (res.status === "invalid") {
                                                    setProdErr(res.data);
                                                } else {
                                                    setProdErr("An error occured.");
                                                }
                                            }).catch((err) => console.log(err));
                                    

                                        let payment_detail = {
                                            "order_id": anaOrdId,
                                            "customer_id": anaCusId,
                                            "customer_name": customer_name,
                                            "is_customer": false,
                                            "amount": totPrice,
                                            "created_at": new Date().toISOString(),
                                            "updated_at": new Date().toISOString()
                                        }

                                        let payUrl = `http://localhost:7000/parcel_order/payment_save/${anaOrdId}/`;

                                        let savePayment = UseFetchJSON(payUrl, "POST", payment_detail);
                                        savePayment.then((res) => {
                                            if(res.status === "success") console.log(res.data);
                                            history.push('/payment');
                                        }).catch((err) => console.log(err));
                                    
                                    } // stop buySingle
                                } else if (res.status === "error") {
                                    setProdErr(res.data);
                                } else if (res.status === "invalid") {
                                    setProdErr(res.data);
                                } else {
                                    setProdErr("An error occured.");
                                }
                            }); //stop
                        }
                        // stop
                    } else if(res.status === "error") {
                        // Existing Anonymous Customer
                        // setCustomerId(0);
                        // setCustomerId(res.data);
                        let alrCusId = res.data;
                        if(alrCusId) {
                            // console.log(customer_id);
                            let order_detail = {
                                "customer_id": alrCusId,
                                "customer_name": customer_name,
                                "total_items": cartTot,
                                "total_price": totPrice,
                                "shipping_method": shipping_method,
                                "zip_code": zip_code,                                
                                "is_customer": false,
                                "is_completed": false,
                                "updated_at": new Date().toISOString() 
                            }
                            let ordUrl = `http://localhost:7000/parcel_order/order_update/${customer_name}/`;
                            let updateOrder = UseFetchJSON(ordUrl, 'PATCH', order_detail);
                            updateOrder.then((res) => {
                                if (res.status === "success") {
                                    let ordId = res.data;
                                    console.log(ordId);
                                    // setSessionId(0);
                                    // setSessionId(res.data);
                                    if(ordId) {
                                        // console.log(session_id);
                                        localStorage.setItem('curOrder', ordId);
                                        
                                            let product_name = buySingle.prod_name;
                                            let product_id = buySingle.id;
                                            let quantity = buySingle.purchased_qty
                                            let order_item = {
                                                "order_id": ordId,
                                                "product_id": product_id,
                                                "product_name": product_name,
                                                "quantity": quantity,
                                                "is_customer": false,
                                                "is_completed": false,
                                                "updated_at": new Date().toISOString()
                                            }
                                            let orderItemUrl = `http://localhost:7000/parcel_order/order_item_update/${ordId}/${product_id}/`;
                                            let orderItemUpdate = UseFetchJSON(orderItemUrl, 'PATCH', order_item);
                                            orderItemUpdate.then((res) => {
                                                if (res.status === "success") {
                                                    setProdSus(res.data);
                                                } else if (res.status === "expired") {
                                                    setProdErr(res.data);
                                                } else if (res.status === "invalid") {
                                                    setProdErr(res.data);
                                                } else if (res.status === "non-exist") {
                                                    setProdErr(res.data);
                                                } else {
                                                    setProdErr("An error occured.");
                                                }
                                            }).catch((err) => console.log(err));
                                        

                                        let payment_detail = {
                                            "order_id": ordId,
                                            "customer_id": alrCusId,
                                            "customer_name": customer_name,
                                            "is_customer": false,
                                            "amount": totPrice,                                                    
                                            "updated_at": new Date().toISOString()
                                        }

                                        let payUrl = `http://localhost:7000/parcel_order/payment_save/${ordId}/`;

                                        let savePayment = UseFetchJSON(payUrl, "PATCH", payment_detail);
                                        savePayment.then((res) => {
                                            if(res.status === "success") {
                                                setProdSus(res.data);
                                                history.push('/payment');
                                            } else if (res.status === "error") {
                                                setProdErr(res.data);
                                            } else {
                                                setProdErr("An error occured");
                                            }
                                        }).catch((err) => console.log(err));
                                    } // stop buySingle
                                } else if (res.status === "error") {
                                    setProdErr(res.data);
                                } else if (res.status === "non-exist") {
                                    // cart customer here
                                    // if(anaCusId) {
                                    //     // console.log(customer_id);
                                        let order_detail = {
                                            "customer_id": alrCusId,
                                            "customer_name": customer_name,
                                            "total_items": cartTot,
                                            "total_price": totPrice,
                                            "shipping_method": shipping_method,
                                            "zip_code": zip_code,
                                            "is_customer": false,
                                            "is_completed": false,
                                            "created_at": new Date().toISOString(),
                                            "updated_at": new Date().toISOString() 
                                        }
                                        let orderUrl = `http://localhost:7000/parcel_order/order_save/${customer_name}/`;
                                        let saveOrder = UseFetchJSON(orderUrl, 'POST', order_detail);
                                        saveOrder.then((res) => {
                                            if (res.status === "success") {
                                                let anaOrdId = res.data;
                                                // setSessionId(0);
                                                // setSessionId(res.data);
                                                if(anaOrdId) {
                                                    // console.log(session_id);
                                                    localStorage.setItem('curOrder', anaOrdId);
                                                    
                                                        let product_name = buySingle.prod_name;
                                                        let product_id = buySingle.id;
                                                        let quantity = buySingle.purchased_qty
                                                        let order_item = {
                                                            "order_id": anaOrdId,
                                                            "product_id": product_id,
                                                            "product_name": product_name,
                                                            "quantity": quantity,
                                                            "is_customer": false,
                                                            "is_completed": false,
                                                            "created_at": new Date().toISOString(),
                                                            "updated_at": new Date().toISOString()
                                                        }// originally save
                                                        let orderItemUrl = `http://localhost:7000/parcel_order/order_item_update/${anaOrdId}/${product_id}/`;
                                                        let saveOrderItem = UseFetchJSON(orderItemUrl, 'PATCH', order_item);
                                                        saveOrderItem.then((res) => {
                                                            if (res.status === "success") {
                                                                setProdSus(res.data);
                                                            } else if (res.status === "error") {
                                                                setProdErr(res.data);
                                                            } else if (res.status === "invalid") {
                                                                setProdErr(res.data);
                                                            } else {
                                                                setProdErr("An error occured.");
                                                            }
                                                        }).catch((err) => console.log(err));
                                                    
            
                                                    let payment_detail = {
                                                        "order_id": anaOrdId,
                                                        "customer_id": alrCusId,
                                                        "customer_name": customer_name,
                                                        "is_customer": false,
                                                        "amount": totPrice,
                                                        "created_at": new Date().toISOString(),
                                                        "updated_at": new Date().toISOString()
                                                    }
            
                                                    let payUrl = `http://localhost:7000/parcel_order/payment_save/${anaOrdId}/`;
            
                                                    let savePayment = UseFetchJSON(payUrl, "POST", payment_detail);
                                                    savePayment.then((res) => {
                                                        if(res.status === "success") setProdSus(res.data);
                                                        history.push('/payment');
                                                    }).catch((err) => console.log(err));
                                                } //stop buySingle
                                            }
                                        });
                                                
                                                
                                                
            

                                } else if (res.status === "invalid") {
                                    setProdErr(res.data);
                                } else {
                                    setProdErr("An error occured.");
                                }
                            });
                        } //stop
                    } else { 
                        setProdErr('Please enter valid data');
                    }
                }).catch((err) => console.log(err));
            } else {
                  setProdErr("Some fields are blank");
        }
    } 
    // } else {
    //     
    // }
}
