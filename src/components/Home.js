import './styles/home.css';
import { useCallback, useEffect, useReducer, createContext, useState } from 'react';
import { useHistory } from 'react-router-dom';
import { UseFetchJSON } from './useFetch';
import ProdDetailPage from './ProdDetailPage';



const Home = () => {
    let initialState = {
        products: []
    }

    let history = useHistory();
    // let [showProd, setShowProd] = useState(false);

    // const ProductContext = createContext();
    // let [prodView, setProdView] = useState();


   
    const getAllProds = useCallback((data)=>{
        return dispatch({
            type: "GET_ALL_PRODUCTS",
            payload: data
        })
    },[]);

    const prodDetail = useCallback((id)=>{
        return dispatch({
            type: "PROD_DETAIL",
            payload: id
        })
    },[]);

    let reducer = (state, action) => {
        if (action.type === "GET_ALL_PRODUCTS") {
            let adjData = action.payload;
            // let adjData = action.payload.map((prod) => {
            //     return {...prod, "edit": false}
            // });
            return {...state, products:adjData}   
        }

        if (action.type === "PROD_DETAIL") {
            state.products.map((prod)=>{
                if (prod.id === action.payload) {
                    // setProdView(prod);
                    localStorage.setItem('prodView', JSON.stringify(prod));
                    history.push('/prod-detail');
                }
                return null;
            });
        }

        return state;
    }

    useEffect(()=>{
        let apiUrl = "http://localhost:7000/parcel_product/get_prod/";
        let data = UseFetchJSON(apiUrl, 'GET');
        data.then((res)=>{
            getAllProds(res.data);
        });
    },[getAllProds]);

    const [state, dispatch] = useReducer(reducer, initialState);
    console.log(state);
    // console.log(prodView);

    return (
     <div className='container'> 
        <div className='prod-contain'>
            {state.products.map((prod, index)=>(
                <div key={index} className='prod-home-frag'>
                    <p  className="badge prod-name">{prod.prod_name}</p>
                    <p  className="prod-disc">-{40 + prod.prod_disc}%</p>
                    <img alt="prod-avatar" className="img-thumbnail prod-img" src={prod.prod_photo} />
                    <div className='well bottom-container'>
                        <p className='prod-desc well'>{prod.prod_model}</p>
                        <p className='prod-desc well'>{prod.prod_desc}</p>
                        <div className='prod-price-button'>
                            <p className='badge'>₦ {prod.prod_price}</p>
                            <button onClick={()=>prodDetail(prod.id)} className='btn'>View</button>
                        </div>
                    </div>
                </div>
            ))}  
        </div>    
     </div>
    );
}

export default Home