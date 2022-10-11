import { Link, useHistory } from 'react-router-dom';
import { useCallback, useEffect, useState } from 'react';
import './styles/header.css';



const Header = () => {

    const [cartVal, setCartVal] = useState(0);
    let history = useHistory();
    let logcus = JSON.parse(localStorage.getItem('logcus'));
    let logvend = JSON.parse(localStorage.getItem('logvend'));
    let logcour = JSON.parse(localStorage.getItem('logcour'));

    let refresh = () => {
        window.location.reload(false);
    }
    // setInterval(() => {
    //    refresh();
    // }, 10000);
    // console.log(logcus);

    // const handleCustomerDashboardView = () => {
    //     if(logcus) history.push('/customer-dash')
    // }

//     const [cartVal, setCartVal] = useState(0);
//     // let totItem = 0;
//     let parcelCart = JSON.parse(localStorage.getItem('parcelCart')) || null;
//     console.log(parcelCart);
//    let calcCart = useCallback(()=>{
//     if(parcelCart) {
//         let totItem = 0;
//         for (let i=0; i<parcelCart.length; i++) {
//             let num = parcelCart[i].purchased_qty;
//             totItem += num;
//         }
//         console.log(totItem);
//         setCartVal(totItem);
//     }
//    },[parcelCart]); 

//    useEffect(()=>{
//        calcCart();
//    },[calcCart, parcelCart]);


const updateCartTot = useCallback(()=>{
    let cartTot = JSON.parse(localStorage.getItem("cartTot")) || null;
    if(cartTot) setCartVal(cartTot.totItem);
},[]);


useEffect(()=>{
    updateCartTot();
},[updateCartTot]);


  
    return (
      <div className="mainHeader">
          <div className="upperHeader">
                <div id="brand-container">
                    <img className="img-fluid" alt="brand-logo" id="brand_icon" src="parcel_ico.png" />
                </div>
                <div id="search-box">
                    <input type="search" placeholder="Search for Products" id="search-input"/>
                    <button type="button" id="submit-input">
                        <img className="tab-icon" id="search-img" src="search_ico.png" alt="search-submit" />
                    </button>
                </div>
                <Link to='/cart-check'>
                    <div id="cart">
                        <div id="cart-img-con">
                            <img id="cart-img" alt="cart-img" src="cart_bask_ico.png" className="img-fluid"/>
                        </div>
                        <div id="cart-val">
                            <p className='badge' style={{textAlign:"center", color:"rgb(219, 33, 76)", fontSize:"1.2em"}}>{cartVal}</p>
                        </div>
                    </div>
                </Link>
          </div>
          <div className="buffer">

          </div>
          <div className="lowerHeader">
          <Link to="/home"><div className="tab-container" id="home">
                    <img className="tab-icon" src="home_ico.png" alt="vendor_icon"/>
                    {window.innerWidth > 415? <p className="tab-info">Home</p>: ""}
                </div></Link>
                <Link to={logvend?"/vendor-dash":"/vendor"}><div className="tab-container" id="vendor">
                    <img className="tab-icon" src="vendor_ico.png" alt="vendor_icon"/>
                    {window.innerWidth > 415? <p className="tab-info">Vendor</p>: ""}
                </div></Link>
                <Link to={logcour?"/courier-dash":"/courier"}><div className="tab-container" id="transporter">
                    <img className="tab-icon" src="courier_ico.png" alt="courier_icon"/>
                    {window.innerWidth > 415? <p className="tab-info">Courier</p>: ""}
                </div></Link>
                <Link to="/catalogue"><div className="tab-container" id="catalogue">
                    <img className="tab-icon" src="catalogue_ico.png" alt="catalogue_icon"/>
                    {window.innerWidth > 415? <p className="tab-info">Catalogue</p>: ""}
                </div></Link>
                <Link to="/hot-deals"><div className="tab-container" id="hot-deals">
                    <img className="tab-icon" src="hot_deal_ico.png" alt="hot-deal_icon"/>
                    {window.innerWidth > 415? <p className="tab-info">Hot-Deals</p>: ""}
                </div></Link>
                <Link to={logcus?'/customer-dash':'/customer'}><div className="tab-container" id="client">
                    <img className="tab-icon" src="usericon.png" alt="usericon"/>
                    {window.innerWidth > 415? <p className="tab-info">Customer</p>: ""}
                </div></Link>
          </div>
      </div>
    );
}

export default Header