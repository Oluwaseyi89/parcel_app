import { useEffect, useCallback, useRef, useContext, useState } from 'react';
import { Link, useHistory } from 'react-router-dom';
import './styles/home.css';
// import { Link } from 'react-router-dom';
// import CSRFTOKEN from './CSRFTOKEN';
// import { useState } from 'react';
// import { UseFetch } from './useFetch';


const ProdDetailPage = () => {

    // let history = useHistory();
    let prodView = JSON.parse(localStorage.getItem('prodView'));
    const [proderr, setProdErr] = useState("");
    const [prodsus, setProdSus] = useState("");

    let [prodqty, setProdQty] = useState(0);

    let refresh = () => {
        window.location.reload(false);
    }

    let history = useHistory();

    // setInterval(() => {
    //     refresh();
    // }, 10000);

    const incrementProd = () => {
        // if (prodqty === isNaN) setProdQty(0);
        setProdQty(parseInt(prodqty) + 1)
    }

    const decrementProd = () => {
        // if (prodqty === isNaN) setProdQty(0);
        if (prodqty > 0) setProdQty(parseInt(prodqty) - 1);
        else setProdQty(0);
    }

    const handleBuyNow = () => {
        if(prodView) {
            let buySingle = {...prodView, purchased_qty:parseInt(prodqty)}
            localStorage.setItem('buySingle', JSON.stringify(buySingle));
            history.push('/single');

        }
    }

    const handleRemoveFromCart = () => {
        let cartStored = JSON.parse(localStorage.getItem('parcelCart')) || null;
        if(cartStored) {
            let prodRemoved =cartStored.filter((prod)=> prod.id === prodView.id);
            if (prodRemoved.length === 1) {
                let prodToReturn = cartStored.filter((prod)=> prod.id !== prodView.id);
                localStorage.setItem('parcelCart', JSON.stringify(prodToReturn));
                setProdSus('Product Removed!');
                setProdQty(0);
            } else setProdErr('Product was not in cart.');
        }
        refresh();
    }

    const handleAddToCart = async () => {
        let cart = [];
        let cartStored = await JSON.parse(localStorage.getItem('parcelCart')) || null;
        if (cartStored) {
            // for (let i=0; i<cartStored.length; i++) {
            //     cart.push(cartStored[i]);
            // }
            let availProd = await cartStored.filter((item)=> item.id === prodView.id);
            // let adjCart = await cartStored.map((item)=>{
            //     if (item.id === prodView.id) {
            //         if (prodqty) return {...item, purchased_qty:prodqty};
            //         else return null;
            //     } 
            //     console.log(item);
            //     return 
            // });

            console.log(availProd);
            // console.log(adjCart);

            let normCart = await cartStored.filter((item) => item.id !== prodView.id);
            console.log(normCart);

            if (normCart.length < cartStored.length) {
                if(availProd && prodqty>0) {
                    cart = [];
                    let repProd = availProd[0];
                    repProd = {...repProd, purchased_qty:parseInt(prodqty)}
                    cart.push(repProd);
                    if(normCart) cart.push(...normCart);
                    console.log(cart);
                    // localStorage.removeItem('parcelCart');
                    localStorage.setItem('parcelCart', JSON.stringify(cart));
                    setProdSus(`${prodView.prod_name} added to cart.`);
                } else {
                    setProdErr('Can not add zero qty to cart');
                }
            } else {
                if(prodqty>0) {
                    cart = [];
                    cart.push(...normCart);
                    cart.push({...prodView, purchased_qty:parseInt(prodqty)});
                    // localStorage.removeItem('parcelCart');
                    localStorage.setItem('parcelCart', JSON.stringify(cart));
                    setProdSus(`${prodView.prod_name} added to cart.`);
                } else {
                    setProdErr('Can not add zero qty to cart');
                }
            }

        } else {
            if (prodqty>0) {
                cart.push({...prodView, purchased_qty:parseInt(prodqty)});
                localStorage.setItem('parcelCart', JSON.stringify(cart));
                setProdSus(`${prodView.prod_name} added to cart.`);
            } else {
                setProdErr('Can not add zero qty to cart');
            }
        }
        refresh();
    }

  
       
    useEffect(()=>{
        let updateQty = JSON.parse(localStorage.getItem('parcelCart')) || null;
        if(updateQty) {
            for (let i=0; i<updateQty.length; i++) {
                if(updateQty[i].id === prodView.id) {
                    setProdQty(updateQty[i].purchased_qty);
                }
            }
        }
    },[prodView.id]);

    // let parcelCart = JSON.parse(localStorage.getItem('parcelCart')) || null;
 
    // let calcCart = useCallback(()=>{
    //  if(parcelCart) {
    //      let totItem = 0;
    //      for (let i=0; i<parcelCart.length; i++) {
    //          let num = parcelCart[i].purchased_qty;
    //          totItem += num;
    //      }
    //      console.log(totItem);
    //      let cartTot = {"totItem":totItem};
    //      localStorage.setItem('cartTot', JSON.stringify(cartTot));
    //  } 
    // },[parcelCart]); 
   
    // useEffect(()=>{
    //     calcCart();
    // },[calcCart, parcelCart]);
    
   
   
    return (
        <div className="container"> <br/>
             {proderr?(<div id='apiAlert' style={{"height": "50px", "textAlign": "center"}} className='alert alert-danger alert-dismissible' role='alert'>
                                    {proderr}
                                    <button className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                    </div>): "" }
             {prodsus?(<div id='apiAlert' style={{"height": "50px", "textAlign": "center"}} className='alert alert-success alert-dismissible' role='alert'>
                                    {prodsus}
                                    <button className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                    </div>): "" }
            <div className='view-detail'>
                <img alt='avatar' className='img-thumbnail' src={prodView.prod_photo} />
                <div className='view-content'>
                            <p>{prodView.prod_desc}</p>
                    <div className='prod-detail-ven-con'>
                        <div className='prod-det'>
                            <p className='badge'>{prodView.prod_name}</p> <br/>
                            <p className='badge'>{prodView.prod_model}</p> <br/>
                            <p className='badge'>Rating:</p> <br/>
                            <p className='badge'>₦ {prodView.prod_price}</p>
                        </div>
                        <div className='vend-img-con'>
                            <p className='badge'>Vendor</p>
                            <img alt='avatar' style={{width:"100%", height:"100%"}} src={prodView.vend_photo}/>
                            <p className='badge'>{prodView.vendor_name}</p>
                        </div>
                    </div>
                    <div className='prod-qty'>
                        <label className='badge'>Product Quantity</label> <br/>
                        <div className='incr-btn'>
                            <button style={{color:"chartreuse", fontSize:"1.3em"}}  onClick={decrementProd}  className='btn'>-</button> 
                            <input type='text' onChange={(e)=>setProdQty(e.target.value)} value={prodqty}/> 
                            <button style={{color:"chartreuse", fontSize:"1.3em"}}  onClick={incrementProd}  className='btn'>+</button> <br/>
                        </div>
                    </div> <br/>
                    <div className='view-bottom'> 
                        <div className='check-btn'>
                            <button onClick={handleBuyNow} className='btn'>Buy Now</button>
                            <button onClick={handleAddToCart} className='btn'> Add to Cart </button>
                            <button onClick={handleRemoveFromCart} className='btn'>Remove from Cart</button>
                        </div>
                    </div>      
                </div> 
                  
            </div>
        </div>     
    );
}

export default ProdDetailPage