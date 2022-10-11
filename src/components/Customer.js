import './styles/courier.css';
import { Link, useHistory } from 'react-router-dom';
import { useState } from 'react';
import { UseFetchJSON } from './useFetch';



const Customer = () => {

    const [cuscred, setCusCred] = useState({
        "email": "",
        "password": ""
    });
    const history = useHistory();
    
    // const [logvend, setLogVend] = useState(null);
    const [logcusalert, setLogCusAlert] = useState();
    const [logcus, setLogCus] = useState({"first_name": ""});
    const [showreset, setShowReset] = useState(false);
    const [resetEmail, setResetEmail] = useState("");
    const [logsus, setLogSus] = useState();


   

    const {email, password} = cuscred;

    const handleCusCredChange = (e) => {
        let name = e.target.name;
        let value = e.target.value;
        setCusCred({...cuscred, [name]:value});
        setShowReset(false);
    }  


    const handleResetPassword = (e) => {
        e.preventDefault();
        let data = {"email": resetEmail}
        let apiURL = "http://localhost:7000/parcel_customer/customer_resetter/"
        if (resetEmail === "") {
            setLogCusAlert("Enter your email for resetting the password");
        } else {
            let result = UseFetchJSON(apiURL, 'POST', data);
            result.then((res)=>{
                if (res.status === "success") {
                    setLogSus(res.data);
                } else if (res.status === "error") {
                    setLogCusAlert(res.data);
                } else {
                    setLogCusAlert("An error occured!");
                }
            }).catch((err) => console.log(err));
        }
    }


    const handleCusCredLogin = (e) => {
        e.preventDefault();
        setLogCusAlert("");
        if (email && password) {
            let apiURL = "http://localhost:7000/parcel_customer/customer_login/";
            let result = UseFetchJSON(apiURL, 'POST', cuscred);
            result.then((res) => {
                if (res.data.email === email) {
                setLogCus(res.data)
                    if (logcus.first_name !== "") {
                        localStorage.setItem('logcus', JSON.stringify(logcus));
                        history.push('/customer-dash');
                    }
                } else {
                    if (res.status === "password-error") {
                        setShowReset(true);
                        setLogCusAlert(res.data);
                    }
                    setLogCusAlert(res.data);
                }
            }).catch((err)=>console.log(err));
        } else {
            setLogCusAlert("Some fields are empty");
        }
    }

    return (
        <div className="Vendor-Frag">
            <form onSubmit={handleCusCredLogin} name="vendor-log-form" className="container">
                <div className="input-container">
                    <legend className="Vendor-Log-Legend" >Customer Login</legend> <br/>
                    {logcusalert ? <div className='alert alert-danger alert-dismissible' role='alert'>
                        <p style={{"textAlign": "center"}}>{logcusalert}</p>
                        <button className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                    </div>:""} 
                    {logsus ? <div className='alert alert-success alert-dismissible' role='alert'>
                        <p style={{"textAlign": "center"}}>{logsus}</p>
                        <button className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                    </div>:""}<br/>
                    <div className="form-group">
                        <input name="email" value={email} onChange={handleCusCredChange} type="email" placeholder="Enter Email" className="form-control" />
                    </div>
                    <div className="form-group">
                        <input name="password" value={password} onChange={handleCusCredChange} type="password" placeholder="Enter Password" className="form-control" />
                    </div>
                        <input className="vendor-log-submit btn" name="vendor-log-submit" type="submit" value="Login" />
                        <br/><Link style={{"color":"rgb(219, 33, 76)", "fontWeight":"bold"}} to="/register-customer">Register Instead</Link>
                </div>
            </form>
            {showreset ? (<form name='password-reset' className='container'>
                <legend className='Vendor-Log-Legend'>Reset Password</legend>
                <div className='form-group'>
                    <input name='resetEmail' value={resetEmail} onChange={(e)=>setResetEmail(e.target.value)} type='text' className='form-control' placeholder='Your Email'/>
                </div>
                    <input onClick={handleResetPassword} style={{"backgroundColor": "rgb(219, 33, 76"}} type='submit' className='btn' value="Submit" />
            </form>) : ""}  
        </div>     
    );
}

export default Customer