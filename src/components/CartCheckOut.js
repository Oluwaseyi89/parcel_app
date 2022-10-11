import './styles/vendor.css';
import './styles/product.css';
import { useHistory } from 'react-router-dom';
import { useCallback, useEffect, useReducer, useState } from 'react';
import { UseFetchJSON } from './useFetch';
import { savecart } from './savecart';
import { saveorder } from './saveorder';
// import { Link } from 'react-router-dom';
// import CSRFTOKEN from './CSRFTOKEN';
// import { useState } from 'react';
// import { UseFetch } from './useFetch';


const CartCheckOut = () => {

    let history = useHistory();

    let refresh = () => {
        window.location.reload(false);
    }


    let cartItems = JSON.parse(localStorage.getItem('parcelCart')) || null;
    let logcus = JSON.parse(localStorage.getItem('logcus')) || null;
    const [totPrice, setTotPrice] = useState();
    const [proderr, setProdErr] = useState("");
    const [prodsus, setProdSus] = useState("");
    const [showCartAlrt, setShowCartAlrt] = useState(false);
    const [showCartAlrtSwitch, setShowCartAlrtSwitch] = useState(true);
    // const [customer_id, setCustomerId] = useState();
    // const [session_id, setSessionId] = useState();
    let storedTot = JSON.parse(localStorage.getItem('cartTot')) || null;
    let cartTot = storedTot ? storedTot.totItem : 0;
    console.log(cartTot);

    const [cartUser, setCartUser] = useState({
        "first_name": "",
        "last_name": "",
        "country": "",
        "state": "",
        "shipping_method": "",
        "street": "",
        "phone_no": "",
        "email": "",
        "zip_code": "",
        "reg_date": new Date().toISOString()
    });

    const { first_name, last_name, country, state, shipping_method, zip_code,
        street, phone_no, email, reg_date } = cartUser;

    let customer_name = last_name + " " + first_name;

    const handleCartFormChange = (e) => {
        e.preventDefault();
        let name = e.target.name;
        let value = e.target.value;
        setCartUser({ ...cartUser, [name]: value })
    }

    const handleSaveCartClick = (e) => {
        e.preventDefault();
        savecart(first_name, last_name, country, state,
            shipping_method, zip_code, street, phone_no, email,
            reg_date, logcus, cartTot, totPrice, cartItems,
            customer_name, setProdSus, setProdErr);
    }


    const handleSaveOrderClick = (e) => {
        e.preventDefault();
        saveorder(first_name, last_name, country, state,
            shipping_method, zip_code, street, phone_no, email,
            reg_date, logcus, cartTot, totPrice, cartItems,
            customer_name, setProdSus, setProdErr, history);
    }


    useEffect(() => {
        if (showCartAlrtSwitch) {

            if (logcus !== null) {
                let cust_name = logcus.last_name + " " + logcus.first_name;
                let apiUrl = `http://localhost:7000/parcel_customer/get_cart/${cust_name}/`;
                let getCart = UseFetchJSON(apiUrl, 'GET');
                getCart.then((res) => {
                    if (res.status === "success") {
                        setShowCartAlrt(true);
                        localStorage.setItem('retrCart', JSON.stringify(res.data));
                    }
                });
            } else {
                if (first_name && last_name) {
                    let cust_name = last_name + " " + first_name;
                    let apiUrl = `http://localhost:7000/parcel_customer/get_cart/${cust_name}/`;
                    let getCart = UseFetchJSON(apiUrl, 'GET');
                    getCart.then((res) => {
                        if (res.status === "success") {
                            setShowCartAlrt(true);
                            localStorage.setItem('retrCart', JSON.stringify(res.data));
                        }
                    });
                }
            }
        }
    }, [logcus, first_name, last_name, showCartAlrtSwitch]);

    const handleDiscardCart = () => {
        let retrCart = JSON.parse(localStorage.getItem('retrCart'));
        let session_id = retrCart[0].session_id;
        let apiUrl = `http://localhost:7000/parcel_customer/del_cart/${session_id}/`;
        let delCart = UseFetchJSON(apiUrl, 'DELETE');
        delCart.then((res) => {
            if (res.status === "success") setProdSus(res.data); setShowCartAlrt(false);
        });
        refresh();
    }

    const handleRetrieveCart = () => {
        let cartItems = JSON.parse(localStorage.getItem('parcelCart')) || null;
        let retrCart = JSON.parse(localStorage.getItem('retrCart')) || null;
        let filteredItems = [];
        let newItems = [];
        if (cartItems) {
            for (let i = 0; i < retrCart.length; i++) {
                cartItems = cartItems.filter((prod) => prod.id !== retrCart[i].product_id);
                filteredItems.push(cartItems);
            }
            newItems = filteredItems.pop();
        }

        let retCartDetails = [];
        retCartDetails.push(...newItems);
        if (retrCart) {
            retrCart.forEach((prod) => {
                let apiUrl = `http://localhost:7000/parcel_product/get_sing_prod/${prod.product_id}/`;
                let getCartDetail = UseFetchJSON(apiUrl, 'GET');
                getCartDetail.then((res) => {
                    if (res.status === "success") {
                        retCartDetails.push(res.data);
                        retCartDetails = retCartDetails.map((newItem) => {
                            if (newItem.id === prod.product_id) {
                                return { ...newItem, purchased_qty: prod.quantity }
                            }
                            return newItem;
                        });
                        localStorage.setItem('parcelCart', JSON.stringify(retCartDetails));
                    }
                });
            });
        }
        setShowCartAlrt(false);
        setShowCartAlrtSwitch(false);
        // refresh();
    }

    const getTotPrice = useCallback(() => {
        if (cartItems) {
            let g_tot = 0;
            for (let i = 0; i < cartItems.length; i++) {
                let p_qty = cartItems[i].purchased_qty;
                let p_price = cartItems[i].prod_price;
                let p_tot = p_qty * p_price;
                g_tot += p_tot;
            }
            setTotPrice(g_tot);
        }
    }, [cartItems]);

    useEffect(() => {
        getTotPrice();
    }, [getTotPrice, cartItems]);

    let initCartState = {
        cart: []
    }

    const getCartItems = () => {
        return dispatch({
            type: "GET_ITEMS"
        })
    }

    const handleChangeQty = useCallback((id, e) => {
        return dispatch({
            type: "CHANGE_QTY",
            payload: { "id": id, "e": e }
        })
    }, []);

    const removeItem = useCallback((id) => {
        return dispatch({
            type: "REMOVE_ITEM",
            payload: id
        })
    }, []);

    const cartReducer = (state, action) => {
        if (action.type === "GET_ITEMS") {
            if (cartItems !== null) {
                return { ...state, cart: cartItems }
            }
            return { ...state }
        }

        if (action.type === "REMOVE_ITEM") {
            let curItems = state.cart.filter((item) => item.id !== action.payload);
            localStorage.setItem('parcelCart', JSON.stringify(curItems));
            refresh();
            return { ...state, cart: curItems }
        }

        if (action.type === "CHANGE_QTY") {
            let changeProd = state.cart.map((prod) => {
                if (prod.id === action.payload.id) {
                    let e = action.payload.e;
                    let value = e.target.value;
                    return { ...prod, purchased_qty: parseInt(value) }
                }
                return { ...prod };
            });
            localStorage.setItem('parcelCart', JSON.stringify(changeProd));
            refresh();
            return { ...state, cart: changeProd }
        }

        return state;
    }
    useEffect(() => {
        getCartItems();
    }, []);
    const [cartState, dispatch] = useReducer(cartReducer, initCartState);

    return (
        <div className="Vendor-Frag">
            <h4 style={{ textAlign: "center", color: "rgb(219, 33, 76" }}>Cart Details</h4> <br />
            {showCartAlrt ? (<div className='cartAlert'>
                <p className='badge'>You have a saved cart. What do you wish to do with it?</p>
                <div className='cartBtnContainer'>
                    <button onClick={handleRetrieveCart} className='btn'>Retrieve</button>
                    <button onClick={handleDiscardCart} className='btn'>Discard</button>
                </div>
            </div>) : ""}
            {proderr ? (<div id='apiAlert' style={{ "height": "50px", "textAlign": "center" }} className='alert alert-danger alert-dismissible' role='alert'>
                {proderr}
                <button className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
            </div>) : ""}
            {prodsus ? (<div style={{ "height": "50px", "textAlign": "center" }} className='alert alert-success alert-dismissible' role='alert'>
                {prodsus}
                <button className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
            </div>) : ""}
            <div className='check-out-cont'>
                <div className='order-summary'>
                    <p className='badge order-frag-head'>Order Summary</p>
                    {cartState.cart.map((item) => (
                        <div className='order-frag' key={item.id}>
                            <img alt='cart-avatar' src={item.prod_photo} className="img-thumbnail" />
                            <div className='order-frag-panel'>
                                <p className='badge'>{item.prod_name}</p>
                                <p className='badge'>{item.prod_model}</p>
                                <p className='badge'>₦ {item.prod_price}</p>
                                <label className='badge'>Qty: <input style={{ textAlign: "center" }} onChange={(e) => handleChangeQty(item.id, e)} id='cart-item-qty' type="number" name="cart-item-qty" value={item.purchased_qty} /></label>
                                <p className='badge'>Tot: ₦ {item.prod_price * item.purchased_qty}</p>
                                <button onClick={() => removeItem(item.id)} className='btn'>Remove</button>
                            </div>
                            <hr />
                        </div>))}
                    {cartState.cart.length > 0 && <p style={{ fontSize: "1.2em" }} className='badge'>G.Total: ₦ {totPrice}</p>}
                </div>
                <div className='shipping-info'>
                    <p id='shipping-head' className='badge order-frag-head'>Shipping Info</p>
                    <form className='form container'>
                        <div className='form-group'>
                            <input type='text' name='first_name' value={logcus ? logcus.first_name : first_name} onChange={handleCartFormChange} className='form-control check-input' placeholder='First Name' />
                        </div>
                        <div className='form-group'>
                            <input type='text' name='last_name' value={logcus ? logcus.last_name : last_name} onChange={handleCartFormChange} className='form-control check-input' placeholder='Last Name' />
                        </div>
                        <div className='form-group'>
                            <select className='form-control check-input' name="shipping_method" value={shipping_method} onChange={handleCartFormChange}>
                                <option>Shipping_Method</option>
                                <option>Delivery</option>
                                <option>Pick up</option>
                            </select>
                        </div>
                        <div className='form-group'>
                            <input type='text' name='street' value={logcus ? logcus.street : street} onChange={handleCartFormChange} className='form-control check-input' placeholder='Street' />
                        </div>
                        <div className='form-group'>
                            <input type='text' name='state' value={logcus ? logcus.state : state} onChange={handleCartFormChange} className='form-control check-input' placeholder='State' />
                        </div>
                        <div className='form-group'>
                            <input type='text' name='country' value={logcus ? logcus.country : country} onChange={handleCartFormChange} className='form-control check-input' placeholder='Country' />
                        </div>
                        <div className='form-group'>
                            <input type='text' name='zip_code' value={zip_code} onChange={handleCartFormChange} className='form-control check-input' placeholder='Zip Code' />
                        </div>
                        <p style={{ marginLeft: "10px" }} className='badge order-frag-head'>Contact Info</p>
                        <div className='form-group'>
                            <input type='text' name='email' value={logcus ? logcus.email : email} onChange={handleCartFormChange} className='form-control check-input' placeholder='E-mail Address' />
                        </div>
                        <div className='form-group'>
                            <input type='text' name='phone_no' value={logcus ? logcus.phone_no : phone_no} onChange={handleCartFormChange} className='form-control check-input' placeholder='Phone Number' />
                        </div>
                        <div className='check-btn-fin'>
                            <button onClick={handleSaveOrderClick} className='btn'>Proceed to Payment</button><br /><br />
                            <button onClick={handleSaveCartClick} className='btn'>Save Cart</button>

                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}

export default CartCheckOut