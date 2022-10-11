import '../styles/customer.css';
import { useState, useEffect, useReducer, useCallback } from 'react';
import { UseFetchJSON } from '../useFetch';



const CustomerDelivery = () => {

    let loggedCus = JSON.parse(localStorage.getItem('logcus')) || null;
    const [prodsus, setProdSus] = useState();
    const [proderr, setProdErr] = useState();
    const [doFetch, setDoFetch] = useState(true);

    const initialDeliveries = {
        deliveries: []        
    }

    const getDispatchables = useCallback((data) => {
        return dispatch({
            type: "GET_DISPATCHABLES",
            payload: data
        });
    }, []);

    const handleSupplyReceived = (e, order_id, product_id) => {
        return dispatch({
            type: "SUBMIT_SUPPLY_RECEIVED",
            payload: {"e":e, "order_id": order_id, "product_id": product_id}
        });
    }


    const reducer = (state, action) => {

        if(action.type === "GET_DISPATCHABLES") {
            let setData = action.payload.filter((data) => data.email === loggedCus.email);
            let pendData = setData.map((each) => {
               let pendProd = each.products.filter((it) => it.is_received === false);
               if (pendProd.length === 0) {
                   let apiUrl = `http://localhost:7000/parcel_dispatch/update_dispatch/${each.order_id}/`;
                   let orderUrl = `http://localhost:7000/parcel_order/update_order_dispatched/${each.order_id}/`;
                   let detail = {
                       "is_delivered": true,
                       "is_received": true,
                       "updated_at": new Date().toISOString()
                   }

                   let updateData = {
                       "is_dispatched": true,
                       "updated_at": new Date().toISOString()
                   }

                   let deliveryData = UseFetchJSON(apiUrl, 'PATCH', detail);
                   deliveryData.then((res) => {
                       if(res.status === "success") {
                           console.log(res.data);
                           let orderUpdate = UseFetchJSON(orderUrl, 'PATCH', updateData);
                           orderUpdate.then((res) => {
                                if (res.status === "success") console.log(res.data);
                           }).catch((err) => console.log(err.message));                          
                           return {...each, is_received:true, is_delivered:true};
                       }
                   }).catch((err) => setProdErr(err.message));               
                   return each;                   
               }
               return each;
            });
            let useable = pendData.filter((ite) => ite.is_received === false);
            return {...state, deliveries: useable}
        }

        if(action.type === "SUBMIT_SUPPLY_RECEIVED") {
            let newState = state.deliveries.map((item) => {
                if (item.order_id === action.payload.order_id) {
                    item.products.map((prod) => {
                        if (prod.product_id === action.payload.product_id) {
                            let order_id = action.payload.order_id;
                            let product_id = action.payload.product_id;
                            let e = action.payload.e;
                            let checked = e.target.checked;
                            prod.is_received = checked;
                            let detail = {
                                "is_received": checked,
                                "updated_at": new Date().toISOString()
                            }
                            let apiUrl = `http://localhost:7000/parcel_dispatch/update_dispatched_product/${order_id}/${product_id}/`;
                            let data = UseFetchJSON(apiUrl, 'PATCH', detail);
                            data.then((res) => {
                                if (res.status === 'success') {
                                    setProdSus(res.data);
                                    setDoFetch(true);
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
            return { ...state, deliveries: newState }
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
        if(doFetch) {
            fetchDispatchable();
        }
        setDoFetch(false);
    }, [getDispatchables, doFetch]);

    const [state, dispatch] = useReducer(reducer, initialDeliveries);
    console.log(state);

    return (
        <div>
        <p style={{textAlign:"center", fontWeight:"bold", marginTop:"10px"}}>You have {state.deliveries.length} expected order to receive</p>

        {proderr?(<div id='apiAlert' style={{"height": "50px", "textAlign": "center"}} className='alert alert-danger alert-dismissible' role='alert'>
                                {proderr}
                                <button className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                </div>): "" }
         {prodsus?(<div id='apiAlert' style={{"height": "50px", "textAlign": "center"}} className='alert alert-success alert-dismissible' role='alert'>
                                {prodsus}
                                <button className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                </div>): "" }
        {state.deliveries.map((item) => {
            return (
                <div className='dispatch-item-container' key={item.order_id}>
                    <div className='dispatch-customer-detail'>
                        <div><strong>Order Id: </strong> {item.order_id}</div>
                        <div><strong>Courier: </strong> {item.courier_name === "000"? "No Courier yet" :item.courier_name}</div>
                        <div><strong>Phone: </strong> {item.courier_phone === "000"? "No Courier yet" :item.courier_phone}</div>                       
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
                                <div className='dispatch-prod-container' key={prod.order_id + prod.product_id}>
                          
                               <div className='dispatch-img-container'>
                               <img className='img-thumbnail dispatch-prod-img' src={prod.prod_photo} alt='prod-img' />                                   
                               </div>
                               <div>                                                                   
                                    <div className={prod.is_supply_ready?"delivery-yes":"delivery-no"}>
                                        <strong style={{color:"black"}}>Vendor Response: </strong>
                                            {prod.is_supply_ready?"Supplied":"Pending"}
                                    </div>
                                    <div className={prod.is_supply_received?"delivery-yes":"delivery-no"}>
                                        <strong style={{color:"black"}}>Courier Response: </strong>
                                            {prod.is_supply_received?"Received":"Pending"}
                                    </div>
                                    <div className={prod.is_delivered?"delivery-yes":"delivery-no"}>
                                        <strong style={{color:"black"}}>Delivery Status: </strong>
                                            {prod.is_delivered?"Delivered":"Pending"}
                                    </div>
                                    <div><strong>Order Id: </strong> {prod.order_id}</div>
                                    <div><strong>Product: </strong> {prod.product_name}</div>
                                    <div><strong>Model: </strong> {prod.prod_model}</div>
                                    <div><strong>Price: </strong> ₦ {prod.prod_price}</div>
                                    <div><strong>Qty: </strong> {prod.quantity}</div>
                                    <div><strong>Amount: </strong> ₦ {prod.total_amount}</div>
                                    <div><strong>I received this: </strong><input onChange={(e) => handleSupplyReceived(e, prod.order_id, prod.product_id)}  type='checkbox' checked={prod.is_received}  /></div>
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

export default CustomerDelivery