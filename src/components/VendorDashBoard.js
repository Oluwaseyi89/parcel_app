import './styles/vendor.css';
// import { createContext, useContext } from 'react';
import { useHistory } from 'react-router-dom';
import { useState } from 'react';
import VendProducts from './VendProducts';
import VendDeals from './VendDeals';
import VendTransactions from './VendTransactions';
import VendResolutions from './VendResolutions';
// import {vendLogContext} from './Vendor';

// const VendLogContext = createContext();

const VendorDashBoard = () => {
    // const { logvend } = useContext(VendLogContext);
    
    let loggedVen = JSON.parse(localStorage.getItem('logvend')) || '';
    let history = useHistory()
    const handleLogout = () => {
        localStorage.removeItem('logvend');
        history.push('/vendor');
    }
    const [prodtab, setProdTab] = useState(false);
    const [dealtab, setDealTab] = useState(false);
    const [transtab, setTransTab] = useState(false);
    const [resoltab, setResolTab] = useState(false);
   
    const handleProdTabClick = () => {
        setProdTab(true);
        setDealTab(false);
        setTransTab(false);
        setResolTab(false);
    }
    const handleDealTabClick = () => {
        setProdTab(false);
        setDealTab(true);
        setTransTab(false);
        setResolTab(false);
    }
    const handleTransTabClick = () => {
        setProdTab(false);
        setDealTab(false);
        setTransTab(true);
        setResolTab(false);
    }
    const handleResolTabClick = () => {
        setProdTab(false);
        setDealTab(false);
        setTransTab(false);
        setResolTab(true);
    }
   

    // const {first_name, last_name, vend_photo} = loggedVen;

    return (
     <div className='container log-vendor'>
         {loggedVen ? (<div>
            <div>
                <button onClick={handleLogout} className='btn logout-btn'>Logout</button><h4 className='Vendor-Log-Legend'>Vendor Dashboard</h4> <br/>
            </div> 
            <div className='photo-container'>
                {loggedVen && <p id='vendor-details'>Hello, {loggedVen.last_name + " " + loggedVen.first_name}</p>}
                <div className='avatar-container'>
                    {loggedVen && <img src={loggedVen.vend_photo} alt="vendor-avatar"/>}
                </div>
            </div>
         </div>) : history.push('/vendor')}
         <div className='sub-tab'>
             <div onClick={handleProdTabClick} className={prodtab?"sub-tab-active":"sub-tab-item"} >Products</div>
             <div onClick={handleDealTabClick} className={dealtab?"sub-tab-active":"sub-tab-item"} >Deals</div>
             <div onClick={handleTransTabClick} className={transtab?"sub-tab-active":"sub-tab-item"} >Transactions</div>
             <div onClick={handleResolTabClick} className={resoltab?"sub-tab-active":"sub-tab-item"} >Resolutions</div>
         </div>
         <div>
             {prodtab?<VendProducts/>:""}
             {dealtab?<VendDeals/>:""}
             {transtab?<VendTransactions/>:""}
             {resoltab?<VendResolutions/>:""}
         </div>
     </div>
    );
}

export default VendorDashBoard