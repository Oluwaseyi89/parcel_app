import './styles/vendor.css';
import './styles/product.css';
import { useState, useEffect, useReducer, useCallback } from 'react';
import { useHistory } from 'react-router-dom';
import CourierDeals from './CourierDeals';
import CourierDispatch from './CourierDispatch';
import CourierResolutions from './CourierResolutions'
import CourierTransactions from './CourierTransactions'
import { UseFetchJSON } from './useFetch';



const CourierDashBoard = () => {
    // localStorage.removeItem('logcour');

    const [dealtab, setDealTab] = useState(false);
    const [dispatchtab, setDispatchTab] = useState(false);
    const [resoltab, setResolTab] = useState(false);
    const [txntab, setTxnTab] = useState(false);

    const handleDealTabClick = () => {
        setDealTab(true);
        setDispatchTab(false);
        setResolTab(false);
        setTxnTab(false);
    }

    const handleDispatchTabClick = () => {
        setDealTab(false);
        setDispatchTab(true);
        setResolTab(false);
        setTxnTab(false);
    }

    const handleResolTabClick = () => {
        setDealTab(false);
        setDispatchTab(false);
        setResolTab(true);
        setTxnTab(false);
    }


    const handleTxnTabClick = () => {
        setDealTab(false);
        setDispatchTab(false);
        setResolTab(false);
        setTxnTab(true);
    }

    let history = useHistory();
    const handleLogout = () => {
        localStorage.removeItem('logcour');
        history.push('/courier');
    }
    let loggedCour = JSON.parse(localStorage.getItem('logcour')) || null;

    
   

    // const {first_name, last_name, cour_photo} = loggedCour;

    return (
     <div className='container log-vendor'>
         {loggedCour ? (<div>
            <div>
                <button onClick={handleLogout} className='btn logout-btn'>Logout</button><h4 className='Vendor-Log-Legend'>Courier Dashboard</h4> <br/>
            </div> 
            <div className='photo-container'>
                {loggedCour && <p id='vendor-details'>Hello, {loggedCour.last_name + " " + loggedCour.first_name}</p>}
                <div className='avatar-container'>
                    {loggedCour && <img alt='avatar' src={loggedCour.cour_photo}/>}
                </div>
            </div>
         </div>): history.push('/courier')}
         <div className='sub-tab'>
             <div onClick={handleDealTabClick} className={dealtab?"sub-tab-cour-active":"sub-tab-cour-item"} >Deals</div>
             <div onClick={handleDispatchTabClick} className={dispatchtab?"sub-tab-cour-active":"sub-tab-cour-item"} >Dispatches</div>
             <div onClick={handleTxnTabClick} className={txntab?"sub-tab-cour-active":"sub-tab-cour-item"} >Transactions</div>
             <div onClick={handleResolTabClick} className={resoltab?"sub-tab-cour-active":"sub-tab-cour-item"} >Resolutions</div>
         </div>
         <div>
             {dealtab?<CourierDeals/>:""}
             {dispatchtab?<CourierDispatch/>:""}
             {txntab?<CourierTransactions/>:""}
             {resoltab?<CourierResolutions/>:""}
         </div>
     </div>
    );
}

export default CourierDashBoard