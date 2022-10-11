import './styles/vendor.css';
// import { Link } from 'react-router-dom';
// import CSRFTOKEN from './CSRFTOKEN';
import { useState, useCallback, useEffect, useReducer } from 'react';
import { UseFetchJSON, UseFetch } from './useFetch';


const VendDeals = () => {
    
    const [prodsus, setProdSus] = useState("");
    const [proderr, setProdErr] = useState("");
    const [loadDeals, setLoadDeals] = useState(true);

    let logvend = JSON.parse(localStorage.getItem('logvend')) || null;
    console.log(logvend);


    
   const initialDeals = {
       "deals": []
   }

   const getDeals = useCallback((data) => {
    return dispatch({
        type: "GET_DEALS",
        payload: data,
    });
  }, []);

  const handleSupplyReadyClick = (e, order_id, product_id) => {
    return dispatch({
        type: "SUPPLY_READY",
        payload: {"e":e, "order_id": order_id, "product_id": product_id}
    });
  }

   const reducer = (state=initialDeals, action) => {
       if(action.type === "GET_DEALS") {

           let vendDealCollectn = [];
        action.payload.forEach((deal) => {
            deal.products.forEach((prod) => {
                if(prod.vendor_phone === logvend.phone_no) vendDealCollectn.push(prod);
            })            
        });
      
        let modDealColl = [];
        vendDealCollectn.forEach((deal) => {
            action.payload.forEach((mod) => {                
                if(mod.order_id === deal.order_id) {
                    modDealColl.push({...deal, 
                        "handled_dispatch": mod.handled_dispatch,
                        "courier_name": mod.courier_name,
                        "courier_phone": mod.courier_phone
                    });
                }
            });
        });
        return {...state, deals: modDealColl} 
       }

       if(action.type === "SUPPLY_READY") {
        let newState = state.deals.map((item) => {
            if (item.order_id === action.payload.order_id && item.product_id === action.payload.product_id) {                                    
                let order_id = action.payload.order_id;
                let product_id = action.payload.product_id;
                let e = action.payload.e;
                let checked = e.target.checked;
                item.is_supply_ready = checked;
                console.log(checked);                
                let formData = new FormData();
                formData.append("is_supply_ready", checked);
                formData.append("updated_at", new Date().toISOString());
                let apUrl = `http://localhost:7000/parcel_dispatch/update_supplied_product/${order_id}/${product_id}/`;
                let data = UseFetch(apUrl, 'POST', formData);
                data.then((res) => {
                    if (res.status === 'success') {
                        setProdSus(res.data)
                    }
                }).catch((err) => console.log(err.message));
                return item;
            }
            return { ...item }        

        });
        return { ...state, deals: newState }
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
            <p id='deal-count'>There are {state.deals.length} supply deals available</p>
            
             {proderr?(<div id='apiAlert' style={{"height": "50px", "textAlign": "center"}} className='alert alert-danger alert-dismissible' role='alert'>
                                    {proderr}
                                    <button className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                    </div>): "" }
             {prodsus?(<div id='apiAlert' style={{"height": "50px", "textAlign": "center"}} className='alert alert-success alert-dismissible' role='alert'>
                                    {prodsus}
                                    <button className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                    </div>): "" }
            <div className='grid-deals'>
                {state.deals.map((item, key) => {
                    return (
                        <div className='dispatch-prod-container' key={item.order_id + item.product_id}>
                          
                               <div className='dispatch-img-container'>
                               <img className='img-thumbnail dispatch-prod-img' src={item.prod_photo} alt='prod-img' />                                   
                               </div>
                               <div>
                                    <div><strong>Courier: </strong> {item.courier_name === "000"? "No Courier yet" :item.courier_name}</div>
                                    <div><strong>Phone: </strong> {item.courier_phone === "000"? "No Courier yet" :item.courier_phone}</div>                                  
                                    <div className={item.is_supply_received?"delivery-yes":"delivery-no"}>
                                        <strong style={{color:"black"}}>Courier Status: </strong>
                                            {item.is_supply_received?"Received":"Pending"}
                                    </div>
                                    <div className={item.is_received?"delivery-yes":"delivery-no"}>
                                        <strong style={{color:"black"}}>Customer Status: </strong>
                                            {item.is_received?"Received":"Pending"}
                                    </div>
                                    <div><strong>Order Id: </strong> {item.order_id}</div>
                                    <div><strong>Product: </strong> {item.product_name}</div>
                                    <div><strong>Model: </strong> {item.prod_model}</div>
                                    <div><strong>Price: </strong> ₦ {item.prod_price}</div>
                                    <div><strong>Qty: </strong> {item.quantity}</div>
                                    <div><strong>Amount: </strong> ₦ {item.total_amount}</div>
                                    <div><strong>Ready for supply: </strong><input onChange={(e) => handleSupplyReadyClick(e, item.order_id, item.product_id)}  type='checkbox' checked={item.is_supply_ready}  /></div>
                               </div>
                          
                        </div>
                    )
                })}
            </div>
        </div>     
    );
}

export default VendDeals