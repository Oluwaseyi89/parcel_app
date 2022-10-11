import './styles/vendor.css';
import { useState, useEffect, useCallback } from 'react';
import { useHistory } from 'react-router-dom';
import CustomerCart from './customerPrivatePages/CustomerCart';
import CustomerOrder from './customerPrivatePages/CustomerOrder';
import CustomerDelivery from './customerPrivatePages/CustomerDelivery';
import CustomerNotification from './customerPrivatePages/CustomerNotification';
import CustomerComplain from './customerPrivatePages/CustomerComplain';


const CustomerDashBoard = () => {
 
    let loggedCus = JSON.parse(localStorage.getItem('logcus')) || null;
    const [carttab, setCartTab] = useState(false);
    const [ordertab, setOrderTab] = useState(false);
    const [deliverytab, setDeliveryTab] = useState(false);
    const [notifytab, setNotifyTab] = useState(false);
    const [complaintab, setComplainTab] = useState(false);

    const handleCartTabClick = () => {
        setCartTab(true);
        setOrderTab(false);
        setDeliveryTab(false);
        setNotifyTab(false);
        setComplainTab(false);
    }

    const handleOrderTabClick = () => {
        setCartTab(false);
        setOrderTab(true);
        setDeliveryTab(false);
        setNotifyTab(false);
        setComplainTab(false);
    }

    const handleDeliveryTabClick = () => {
        setCartTab(false);
        setOrderTab(false);
        setDeliveryTab(true);
        setNotifyTab(false);
        setComplainTab(false);
    }


    const handleNotifyTabClick = () => {
        setCartTab(false);
        setOrderTab(false);
        setDeliveryTab(false);
        setNotifyTab(true);
        setComplainTab(false);
    }

    const handleComplainTabClick = () => {
        setCartTab(false);
        setOrderTab(false);
        setDeliveryTab(false);
        setNotifyTab(false);
        setComplainTab(true);
    }

    let history = useHistory();
    const handleLogout = () => {
        localStorage.removeItem('logcus');
        history.push('/customer');
    }    
   

    // const {first_name, last_name, cour_photo} = loggedCour;

    return (
     <div className='container log-vendor'>
         {loggedCus ? (<div>
            <div>
                <button onClick={handleLogout} className='btn logout-btn'>Logout</button><h4 className='Vendor-Log-Legend'>Customer Dashboard</h4> <br/>
            </div> 
            <div className='photo-container'>
                {loggedCus && <p id='vendor-details'>Hello, {loggedCus.last_name + " " + loggedCus.first_name}</p>}
            </div>
         </div>): history.push('/customer')}
         <div className='sub-tab'>
             <div onClick={handleCartTabClick} className={carttab?"sub-tab-cour-active":"sub-tab-item-cus"} >Carts</div>
             <div onClick={handleOrderTabClick} className={ordertab?"sub-tab-cour-active":"sub-tab-item-cus"} >Orders</div>
             <div onClick={handleDeliveryTabClick} className={deliverytab?"sub-tab-cour-active":"sub-tab-item-cus"} >Deliveries</div>
             <div onClick={handleNotifyTabClick} className={notifytab?"sub-tab-cour-active":"sub-tab-item-cus"} >Notifications</div>
             <div onClick={handleComplainTabClick} className={complaintab?"sub-tab-cour-active":"sub-tab-item-cus"} >Complaints</div>
         </div>
         <div>
             {carttab?<CustomerCart/>:""}
             {ordertab?<CustomerOrder/>:""}
             {deliverytab?<CustomerDelivery/>:""}
             {notifytab?<CustomerNotification/>:""}
             {complaintab?<CustomerComplain/>:""}
         </div>
     </div>
    );
}

export default CustomerDashBoard