import './styles/vendor.css';
import { Link } from 'react-router-dom';
import { UseFetchJSON } from './useFetch';
import { useState } from 'react';
// import CSRFTOKEN from './CSRFTOKEN';



const RegisterCustomer = () => {

    const [dupPass, setDupPass] = useState("");
    const [cusalert, setCusAlert] = useState("");
    const [cussuccess, setCusSuccess] = useState("");
    const [customer, setCustomer] = useState({
        "first_name": "",
        "last_name": "",
        "country": "",
        "state": "",
        "street": "",
        "phone_no": "",
        "email": "",
        "password": "",
        "reg_date": new Date().toISOString(),
        "is_email_verified": false
    });

    const { first_name, last_name, country, state, street, phone_no, email, password, is_email_verified } = customer;

    const handleCustomerChange = (e) => {
        let name = e.target.name;
        let value = e.target.value;
        setCustomer({...customer, [name]:value});
    }

    const handleCustomerSubmit = (e) => {
        e.preventDefault();
        if (first_name && last_name && country && state && street &&
            phone_no && email && password) {
                if (password === dupPass) {
                    let apiURL = "http://localhost:7000/parcel_customer/reg_customer/";
                    let result = UseFetchJSON(apiURL, "POST", customer);
                    result.then((res) => {
                        if (res.status === "success") {
                            setCusSuccess(res.data);
                        } else if (res.status === "error") {
                            setCusAlert(res.data);
                        }
                    }).catch((err) => console.log(err));

                } else {
                    setCusAlert("Passwords do not match");
                }
            } else {
                setCusAlert("Some Fields are empty");
            }
    }

    return (
        <div className="Vendor-Frag">
            <form onSubmit={handleCustomerSubmit} className="container">
                <div className="input-container">
                    <legend className="Vendor-Log-Legend" >Customer Registration Form</legend> <br/>
                    {cusalert ? <div className='alert alert-danger alert-dismissible' role='alert'>
                        <p style={{"textAlign": "center"}}>{cusalert}</p>
                        <button className='close' role='alert' data-dismiss='alert' ><span>&times;</span></button>
                    </div>: ""}
                    {cussuccess ? <div className='alert alert-success alert-dismissible' role='alert'>
                        <p style={{"textAlign": "center"}}>{cussuccess}</p>
                        <button className='close' role='alert' data-dismiss='alert' ><span>&times;</span></button>
                    </div> : ""}
                    <div className="form-group">
                        <input name="first_name" value={first_name} onChange={handleCustomerChange} type="text" placeholder="First Name Here" className="form-control" />
                    </div>
                    <div className="form-group">
                        <input name="last_name" value={last_name} onChange={handleCustomerChange} type="text" placeholder="Last Name Here" className="form-control" />
                    </div>
                    <div className="form-group">
                        <input name="country" value={country} onChange={handleCustomerChange} type="text" placeholder="Country" className="form-control" />
                    </div>
                    <div className="form-group">
                        <input name="state" value={state} onChange={handleCustomerChange} type="text" placeholder="State/Province" className="form-control" />
                    </div>
                    <div className="form-group">
                        <input name="street" value={street} onChange={handleCustomerChange} type="text" placeholder="Street Address" className="form-control" />
                    </div>
                    <div className="form-group">
                        <input name="phone_no" value={phone_no} onChange={handleCustomerChange} type="text" placeholder="Phone Number" className="form-control" />
                    </div>
                    <div className="form-group">
                        <input name="email" value={email} onChange={handleCustomerChange} type="email" placeholder="Enter Email" className="form-control" />
                    </div>
                    <div className="form-group">
                        <input name="password" value={password} onChange={handleCustomerChange} type="password" placeholder="Enter Password" className="form-control" />
                    </div>
                    <div className="form-group">
                        <input name="retype-password" value={dupPass} onChange={(e)=>setDupPass(e.target.value)} type="password" placeholder="Retype Password" className="form-control" />
                    </div>
                        <input className="vendor-log-submit btn" name="vendor-log-submit" type="submit" value="Register" />
                        <br/><Link style={{"color":"rgb(219, 33, 76)", "fontWeight":"bold"}} to="/customer">Login Instead</Link>
                </div>
            </form>
        </div>     
    );
}

export default RegisterCustomer