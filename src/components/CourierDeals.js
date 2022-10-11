import './styles/vendor.css';
import './styles/courier.css';
// import { Link } from 'react-router-dom';
// import CSRFTOKEN from './CSRFTOKEN';
import { useState, useEffect, useReducer, useCallback } from 'react';
import { UseFetch, UseFetchJSON } from './useFetch';


const CourierDeals = () => {

    const [prodsus, setProdSus] = useState("");
    const [proderr, setProdErr] = useState("");
    const [loadDeals, setLoadDeals] = useState(true);

    let loggedCour = JSON.parse(localStorage.getItem('logcour')) || null;
    console.log(loggedCour);


    
   const initialDeals = {
       "deals": []
   }

   const getDeals = useCallback((data) => {
    return dispatch({
        type: "GET_DEALS",
        payload: data,
    });
  }, []);

  const acceptDeal = (id) => {
      return dispatch({
          type: "ACCEPT_DEAL",
          payload: id
        });
  }

  const handleAcceptClick = (param) => {
      acceptDeal(param);
  }

   const reducer = (state=initialDeals, action) => {
       if(action.type === "GET_DEALS") {
           let revDeal = action.payload.filter((item) => item.handled_dispatch === false);
        return {...state, deals: revDeal} 
       }

       if(action.type === "ACCEPT_DEAL") {
           state.deals.map((item) => {
               if (item.order_id === action.payload) {
                   if(loggedCour) {
                       let detail = {
                           "handled_dispatch": true,
                           "courier_id": loggedCour.id,
                           "courier_name": loggedCour.last_name + " " + loggedCour.first_name,
                           "courier_email": loggedCour.email,
                           "courier_phone": loggedCour.phone_no,
                           "updated_at": new Date().toISOString()
                       }

                       let apiUrl = `http://localhost:7000/parcel_dispatch/update_dispatch/${action.payload}/`;
                       let data = UseFetchJSON(apiUrl, 'PATCH', detail);
                       data.then((res) => {
                           if (res.status === "success") {
                               setProdSus(res.data);
                               setLoadDeals(true);
                            }
                        }).catch((err) => console.log(err.message));
                    }
                }
                
                let filteredDeals = state.deals.filter((sample) => sample.order_id !== action.payload);
                return {...state, deals:filteredDeals}
           });
           return {...state}
       }

       return state;
   }

   useEffect(()=>{        
    const fetchDeals = () => {
        let apiUrl = 'http://localhost:7000/parcel_dispatch/get_dispatch_from_db/'; 
        let data = UseFetchJSON(apiUrl, 'GET');
        data.then((res) => {
            let deals = res.deals;
            getDeals(deals);
        }).catch((err) => console.log(err.message));
    }
    if(loadDeals) {
        fetchDeals();
    }
    setLoadDeals(false);
},[getDeals, loadDeals]);


   const [state, dispatch] = useReducer(reducer, initialDeals);
   console.log(state);


    return (
        <div>
             {proderr?(<div id='apiAlert' style={{"height": "50px", "textAlign": "center"}} className='alert alert-danger alert-dismissible' role='alert'>
                                    {proderr}
                                    <button className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                    </div>): "" }
             {prodsus?(<div id='apiAlert' style={{"height": "50px", "textAlign": "center"}} className='alert alert-success alert-dismissible' role='alert'>
                                    {prodsus}
                                    <button className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                    </div>): "" }
            <p id='courierDealHeading'>Click 'Accept' to execute a deal</p>
            <p id='deal-count'>There are {state.deals.length} deals available</p>
            <div>
                {state.deals.map((item, key) => {
                    return (
                        <div className='delivery-frag' key={item.order_id}>
                            <div className='info-container'>
                                <div className='customer-info'>
                                    <strong>Customer's Info:</strong>
                                    <ul>
                                        <li className='delivery-para'> <strong>{item.customer_name}</strong></li>
                                        <li className='delivery-para'> {item.address}</li>
                                        <li className='delivery-para'> {item.phone_no}</li>
                                        <li className='delivery-para'> <strong>Order Id: </strong>{item.order_id}</li>
                                    </ul>
                                </div>
                                <div className='customer-info'>
                                    <div><strong>Vendors' Info:</strong> {item.products.map((prod, key) => {
                                        return (
                                            <div key={prod.product_id}>
                                                <ul>
                                                    <li><strong>{prod.vendor_name}</strong></li>
                                                    <li><strong className={prod.is_supply_ready?"delivery-yes":"delivery-no"}>{prod.is_supply_ready?"Supply is ready":"Supply not ready"}</strong></li>
                                                    <li>{prod.vendor_address}</li>
                                                    <li>{prod.vendor_phone}</li>
                                                    <li><strong>Order Id: </strong>{prod.order_id}</li>
                                                </ul>
                                            </div>
                                        )
                                    })}</div>
                                </div>
                            </div>
                            <div className='btn-frag'>
                                <button onClick={() => handleAcceptClick(item.order_id)} className='btn'>Accept</button>
                            </div>

                        </div>
                    )
                })}
            </div>
        </div>     
    );
}

export default CourierDeals