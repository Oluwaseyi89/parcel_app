import './styles/vendor.css';
// import { Link } from 'react-router-dom';
// import CSRFTOKEN from './CSRFTOKEN';
import { useEffect, useState } from 'react';
import { savesingleorder } from './savesingleorder';
import { useHistory } from 'react-router-dom';
// import { UseFetch } from './useFetch';


const SingleProdCheckOutPage = () => {

    let buySingle = JSON.parse(localStorage.getItem('buySingle'));

    let history = useHistory();

    let refresh = () => {
        window.location.reload(false);
    }


    let cartItems = JSON.parse(localStorage.getItem('parcelCart')) || null;
    let logcus = JSON.parse(localStorage.getItem('logcus')) || null;
    const [totPrice, setTotPrice] = useState();
    const [proderr, setProdErr] = useState("");
    const [prodsus, setProdSus] = useState("");

    useEffect(()=>{
        if (buySingle) {
            let t_price = buySingle.prod_price * buySingle.purchased_qty;
            setTotPrice(t_price);
        }
    },[buySingle, totPrice]);

    let cartTot = buySingle ? buySingle.purchased_qty : 0;
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
            setCartUser({...cartUser, [name]:value})
        }


        const handleSingleOrderClick = (e) => {
            e.preventDefault();
            savesingleorder(first_name, last_name, country, state,
            shipping_method, zip_code, street, phone_no, email, 
            reg_date, logcus, cartTot, totPrice, buySingle,
            customer_name, setProdSus, setProdErr, history);
        }
    
        const handleChangeQty = (e) => {
            if (buySingle) {
                        let value = e.target.value;
                        let newCartVal = {...buySingle, purchased_qty:parseInt(value)};
                        localStorage.setItem('buySingle', JSON.stringify(newCartVal));
                        refresh();
                    }
            }
        
     
   
    return (
        <div className="Vendor-Frag">
             {proderr?(<div id='apiAlert' style={{"height": "50px", "textAlign": "center"}} className='alert alert-danger alert-dismissible' role='alert'>
                                    {proderr}
                                    <button className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                    </div>): "" }
                    {prodsus?(<div  style={{"height": "50px", "textAlign": "center"}} className='alert alert-success alert-dismissible' role='alert'>
                                    {prodsus}
                                    <button  className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                    </div>): ""}

            <div className='single-check-con'>
                <div className='shipping-info'>
                        <p id='shipping-head' className='badge order-frag-head'>Shipping Info</p>
                        <form className='form container'>
                            <div className='form-group'>
                                <input type='text' name='first_name' value={logcus?logcus.first_name:first_name} onChange={handleCartFormChange} className='form-control check-input' placeholder='First Name'/>
                            </div>
                            <div className='form-group'>
                                <input type='text' name='last_name' value={logcus?logcus.last_name:last_name} onChange={handleCartFormChange} className='form-control check-input' placeholder='Last Name'/>
                            </div>
                            <div className='form-group'>
                                <select className='form-control check-input' name="shipping_method" value={shipping_method} onChange={handleCartFormChange}>
                                    <option>Shipping_Method</option>
                                    <option>Delivery</option>
                                    <option>Pick up</option>
                                </select>
                            </div>
                            <div className='form-group'>
                                <input type='text' name='street' value={logcus?logcus.street:street} onChange={handleCartFormChange} className='form-control check-input' placeholder='Street'/>
                            </div>
                            <div className='form-group'>
                                <input type='text' name='state' value={logcus?logcus.state:state} onChange={handleCartFormChange} className='form-control check-input' placeholder='State'/>
                            </div>
                            <div className='form-group'>
                                <input type='text' name='country' value={logcus?logcus.country:country} onChange={handleCartFormChange} className='form-control check-input' placeholder='Country'/>
                            </div>
                            <div className='form-group'>
                                <input type='text' name='zip_code' value={zip_code} onChange={handleCartFormChange} className='form-control check-input' placeholder='Zip Code'/>
                            </div>
                            <p style={{marginLeft:"10px"}}  className='badge order-frag-head'>Contact Info</p>
                            <div className='form-group'>
                                <input type='text' name='email' value={logcus?logcus.email:email} onChange={handleCartFormChange} className='form-control check-input' placeholder='E-mail Address'/>
                            </div>
                            <div className='form-group'>
                                <input type='text' name='phone_no' value={logcus?logcus.phone_no:phone_no} onChange={handleCartFormChange} className='form-control check-input' placeholder='Phone Number'/>
                            </div>
                            <div className='check-btn-fin'>
                                <button onClick={handleSingleOrderClick} className='btn'>Proceed to Payment</button>
                            </div>
                        </form>
                </div> 
                <div className='order-summary'>
                    <p  className='badge order-frag-head'>Order Summary</p>
                    
                    <div className='order-frag' >
                        <img alt='cart-avatar' src={buySingle.prod_photo} className="img-thumbnail"/>
                        <div className='order-frag-panel'>
                            <p className='badge'>{buySingle.prod_name}</p>
                            <p className='badge'>{buySingle.prod_model}</p>
                            <p className='badge'>₦ {buySingle.prod_price}</p>
                            <label className='badge'>Qty: <input style={{textAlign:"center"}} onChange={handleChangeQty} id='cart-item-qty' type="number" name="cart-item-qty" value={buySingle.purchased_qty}/></label>
                            <p className='badge'>Tot: ₦ {buySingle.prod_price * buySingle.purchased_qty}</p>
                           
                        </div>
                        <hr/>
                    </div>
                        <p style={{fontSize:"1.2em"}} className='badge'>G.Total: ₦ {totPrice}</p>
                </div>
            </div>
        </div>     
    );
}

export default SingleProdCheckOutPage