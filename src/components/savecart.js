import { UseFetchJSON } from "./useFetch";
export const savecart = (first_name, last_name, country, state, shipping_method,
    zip_code, street, phone_no, email, reg_date, logcus, cartTot, 
    totPrice, cartItems, customer_name, setProdSus, setProdErr) => {
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
            let session_detail = {
                "customer_id": id,
                "customer_name": cust_name,
                "total_items": cartTot,
                "total_price": totPrice,
                "shipping_method": shipping_method,
                "zip_code": zip_code,
                "is_customer": true,
                "created_at": new Date().toISOString(),
                "updated_at": new Date().toISOString() 
            }
            let sessUrl = `http://localhost:7000/parcel_customer/cart_save/${cust_name}/`;
                            let saveSession = UseFetchJSON(sessUrl, 'POST', session_detail);
                            saveSession.then((res) => {
                                if (res.status === "success") {
                                    let sesId = res.data;
                                    // setSessionId(0);
                                    // setSessionId(res.data);
                                    if(sesId) {
                                        // console.log(session_id);
                                        cartItems.forEach((prod) => {
                                            let product_name = prod.prod_name;
                                            let product_id = prod.id;
                                            let quantity = prod.purchased_qty
                                            let cart_detail = {
                                                "session_id": sesId,
                                                "product_id": product_id,
                                                "product_name": product_name,
                                                "quantity": quantity,
                                                "is_customer": true,
                                                "created_at": new Date().toISOString(),
                                                "updated_at": new Date().toISOString()
                                            }
                                            let cartDUrl = `http://localhost:7000/parcel_customer/prod_cart_save/${sesId}/${product_id}/`;
                                            let saveCartDetail = UseFetchJSON(cartDUrl, 'POST', cart_detail);
                                            saveCartDetail.then((res) => {
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
                                        });
                                    }
                                } else if (res.status === "error") {
                                    // console.log(res.data);
                                    let session_detail = {
                                        "customer_id": id,
                                        "customer_name": cust_name,
                                        "total_items": cartTot,
                                        "total_price": totPrice,
                                        "shipping_method": shipping_method,
                                        "zip_code": zip_code,
                                        "is_customer": true,
                                        "updated_at": new Date().toISOString() 
                                    }
                                    let sessUrl = `http://localhost:7000/parcel_customer/cart_update/${cust_name}/`;
                                    let saveSession = UseFetchJSON(sessUrl, 'PATCH', session_detail);
                                    saveSession.then((res) => {
                                        if (res.status === "success") {
                                            let alrSesId = res.data;
                                            console.log(alrSesId);
                                            // setSessionId(0);
                                            // setSessionId(res.data);
                                            if(alrSesId) {
                                                // console.log(session_id);
                                                cartItems.forEach((prod) => {
                                                    let product_name = prod.prod_name;
                                                    let product_id = prod.id;
                                                    let quantity = prod.purchased_qty
                                                    let cart_detail = {
                                                        "session_id": alrSesId,
                                                        "product_id": product_id,
                                                        "product_name": product_name,
                                                        "quantity": quantity,
                                                        "is_customer": true,
                                                        "updated_at": new Date().toISOString()
                                                    }
                                                    let cartDUrl = `http://localhost:7000/parcel_customer/prod_cart_update/${alrSesId}/${product_id}/`;
                                                    let saveCartDetail = UseFetchJSON(cartDUrl, 'PATCH', cart_detail);
                                                    saveCartDetail.then((res) => {
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
                                                });
                                            }
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
                            let session_detail = {
                                "customer_id": anaCusId,
                                "customer_name": customer_name,
                                "total_items": cartTot,
                                "total_price": totPrice,
                                "shipping_method": shipping_method,
                                "zip_code": zip_code,
                                "is_customer": false,
                                "created_at": new Date().toISOString(),
                                "updated_at": new Date().toISOString() 
                            }
                            let sessUrl = `http://localhost:7000/parcel_customer/cart_save/${customer_name}/`;
                            let saveSession = UseFetchJSON(sessUrl, 'POST', session_detail);
                            saveSession.then((res) => {
                                if (res.status === "success") {
                                    let anaSesId = res.data;
                                    // setSessionId(0);
                                    // setSessionId(res.data);
                                    if(anaSesId) {
                                        // console.log(session_id);
                                        cartItems.forEach((prod) => {
                                            let product_name = prod.prod_name;
                                            let product_id = prod.id;
                                            let quantity = prod.purchased_qty
                                            let cart_detail = {
                                                "session_id": anaSesId,
                                                "product_id": product_id,
                                                "product_name": product_name,
                                                "quantity": quantity,
                                                "is_customer": false,
                                                "created_at": new Date().toISOString(),
                                                "updated_at": new Date().toISOString()
                                            }
                                            let cartDUrl = `http://localhost:7000/parcel_customer/prod_cart_save/${anaSesId}/${product_id}/`;
                                            let saveCartDetail = UseFetchJSON(cartDUrl, 'POST', cart_detail);
                                            saveCartDetail.then((res) => {
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
                                        });
                                    }
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
                            let session_detail = {
                                "customer_id": alrCusId,
                                "customer_name": customer_name,
                                "total_items": cartTot,
                                "total_price": totPrice,
                                "shipping_method": shipping_method,
                                "zip_code": zip_code,
                                "is_customer": false,
                                "updated_at": new Date().toISOString() 
                            }
                            let sessUrl = `http://localhost:7000/parcel_customer/cart_update/${customer_name}/`;
                            let saveSession = UseFetchJSON(sessUrl, 'PATCH', session_detail);
                            saveSession.then((res) => {
                                if (res.status === "success") {
                                    let alrSesId = res.data;
                                    console.log(alrSesId);
                                    // setSessionId(0);
                                    // setSessionId(res.data);
                                    if(alrSesId) {
                                        // console.log(session_id);
                                        cartItems.forEach((prod) => {
                                            let product_name = prod.prod_name;
                                            let product_id = prod.id;
                                            let quantity = prod.purchased_qty
                                            let cart_detail = {
                                                "session_id": alrSesId,
                                                "product_id": product_id,
                                                "product_name": product_name,
                                                "quantity": quantity,
                                                "is_customer": false,
                                                "updated_at": new Date().toISOString()
                                            }
                                            let cartDUrl = `http://localhost:7000/parcel_customer/prod_cart_update/${alrSesId}/${product_id}/`;
                                            let saveCartDetail = UseFetchJSON(cartDUrl, 'PATCH', cart_detail);
                                            saveCartDetail.then((res) => {
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
                                        });
                                    }
                                } else if (res.status === "error") {
                                    setProdErr(res.data);
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
