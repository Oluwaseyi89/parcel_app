import '../styles/customer.css';
import { useEffect, useState } from 'react';
import { UseFetchJSON } from '../useFetch';
import { useHistory } from 'react-router-dom';



const CustomerCart = () => {

    let logcus = JSON.parse(localStorage.getItem('logcus')) || null;
    let retrCart = JSON.parse(localStorage.getItem('retrCart')) || null;
    const [retCartDetails, setRetCartDetails] = useState([]);
    const [doFetchCart, setDoFetchCart] = useState(true);
    console.log(retCartDetails);

    let history = useHistory();

    const handleContinueShopping = () => {
        history.push("/cart-check");
    }

    useEffect(() => {   

        if (logcus !== null) {
            let cust_name = logcus.last_name + " " + logcus.first_name;
            let apiUrl = `http://localhost:7000/parcel_customer/get_cart/${cust_name}/`;
            let getCart = UseFetchJSON(apiUrl, 'GET');
            getCart.then((res) => {
                if (res.status === "success") {                       
                    localStorage.setItem('retrCart', JSON.stringify(res.data));
                } else {
                    localStorage.removeItem('retrCart');
                }
            });
        }         
    }, [logcus]);

    useEffect(() => { 
        if(doFetchCart) {
            let details = []  
            if (retrCart) {
                retrCart.forEach((prod) => {
                    let apiUrl = `http://localhost:7000/parcel_product/get_sing_prod/${prod.product_id}/`;
                    let getCartDetail = UseFetchJSON(apiUrl, 'GET');
                    getCartDetail.then((res) => {
                        if (res.status === "success") {
                            details.push(res.data);
                            details = details.map((newItem) => {
                                if (newItem.id === prod.product_id) {
                                    return { ...newItem, purchased_qty: prod.quantity }
                                }
                                return newItem;
                            });
                            setDoFetchCart(false);
                            setRetCartDetails(details);
                        }
                    });
                });
            }
        }           
    }, [doFetchCart, retrCart]);

    return (
     <div className='cus-cart-container'>
         {retrCart && <p style={{"text-align":"center"}}>You have {retrCart.length} distinct products in your saved cart</p>}
         {retrCart === null && <p>You have no saved cart or it might have expired</p>}
         <button onClick={handleContinueShopping} className='btn cus-cart-btn'>Continue Shopping</button> <hr/>
         <div className='grid-deals'>
            {retCartDetails.map((prod) => {
                return (
                    <div className='dispatch-prod-container' key={prod.id}>                          
                        <div className='dispatch-img-container'>
                            <img className='img-thumbnail dispatch-prod-img' src={prod.prod_photo} alt='prod-img' />                                   
                        </div>
                        <div>                                     
                            <div><strong>Product: </strong> {prod.prod_name}</div>
                            <div><strong>Model: </strong> {prod.prod_model}</div>
                            <div><strong>Price: </strong> ₦ {prod.prod_price}</div>
                            <div><strong>Qty: </strong> {prod.purchased_qty}</div>
                            <div><strong>Amount: </strong> ₦ {prod.prod_price * prod.purchased_qty}</div>                    
                        </div>           
                    </div>
                )
            })}
         </div>
     </div>
    );
}

export default CustomerCart