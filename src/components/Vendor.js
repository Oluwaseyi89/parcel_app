import './styles/vendor.css';
import { Link, useHistory } from 'react-router-dom';
import { useState } from 'react';
import { UseFetchJSON } from './useFetch';
// import VendorDashBoard from './VendorDashBoard';
// import CSRFTOKEN from './CSRFTOKEN';


// const VendLogContext = createContext();

const Vendor = () => {

    const [vendcred, setVendCred] = useState({
        "email": "",
        "password": ""
    });
    const history = useHistory();
    
    // const [logvend, setLogVend] = useState(null);
    const [logalert, setLogAlert] = useState();
    const [logsus, setLogSus] = useState();
    const [logvend, setLogVend] = useState({"first_name": ""});
    const [showreset, setShowReset] = useState(false);
    const [resetEmail, setResetEmail] = useState("");

   

    const {email, password} = vendcred;

    const handleVendCredChange = (e) => {
        let name = e.target.name;
        let value = e.target.value;
        setVendCred({...vendcred, [name]:value});
        setShowReset(false);
    }  

    const handleResetPassword = (e) => {
        e.preventDefault();
        let data = {"email": resetEmail}
        let apiURL = "http://localhost:7000/parcel_backends/vendor_resetter/"
        if (resetEmail === "") {
            setLogAlert("Enter your email for resetting the password");
        } else if(resetEmail !== email) {
            setLogAlert("Wrong Email Entered");
        } else {
            let result = UseFetchJSON(apiURL, 'POST', data);
            result.then((res)=>{
                if (res.status === "success") {
                    setLogSus(res.data);
                } else if (res.status === "error") {
                    setLogAlert(res.data);
                } else {
                    setLogAlert("An error occured!");
                }
            }).catch((err) => console.log(err));
        }
    }

    const handleVendCredLogin = (e) => {
        e.preventDefault();
        setLogAlert("");
        if (email && password) {
            let apiURL = "http://localhost:7000/parcel_backends/vendor_login/";
            let result = UseFetchJSON(apiURL, 'POST', vendcred);
            result.then((res) => {
                if (res.data.email === email) {
                setLogVend(res.data)
                    if (logvend.first_name !== "") {
                        localStorage.setItem('logvend', JSON.stringify(logvend));
                        history.push('/vendor-dash');
                    }
                } else {
                    if (res.status === "password-error") {
                        setShowReset(true);
                        setLogAlert(res.data);
                    }
                    setLogAlert(res.data);
                }
            }).catch((err)=>console.log(err));
        } else {
            setLogAlert("Some fields are empty");
        }
    }

    return (
        <div className="Vendor-Frag">

            <form onSubmit={handleVendCredLogin} name="vendor-log-form" className="container">
                <div className="input-container">
                    <legend className="Vendor-Log-Legend" >Vendor Login</legend> <br/>
                    {logalert ? <div className='alert alert-danger alert-dismissible' role='alert'>
                        <p style={{"textAlign": "center"}}>{logalert}</p>
                        <button className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                    </div>:""}
                    {logsus ? <div className='alert alert-success alert-dismissible' role='alert'>
                        <p style={{"textAlign": "center"}}>{logsus}</p>
                        <button className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                    </div>:""}
                    <div className="form-group">
                        <input name="email" value={email} onChange={handleVendCredChange} type="email" placeholder="Enter Email" className="form-control" />
                    </div>
                    <div className="form-group">
                        <input name="password" value={password} onChange={handleVendCredChange} type="password" placeholder="Enter Password" className="form-control" />
                    </div>
                        <input className="vendor-log-submit btn" name="vendor-log-submit" type="submit" value="Login" />
                        <br/><Link style={{"color":"rgb(219, 33, 76)", "fontWeight":"bold"}} to="/register-vendor">Register Instead</Link>
                </div>
            </form> <br/> <br/>
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

export default Vendor