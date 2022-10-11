import './styles/vendor.css';
// import { Link } from 'react-router-dom';
// import CSRFTOKEN from './CSRFTOKEN';
import { useState, useEffect } from 'react';
import { UseFetchJSON } from './useFetch';
// import { UseFetch } from './useFetch';


const VerifyPayment = () => {

    const [proderr, setProdErr] = useState("");
    const [prodsus, setProdSus] = useState("");

    let payRef = localStorage.getItem('payRef') || null;

    console.log(payRef);

    useEffect(() => {
        if(payRef) {
            let verUrl = `http://localhost:8080/v1/verifypayment/${payRef}`;

            let verData = UseFetchJSON(verUrl, 'GET');

            verData.then((res) => {
                if (res === true) setProdSus("Payment successfully verified.");
                else setProdErr("Your payment is yet to be verified, refresh browser to resolve");
                // if (res) setProdSus(res);
                // else setProdErr(res);
            }).catch((err) => console.log(err.message));
        }
    },[payRef]);

    
   
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
        </div>     
    );
}

export default VerifyPayment