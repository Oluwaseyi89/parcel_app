import './styles/vendor.css';
import './styles/courier.css';
// import { Link } from 'react-router-dom';
// import CSRFTOKEN from './CSRFTOKEN';
import { useState, useEffect, useReducer, useCallback } from 'react';
import { UseFetchJSON, UseFetch } from './useFetch';


const CourierDispatch = () => {

    // const [dispatchProd, setDispatchProd] = useState({
    //     "is_delivered": false
    // });

    const [prodsus, setProdSus] = useState();
    const [proderr, setProdErr] = useState();
 
    let loggedCour = JSON.parse(localStorage.getItem('logcour')) || null;

    const initialDispatchables = {
        "dispatchables": []
    }

    const getDispatchables = useCallback((data) => {
        return dispatch({
            type: "GET_DISPATCHABLES",
            payload: data
        });
    }, []);

    const handleIsDelivered = (e, order_id, product_id) => {
        return dispatch({
            type: "SUBMIT_IS_DELIVERED",
            payload: {"e":e, "order_id": order_id, "product_id": product_id}
        });
    }

    const handleSupplyReceived = (e, order_id, product_id) => {
        return dispatch({
            type: "SUBMIT_SUPPLY_RECEIVED",
            payload: {"e":e, "order_id": order_id, "product_id": product_id}
        });
    }

    const reducer = (state, action) => {

        if(action.type === "GET_DISPATCHABLES") {
            let setData = action.payload.filter((data) => data.courier_email === loggedCour.email);
            return {...state, dispatchables: setData}
        }

        if(action.type === "SUBMIT_IS_DELIVERED") {
            let newState = state.dispatchables.map((item) => {
                if (item.order_id === action.payload.order_id) {
                    item.products.map((prod) => {
                        if (prod.product_id === action.payload.product_id) {
                            let order_id = action.payload.order_id;
                            let product_id = action.payload.product_id;
                            let e = action.payload.e;
                            let checked = e.target.checked;
                            prod.is_delivered = checked;
                            let detail = {
                                "is_delivered": checked,
                                "updated_at": new Date().toISOString()
                            }
                            let apiUrl = `http://localhost:7000/parcel_dispatch/update_dispatched_product/${order_id}/${product_id}/`;
                            let data = UseFetchJSON(apiUrl, 'PATCH', detail);
                            data.then((res) => {
                                if (res.status === 'success') {
                                    setProdSus(res.data)
                                }
                            }).catch((err) => console.log(err.message));
                            return prod;
                        }
                        return { ...prod }
                    });
                    return { ...item }
                }
                return { ...item }
            });
            return { ...state, dispatchables: newState }
        }


        if(action.type === "SUBMIT_SUPPLY_RECEIVED") {
            let newState = state.dispatchables.map((item) => {
                if (item.order_id === action.payload.order_id) {
                    item.products.map((prod) => {
                        if (prod.product_id === action.payload.product_id) {
                            let order_id = action.payload.order_id;
                            let product_id = action.payload.product_id;
                            let e = action.payload.e;
                            let checked = e.target.checked;
                            prod.is_supply_received = checked;
                            let formData = new FormData();
                            formData.append("is_supply_received", checked);
                            formData.append("updated_at", new Date().toISOString());
                            let apiUrl = `http://localhost:7000/parcel_dispatch/update_received_product/${order_id}/${product_id}/`;
                            let data = UseFetch(apiUrl, 'POST', formData);
                            data.then((res) => {
                                if (res.status === 'success') {
                                    setProdSus(res.data)
                                }
                            }).catch((err) => console.log(err.message));
                            return prod;
                        }
                        return { ...prod }
                    });
                    return { ...item }
                }
                return { ...item }
            });
            return { ...state, dispatchables: newState }
        }

        return state;
    }

    useEffect(() => {
        const fetchDispatchable = () => {
            let apiUrl = 'http://localhost:7000/parcel_dispatch/get_dispatch_from_db/'; 
            let data = UseFetchJSON(apiUrl, 'GET');
            data.then((res) => {
                let deals = res.deals;
                getDispatchables(deals);
            }).catch((err) => console.log(err.message));
        }

        fetchDispatchable();

    }, [getDispatchables]);


    const [state, dispatch] = useReducer(reducer, initialDispatchables);
    console.log(state);
    
   
    return (
        <div>
            <p style={{textAlign:"center", fontWeight:"bold", marginTop:"10px"}}>You have {state.dispatchables.length} Orders to dispatch</p>

            {proderr?(<div id='apiAlert' style={{"height": "50px", "textAlign": "center"}} className='alert alert-danger alert-dismissible' role='alert'>
                                    {proderr}
                                    <button className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                    </div>): "" }
             {prodsus?(<div id='apiAlert' style={{"height": "50px", "textAlign": "center"}} className='alert alert-success alert-dismissible' role='alert'>
                                    {prodsus}
                                    <button className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                    </div>): "" }
            {state.dispatchables.map((item) => {
                return (
                    <div className='dispatch-item-container' key={item.order_id}>
                        <div className='dispatch-customer-detail'>
                            <div><strong>Customer: </strong>{item.customer_name}</div>
                            <div><strong>Address: </strong>{item.address}</div>
                            <div><strong>Phone: </strong>{item.phone_no}</div>
                            <div><strong>Total Items: </strong>{item.total_items}</div>
                            <div><strong>Total Amount:  </strong> ₦ {item.total_price}</div>
                            <div className={item.is_delivered?"delivery-yes":"delivery-no"}>
                                <strong style={{color:"black"}}>Status: </strong>
                                {item.is_delivered?"Delivered":"Pending"}
                            </div>
                        </div>
                        <div className='dispatch-product-detail'>
                            {item.products.map((prod) => {                               
                                return (
                                    <div className='dispatch-prod-container' key={prod.product_id}>
                                        <div className='dispatch-img-container'>
                                            <img className='img-thumbnail dispatch-prod-img' src={prod.prod_photo} alt='prod-img' />
                                        </div>
                                        <div className='dispatch-prod-info'>
                                            <div><strong>Vendor: </strong> {prod.vendor_name}</div>
                                            <div><strong>Phone: </strong> {prod.vendor_phone}</div>
                                            <div><strong>Address: </strong> {prod.vendor_address}</div>
                                            <div><strong>Order Id: </strong> {prod.order_id}</div>
                                            <div><strong>Product: </strong> {prod.product_name}</div>
                                            <div><strong>Model: </strong> {prod.prod_model}</div>
                                            <div><strong>Price: </strong> ₦ {prod.prod_price}</div>
                                            <div><strong>Qty: </strong> {prod.quantity}</div>
                                            <div><strong>Amount: </strong> ₦ {prod.total_amount}</div>
                                            <div className={prod.is_supply_ready?'delivery-yes':'delivery-no'}><strong style={{color:"black"}}>Supply Status: </strong>{prod.is_supply_ready?"Ready":"Pending"}</div>
                                            <div><strong>I received supply: </strong><input onChange={(e) => handleSupplyReceived(e, prod.order_id, prod.product_id)} type='checkbox' checked={prod.is_supply_received}  /></div>
                                            <div className={prod.is_received?'delivery-yes':'delivery-no'}><strong style={{color:"black"}}>Customer Response: </strong>{prod.is_received?"Received":"Pending"}</div>
                                            <div><strong>I delivered this: </strong><input onChange={(e) => handleIsDelivered(e, prod.order_id, prod.product_id)} type='checkbox' checked={prod.is_delivered}  /></div>
                                        </div>
                                    </div>
                                )
                            })}

                        </div>
                    </div>
                )
            })}            
        </div>     
    );
}

export default CourierDispatch