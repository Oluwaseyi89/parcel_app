import './styles/vendor.css';
import { Link } from 'react-router-dom';
// import CSRFTOKEN from './CSRFTOKEN';
import { useState } from 'react';
import { UseFetch } from './useFetch';


const RegisterVendor = () => {
    
    const [apiAlert, setApiAlert] = useState('');
    const [apiSuccess, setApiSuccess] = useState('');
    const [photo, setPhoto] = useState(null);


    const handlePhoto = (e) => {
        let tmp_path = URL.createObjectURL(e.target.files[0]);
        $("#target-img").fadeIn("slow").attr('src', tmp_path);
        setPhoto(e.target.files[0]);
    }

    console.log(photo);
    const [vendor, setVendor] = useState({
        "first_name": "",
        "last_name": "",
        "bus_country": "",
        "bus_state": "",
        "bus_street": "",
        "bus_category": "",
        "cac_reg_no": "",
        "nin": "",
        "phone_no": "",
        "email": "",
        "password": "",
        "ven_policy": false
  }); 

  let {first_name, last_name, bus_country, bus_state, bus_street, 
    bus_category, cac_reg_no, nin, phone_no, email, password, ven_policy} = vendor;
    
// useEffect(() => {
    
// },[]);

const venRegUrl = 'http://localhost:7000/parcel_backends/reg_temp_ven/'; 


// let handleAlert = () => {
//     let displayAlert = document.getElementById('apiAlert');
//     displayAlert.style.display = 'none'; 
// }

const $ = window.$;

// const handlePhotoChange = (e)=> {
//     let tmp_path = URL.createObjectURL(e.target.files[0]);
//     // console.log(tmp_path);
//     // let full_path = $("#vend-img").val();
//     // let base_name = full_path.split('\\').pop().split('/').pop();
//     // console.log(base_name);
//     $("#target-img").fadeIn("slow").attr('src', tmp_path);
//     // let path_start = "http://localhost:5000/static/dxlApp/Product_Image/";
//     // $("#img_path").html(tmp_path + "<br/>" + path_start + base_name);
// }

const handleVendChange = (e) => {
    // let photo = e.target.files[0];
    let name = e.target.name;
    let value = e.target.value;
    let checked = e.target.checked;
    setVendor({...vendor, [name]:value, ven_policy:checked});
}


// console.log(formData.get('vend_photo'));



const [repass, setRepass] = useState('');


apiAlert && setTimeout(() => {
    setApiAlert('');
}, 10000);

apiSuccess && setTimeout(() => {
    setApiSuccess('');
}, 10000);

const handleSubmitVend = (e) => {
    e.preventDefault();
    
    if(first_name && last_name && bus_category && bus_country && bus_state && photo
        && bus_street && cac_reg_no && nin && phone_no && email && password && ven_policy) {
            const formData = new FormData();
            let full_path = $("#vend-img").val();
            let base_name = full_path.split('\\').pop().split('/').pop();
            console.log(base_name);
                formData.append('first_name', first_name);
                formData.append('last_name', last_name);
                formData.append('bus_country', bus_country);
                formData.append('bus_state', bus_state);
                formData.append('bus_street', bus_street);
                formData.append('bus_category', bus_category);
                formData.append('cac_reg_no', cac_reg_no);
                formData.append('nin', nin);
                formData.append('phone_no', phone_no);
                formData.append('email', email);
                formData.append('password', password);
                formData.append('vend_photo', photo, base_name);
                formData.append('ven_policy', ven_policy);
                formData.append('reg_date', new Date().toISOString());
                formData.append('is_email_verified', false);
            if(password === repass){
                let data = UseFetch(venRegUrl, 'POST', formData);
                data.then((res)=> {
                    if (res.status === "success") {
                        setApiSuccess(res.data);
                    } else if (res.status === 'error') {
                        setApiAlert(res.data);
                    }
                }).catch((err) => console.log(err.message));

                    // {if (res.status == 'OK') { setApiAlert(res.data.first_name + ', has been registered.')}});
                // }
            } else {
                setApiAlert('Passwords do not match');
            }
        } else {
            setApiAlert('Some fields are blank');
        }
}

    return (
        <div className="Vendor-Frag">
            <form name="vendor-log-form" onSubmit={handleSubmitVend} className="container">
                <div className="input-container">
                    <legend className="Vendor-Log-Legend" >Vendor Registration Form</legend> <br/>
                               {apiAlert?(<div id='apiAlert' style={{"height": "50px", "textAlign": "center"}} className='alert alert-danger alert-dismissible' role='alert'>
                                    {apiAlert}
                                    <button id='closeApi' className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                                </div>): "" }
                               {apiSuccess?(<div  style={{"height": "50px", "textAlign": "center"}} className='alert alert-success alert-dismissible' role='alert'>
                                    {apiSuccess}
                                    <button  className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                                </div>): ""}
                             <div className='photo-container'>
                                   <div className='upload-label'><label htmlFor='vend_photo'>Upload Photo</label> <input name="vend_photo" onChange={handlePhoto} id='vend-img' type="file" /></div> <div className='avatar-container'>{photo && <img id='target-img' alt='avatar' className='img-thumbnail' src=''/>}</div>
                            </div> 
                            <hr/>
                    <div className="form-group">
                        <input name="first_name" onChange={handleVendChange} value={first_name} type="text" placeholder="First Name Here" className="form-control" />
                    </div>
                    <div className="form-group">
                        <input name="last_name"  onChange={handleVendChange} value={last_name} type="text" placeholder="Last Name Here" className="form-control" />
                    </div>
                    <div className="form-group">
                        <input name="bus_country"  onChange={handleVendChange} value={bus_country} type="text" placeholder="Business Country" className="form-control" />
                    </div>
                    <div className="form-group">
                        <input name="bus_state"  onChange={handleVendChange} value={bus_state} type="text" placeholder="Business State/Province" className="form-control" />
                    </div>
                    <div className="form-group">
                        <input name="bus_street"  onChange={handleVendChange} value={bus_street} type="text" placeholder="Business Street Address" className="form-control" />
                    </div>
                    <div className="form-group">
                        <select name="bus_category"  onChange={handleVendChange} value={bus_category} className="form-control">
                            <option>Pick_Business_Category</option>
                            <option>Chemicals</option>
                            <option>Clothing</option>
                            <option>Educative_Materials</option>
                            <option>Electronics</option>
                            <option>Furniture</option>
                            <option>General_Merchandise</option>
                            <option>Kitchen_Utensils</option>
                            <option>Plastics</option>
                            <option>Spare_Parts</option>
                        </select>
                    </div>
                    <div className="form-group">
                        <input name="cac_reg_no"  onChange={handleVendChange} value={cac_reg_no} type="text" placeholder="Business CAC Reg. No." className="form-control" />
                    </div>
                    <div className="form-group">
                        <input name="nin"  onChange={handleVendChange} value={nin} type="text" placeholder="National Identity Number (NIN)" className="form-control" />
                    </div>
                    <div className="form-group">
                        <input name="phone_no"  onChange={handleVendChange} value={phone_no} type="text" placeholder="Phone Number" className="form-control" />
                    </div>
                    <div className="form-group">
                        <input name="email"  onChange={handleVendChange} value={email} type="email" placeholder="Enter Email" className="form-control" />
                    </div>
                    <div className="form-group">
                        <input name="password"  onChange={handleVendChange} value={password} type="password" placeholder="Enter Password" className="form-control" />
                    </div>
                    <div className="form-group">
                        <input name="retype-password" value={repass} onChange={(e)=>setRepass(e.target.value)} type="password" placeholder="Retype Password" className="form-control" />
                    </div>
                    <div className="check-policy">
                        <label htmlFor='ven_policy'>Check to Accept</label> 
                        <input name="ven_policy"  checked={ven_policy} onChange={handleVendChange} type="checkbox" />
                        <Link to='vendor-policy'>Read Vendor Policy</Link>
                    </div>
                    <hr/>
                        <input className="vendor-log-submit btn" name="vendor-log-submit" type="submit" value="Register" />
                        <br/><Link style={{"color":"rgb(219, 33, 76)", "fontWeight":"bold"}} to="/vendor">Login Instead</Link>
                </div>
            </form>
        </div>     
    );
}

export default RegisterVendor