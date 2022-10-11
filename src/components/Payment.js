import { useEffect, useState } from 'react';
// import { usePaystackPayment } from 'react-paystack';
import './styles/vendor.css';
import { UseFetchJSON } from './useFetch';
// import { Link } from 'react-router-dom';
// import CSRFTOKEN from './CSRFTOKEN';
// import { useState } from 'react';
// import { UseFetch } from './useFetch';


// const config = {
//     reference: (new Date()).getTime().toString(),
//     email: "isenewoephr2012@gmail.com",
//     amount: 200000,
//     publicKey: "sk_test_b2ffbedf2d7b2cf10229235415ba2dac551c684e"
// };

// const onSuccess = (reference) => {
//     console.log(reference);
// }

// const onClose = () => {
//     console.log('closed');
// }

// const PaystackHookExample = () => {
//     const initializePayment = usePaystackPayment(config);

//     return (
//         <div>
//             <button className='btn' onClick={()=>initializePayment(onSuccess, onClose)}>
//                 InitPay
//             </button>
//         </div>
//     );
// }

const Payment = () => {

    const [showProvider, setShowProvider] = useState(false);
    const [showTxnRef, setShowTxnRef] = useState(false);
    const [showShipTxnRef, setShowShipTxnRef] = useState(false);
    const [showPayDeliv, setShowPayDeliv] = useState(false);
    const [showShipProv, setShowShipProv] = useState(false);
    const [customerId, setCustomerId] = useState(0);
    const [isCustomer, setIsCustomer] = useState(false);
    const [proderr, setProdErr] = useState("");
    const [prodsus, setProdSus] = useState("");

    let curOrder = JSON.parse(localStorage.getItem('curOrder')) || null;

    const [payment, setPayment] = useState({
        "shipping_fee": "",
        "amount": "",
        "grand_total_amount": "",
        "payment_type": "",
        "shipping_pay_type": "",
        "provider": "",
        "shipping_provider": "",
        "txn_ref": "",
        "shipping_txn_ref": ""
    });

    const { shipping_fee, shipping_provider, shipping_pay_type, amount, txn_ref, shipping_txn_ref, grand_total_amount, payment_type, provider } = payment;

    const handlePaymentFormChange = (e) => {
        let name = e.target.name;
        let value = e.target.value;
        setPayment({...payment, [name]:value})
    }

    console.log(customerId);
    console.log(isCustomer);
    
    const handlePayClick = (e) => {
        e.preventDefault();
        let payUrl = "http://localhost:8080/v1/initializetransaction";
        let amount = grand_total_amount * 100;
        let email = "";
        let anonCusUrl = `http://localhost:7000/parcel_customer/get_anon_customer/${customerId}/`;
        let cusUrl = `http://localhost:7000/parcel_customer/get_customer/${customerId}/`;

        if(isCustomer) {
            let data = UseFetchJSON(cusUrl, 'GET');
            data.then((res)=> {
                if(res.status === "success") {
                    email = res.data.email;
                    console.log(email);

                   let payData = {
                        "email": email,
                        "amount": amount,
                        "callback_url": "http://localhost:3000/verify"
                    }

                    let initPay = UseFetchJSON(payUrl, 'POST', payData);
                    initPay.finally(() => setProdSus("Processing your payment..."))
                    .then(async (res) => {
                        let auth_url = await res.data.authorization_url;
                        let reference = await res.data.reference;
                        localStorage.setItem("payRef", reference);
                        let updateData = {
                            "shipping_fee": shipping_fee,
                            "grand_total_amount": grand_total_amount,
                            "reference": reference,
                            "updated_at": new Date().toISOString()
                        }
                        let updateUrl = `http://localhost:7000/parcel_order/payment_update/${curOrder}/`;
                        let updatePayDetail = UseFetchJSON(updateUrl, 'PATCH', updateData);

                        updatePayDetail.then((mess) => {
                            if (mess.status === "success") {
                                window.location = auth_url;
                            } else {
                                setProdErr("Error occured, order might have expired");
                            }
                        });
                        
                    }).catch((err) => {
                        if (err) {
                            setProdSus("");
                            setProdErr("Unable to connect with payment API");
                        }
                    });
                }
            });
        } else if (isCustomer === false) {
            let data = UseFetchJSON(anonCusUrl, 'GET');
            data.then((res)=> {
                if(res.status === "success") {
                    email = res.data.email;
                    console.log(email);
                   let payData = {
                        "email": email,
                        "amount": amount,
                        "callback_url": "http://localhost:3000/verify"
                    }
                    let initPay = UseFetchJSON(payUrl, 'POST', payData);
                    initPay.finally(() => setProdSus("Processing your payment..."))
                    .then( async (res) => {
                        let auth_url = await res.data.authorization_url;
                        let reference = await res.data.reference;
                        localStorage.setItem("payRef", reference);
                        let updateData = {
                            "shipping_fee": shipping_fee,
                            "grand_total_amount": grand_total_amount,
                            "reference": reference,
                            "updated_at": new Date().toISOString()
                        }
                        let updateUrl = `http://localhost:7000/parcel_order/payment_update/${curOrder}/`;
                        let updatePayDetail = UseFetchJSON(updateUrl, 'PATCH', updateData);

                        updatePayDetail.then((mess) => {
                            if (mess.status === "success") {
                                window.location = auth_url;
                            } else {
                                setProdErr("Error occured, order might have expired");
                            }
                        });
                    }).catch((err) =>  {
                        if (err) {
                            setProdSus("");
                            setProdErr("Unable to connect with payment API");
                        }
                    });
                }
            });  
        }
    }

    useEffect(()=>{
        if (payment_type === "Card Payment") setShowProvider(true);
        else setShowProvider(false);

        if (payment_type === "Bank Transfer") setShowTxnRef(true);
        else setShowTxnRef(false);

        if (payment_type === "Bank Transfer On Delivery") setShowPayDeliv(true);
        else setShowPayDeliv(false);
    },[payment_type]);

    useEffect(()=>{
        if (shipping_pay_type === "Card Payment for Shipping") setShowShipProv(true);
        else setShowShipProv(false);

        if (shipping_pay_type === "Bank Transfer for Shipping") setShowShipTxnRef(true);
        else setShowShipTxnRef(false);
    },[shipping_pay_type]);

    useEffect(()=>{
        if (curOrder) {
            let payUrl = `http://localhost:7000/parcel_order/get_order_id/${curOrder}`;
            let setPayDetail = UseFetchJSON(payUrl, 'GET');
            setPayDetail.then((res) => {
                if (res.status === "success") {
                    setPayment({...payment, shipping_fee:res.data.shipping_fee, 
                        amount:res.data.total_price, grand_total_amount:(res.data.shipping_fee + res.data.total_price)});
                        setCustomerId(res.data.customer_id);
                        setIsCustomer(res.data.is_customer);
                }
            });
        }
    },[curOrder]);


   
    return (
        <div className="payment-frag">
              {proderr?(<div id='apiAlert' style={{"height": "50px", "textAlign": "center"}} className='alert alert-danger alert-dismissible' role='alert'>
                                    {proderr}
                                    <button className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                    </div>): "" }
                    {prodsus?(<div  style={{"height": "50px", "textAlign": "center"}} className='alert alert-success alert-dismissible' role='alert'>
                                    {prodsus}
                                    <button  className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                    </div>): ""}

            <form className='form'>
                <div className='form-group'>
                    <input name='shipping_fee' type='text' readOnly="readonly" value={"Shipping Fee = " + shipping_fee} className="form-control"/>
                </div>
                <div className='form-group'>
                    <input name='amount' type='text' readOnly="readonly" value={"Amount = " + amount} className="form-control"/>
                </div>
                <div className='form-group'>
                    <input name='grand_total_amount' type='text' readOnly="readonly" value={"Grand Total Amount = " + grand_total_amount} className="form-control"/>
                </div>
                <div className="form-group">
                    <select name='payment_type' value={payment_type} onChange={handlePaymentFormChange} className='form-control'>
                        <option>Select Payment Type</option>
                        <option>Card Payment</option>
                        <option>Bank Transfer</option>
                        <option>Bank Transfer On Delivery</option>
                    </select>
                </div>
                {showProvider?(<div className="form-group">
                    <select name='provider' onChange={handlePaymentFormChange} className='form-control'>
                        <option>Select Provider</option>
                        <option>Master Card</option>
                        <option>Verve Card</option>
                        <option>Visa Card</option>
                    </select>
                    <input type='submit' value="Pay" onClick={handlePayClick} />
                </div>):""}
                {showTxnRef?(<div className='form-group'>
                    <label className='form-control'>
                        Account No: 3073566093 
                    </label>
                    <label className='form-control'>
                        Bank: First Bank
                    </label>
                    <input name='txn_ref' value={txn_ref} onChange={handlePaymentFormChange} className="form-control" placeholder='Enter Transaction Reference' />
                    <br/>
                    <input type='submit' value="Submit" className='form-control'/>
                </div>):""}
                {showPayDeliv?(<div className="form-group">                    
                    <div className='form-group'>
                        <select name='shipping_pay_type' value={shipping_pay_type} onChange={handlePaymentFormChange} className='form-control'>
                            <option>Select Payment Type for Shipping Fee</option>
                            <option>Card Payment for Shipping</option>
                            <option>Bank Transfer for Shipping</option>                            
                        </select>
                    </div>
                    {showShipProv?(<div className='form-group'>
                        <select name='shipping_provider' value={shipping_provider} onChange={handlePaymentFormChange} className='form-control'>
                            <option>Select Provider</option>
                            <option>Master Card</option>
                            <option>Verve Card</option>
                            <option>Visa Card</option>
                        </select>
                    </div>):""}
                    {showShipTxnRef?(<div className='form-group'>
                    <label className='form-control'>
                        Account No: 3073566093 
                    </label>
                    <label className='form-control'>
                        Bank: First Bank
                    </label>
                    <input name='shipping_txn_ref' value={shipping_txn_ref} onChange={handlePaymentFormChange} className="form-control" placeholder='Enter Transaction Reference' />
                    <br/>
                    <input type='submit' value="Submit" className='form-control'/>
                </div>):""}
                </div>):""}
            </form>
              {/* <PaystackHookExample/> */}
        </div>     
    );
}

export default Payment