import './styles/courier.css';
import { Link, useHistory } from 'react-router-dom';
import { useState } from 'react';
import { UseFetchJSON } from './useFetch';



const Courier = () => {

    const [courcred, setCourCred] = useState({
        "email": "",
        "password": ""
    });
    const history = useHistory();
    
    // const [logvend, setLogVend] = useState(null);
    const [logcouralert, setLogCourAlert] = useState();
    const [logcour, setLogCour] = useState({"first_name": ""});
    const [showreset, setShowReset] = useState(false);
    const [resetEmail, setResetEmail] = useState("");
    const [logsus, setLogSus] = useState();

   

    const {email, password} = courcred;

    const handleCourCredChange = (e) => {
        let name = e.target.name;
        let value = e.target.value;
        setCourCred({...courcred, [name]:value});
        setShowReset(false);
    }  

    const handleResetPassword = (e) => {
        e.preventDefault();
        let data = {"email": resetEmail}
        let apiURL = "http://localhost:7000/parcel_backends/courier_resetter/"
        if (resetEmail === "") {
            setLogCourAlert("Enter your email for resetting the password");
        } else {
            let result = UseFetchJSON(apiURL, 'POST', data);
            result.then((res)=>{
                if (res.status === "success") {
                    setLogSus(res.data);
                } else if (res.status === "error") {
                    setLogCourAlert(res.data);
                } else {
                    setLogCourAlert("An error occured!");
                }
            }).catch((err) => console.log(err));
        }
    }

    const handleCourCredLogin = (e) => {
        e.preventDefault();
        setLogCourAlert("");
        if (email && password) {
            let apiURL = "http://localhost:7000/parcel_backends/courier_login/";
            let result = UseFetchJSON(apiURL, 'POST', courcred);
            result.then((res) => {
                if (res.data.email === email) {
                setLogCour(res.data)
                    if (logcour.first_name !== "") {
                        localStorage.setItem('logcour', JSON.stringify(logcour));
                        history.push('/courier-dash');
                    }
                } else {
                    if (res.status === "password-error") {
                        setShowReset(true);
                        setLogCourAlert(res.data);
                    }
                    setLogCourAlert(res.data);
                }
            }).catch((err)=>console.log(err));
        } else {
            setLogCourAlert("Some fields are empty");
        }
    }

    return (
        <div className="Vendor-Frag">
            <form onSubmit={handleCourCredLogin} name="vendor-log-form" className="container">
                <div className="input-container">
                    <legend className="Vendor-Log-Legend" >Courier Login</legend> <br/>
                    {logcouralert ? <div className='alert alert-danger alert-dismissible' role='alert'>
                        <p style={{"textAlign": "center"}}>{logcouralert}</p>
                        <button className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                    </div>:""} 
                    {logsus ? <div className='alert alert-success alert-dismissible' role='alert'>
                        <p style={{"textAlign": "center"}}>{logsus}</p>
                        <button className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                    </div>:""}<br/>
                    <div className="form-group">
                        <input name="email" value={email} onChange={handleCourCredChange} type="email" placeholder="Enter Email" className="form-control" />
                    </div>
                    <div className="form-group">
                        <input name="password" value={password} onChange={handleCourCredChange} type="password" placeholder="Enter Password" className="form-control" />
                    </div>
                        <input className="vendor-log-submit btn" name="vendor-log-submit" type="submit" value="Login" />
                        <br/><Link style={{"color":"rgb(219, 33, 76)", "fontWeight":"bold"}} to="/register-courier">Register Instead</Link>
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

export default Courier