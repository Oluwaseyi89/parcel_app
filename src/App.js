import './App.css';
import { useState, useCallback, useEffect, useRef, createContext } from 'react';
import Header from './components/Header';
import Footer from './components/Footer';
import Vendor from './components/Vendor';
import Home from './components/Home';
import Courier from './components/Courier';
import Catalogue from './components/Catalogue';
import HotDeals from './components/HotDeals';
import Customer from './components/Customer';
import VendorDashBoard from './components/VendorDashBoard';
import CourierDashBoard from './components/CourierDashBoard';
import CustomerDashBoard from './components/CustomerDashBoard';
import VerifyPayment from './components/VerifyPayment';
import RegisterVendor from './components/RegisterVendor';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
// import { useHistory } from 'react-router-dom';
import './components/styles/bootstrap.min.css';
// import './components/styles/bootstrap';
// import './components/styles/jquery-3.6.0';
// import {bootstrap} from 'bootstrap';
// import {jquery} from 'jquery';
import RegisterCourier from './components/RegisterCourier';
import RegisterCustomer from './components/RegisterCustomer';
import ProdDetailPage from './components/ProdDetailPage';
import CartCheckOut from './components/CartCheckOut';
import Payment from './components/Payment';
import SingleProdCheckOutPage from './components/SingleProdCheckOutPage';
// import { UseFetchJSON } from './components/useFetch'

function App () { 
  // document.body.style.backgroundColor = 'silver';
  // document.style.backgroundColor = 'gray';


  
  let parcelCart = JSON.parse(localStorage.getItem('parcelCart')) || null;
 
 let calcCart = useCallback(()=>{
  if(parcelCart) {
      let totItem = 0;
      for (let i=0; i<parcelCart.length; i++) {
          let num = parcelCart[i].purchased_qty;
          totItem += num;
      }
      console.log(totItem);
      let cartTot = {"totItem":totItem};
      localStorage.setItem('cartTot', JSON.stringify(cartTot));
  } 
 },[parcelCart]); 

 useEffect(()=>{
     calcCart();
 },[calcCart, parcelCart]);
 

  return (
    <div className="main-body">
      <Router>
        <Header />
          <Switch>
            <Route exact path="/home">
              <Home/>
            </Route>
            <Route exact path="/">
              <Home/>
            </Route>
            <Route exact path="/vendor">
              <Vendor/>
            </Route>
            <Route exact path="/courier">
              <Courier/>
            </Route>
            <Route exact path="/catalogue">
              <Catalogue/>
            </Route>
            <Route exact path="/hot-deals">
              <HotDeals/>
            </Route>
            <Route exact path="/customer">
              <Customer/>
            </Route>
            <Route exact path="/register-vendor">
              <RegisterVendor/>
            </Route>
            <Route exact path="/register-courier">
              <RegisterCourier/>
            </Route>
            <Route exact path="/register-customer">
              <RegisterCustomer/>
            </Route>
            <Route exact path="/courier-dash">
              <CourierDashBoard/>
            </Route>
            <Route exact path="/vendor-dash">
              <VendorDashBoard/>
            </Route>
            <Route exact path="/customer-dash">
              <CustomerDashBoard/>
            </Route>
            <Route exact path="/cart-check">
              <CartCheckOut/>
            </Route>
            <Route exact path="/single">
              <SingleProdCheckOutPage/>
            </Route>
            <Route exact path="/payment">
              <Payment/>
            </Route>
            <Route exact path="/verify">
              <VerifyPayment/>
            </Route>
            <Route exact path="/prod-detail">
              <ProdDetailPage/>
            </Route>
          </Switch>
        <Footer/>
      </Router>
    </div>
  );
}

export default App