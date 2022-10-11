import './styles/vendor.css';
import { Link } from 'react-router-dom';
// import CSRFTOKEN from './CSRFTOKEN';
import { useState } from 'react';
import { UseFetch } from './useFetch';



const RegisterCourier = () => {

    const [courphoto, setCourPhoto] = useState(null);


    const [courier, setCourier] = useState({
        "first_name": "",
        "last_name": "",
        "bus_country": "",
        "bus_state": "",
        "bus_street": "",
        "cac_reg_no": "",
        "nin": "",
        "phone_no": "",
        "email": "",
        "password": "",
        "cour_policy": false
  });  

  const {first_name, last_name, bus_country, bus_state, 
  bus_street, cac_reg_no, nin, phone_no, email, password, cour_policy} = courier;

  const $ = window.$;
  const handleCourPhoto = (e) => {
    setCourPhoto(e.target.files[0]);
    let tmp_path = URL.createObjectURL(e.target.files[0]);
    $("#tar-cour-img").fadeIn("slow").attr('src', tmp_path);
}
    const courRegUrl = 'http://localhost:7000/parcel_backends/reg_temp_cour/'; 
    const handleCourChange = (e) => {
        let name = e.target.name;
        let value = e.target.value;
        let checked = e.target.checked;
        setCourier({...courier, [name]:value, cour_policy:checked});
    }

    console.log(cour_policy);

    const [courAlert, setCourAlert] = useState('');
    const [courSuccess, setCourSuccess] = useState('');

    courAlert && setTimeout(() => {
        setCourAlert('');
    }, 10000);
    
    courSuccess && setTimeout(() => {
        setCourSuccess('');
    }, 10000);
    
   

    // $('#cour-img').change( async (e)=> {
    //     let tmp_path = await URL.createObjectURL(e.target.files[0]);
    //     // console.log(tmp_path);
    //     // let full_path = $("#vend-img").val();
    //     // let base_name = full_path.split('\\').pop().split('/').pop();
    //     // console.log(base_name);
    //     $("#tar-cour-img").fadeIn("slow").attr('src', tmp_path);
    //     // let path_start = "http://localhost:5000/static/dxlApp/Product_Image/";
    //     // $("#img_path").html(tmp_path + "<br/>" + path_start + base_name);
    // });

    const [duPass, setDupass] = useState('');


    const handleSubmitCour = (e) => {
        e.preventDefault();
    
        if(first_name && last_name && bus_country && bus_state && courphoto
            && bus_street && cac_reg_no && nin && phone_no && email && password) {
                const formData = new FormData();
                let full_path = $("#cour-img").val();
                let base_name = full_path.split('\\').pop().split('/').pop();
                console.log(base_name);
                    formData.append('first_name', first_name);
                    formData.append('last_name', last_name);
                    formData.append('bus_country', bus_country);
                    formData.append('bus_state', bus_state);
                    formData.append('bus_street', bus_street);
                    formData.append('cac_reg_no', cac_reg_no);
                    formData.append('nin', nin);
                    formData.append('phone_no', phone_no);
                    formData.append('email', email);
                    formData.append('password', password);
                    formData.append('cour_photo', courphoto, base_name);
                    formData.append('cour_policy', cour_policy);
                    formData.append('reg_date', new Date().toISOString());
                    formData.append('is_email_verified', false);

                    console.log(formData.get('bus_street'));
                    console.log(email);

                if(password === duPass){
                    let data = UseFetch(courRegUrl, 'POST', formData);
                    data.then((res)=> {
                        if (res.status === "success") {
                            setCourSuccess(res.data);
                        } else if (res.status === 'error') {
                            setCourAlert(res.data);
                        }
                    });
                        // {if (res.status == 'OK') { setApiAlert(res.data.first_name + ', has been registered.')}});
                    // }
                } else {
                    setCourAlert('Passwords do not match');
                }
            } else {
                setCourAlert('Some fields are blank');
            }
    }

    return (
        <div className="Vendor-Frag">
            <form onSubmit={handleSubmitCour} name="vendor-log-form" className="container">
                <div className="input-container">
                    <legend className="Vendor-Log-Legend" >Courier Registration Form</legend> <br/>
                    {courAlert?(<div id='apiAlert' style={{"height": "50px", "textAlign": "center"}} className='alert alert-danger alert-dismissible' role='alert'>
                                    {courAlert}
                                    <button id='closeApi' className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                                </div>): ""}
                               {courSuccess?(<div  style={{"height": "50px", "textAlign": "center"}} className='alert alert-success alert-dismissible' role='alert'>
                                    {courSuccess}
                                    <button  className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                                </div>): ""}
                            <div className='photo-container'>
                                   <div className='upload-label'><label htmlFor='cour_photo'>Upload Photo</label> <input name="cour_photo" onChange={handleCourPhoto} id='cour-img' type="file" /></div> <div  className='avatar-container'>{courphoto && <img id='tar-cour-img' className='img-thumbnail' src=''/>}</div>
                            </div> 
                            <hr/>
                    <div className="form-group">
                        <input name="first_name" value={first_name} onChange={handleCourChange} type="text" placeholder="First Name Here" className="form-control" />
                    </div>
                    <div className="form-group">
                        <input name="last_name" value={last_name} onChange={handleCourChange} type="text" placeholder="Last Name Here" className="form-control" />
                    </div>
                    <div className="form-group">
                        <input name="bus_country" value={bus_country} onChange={handleCourChange}  type="text" placeholder="Business Country" className="form-control" />
                    </div>
                    <div className="form-group">
                        <input name="bus_state" value={bus_state} onChange={handleCourChange} type="text" placeholder="Business State/Province" className="form-control" />
                    </div>
                    <div className="form-group">
                        <input name="bus_street" value={bus_street} onChange={handleCourChange} type="text" placeholder="Business Street Address" className="form-control" />
                    </div>
                    <div className="form-group">
                        <input name="cac_reg_no" value={cac_reg_no} onChange={handleCourChange} type="text" placeholder="Business CAC Reg. No." className="form-control" />
                    </div>
                    <div className="form-group">
                        <input name="nin" value={nin} onChange={handleCourChange} type="text" placeholder="National Identity Number (NIN)" className="form-control" />
                    </div>
                    <div className="form-group">
                        <input name="phone_no" value={phone_no} onChange={handleCourChange} type="text" placeholder="Phone Number" className="form-control" />
                    </div>
                    <div className="form-group">
                        <input name="email" value={email} onChange={handleCourChange} type="email" placeholder="Enter Email" className="form-control" />
                    </div>
                    <div className="form-group">
                        <input name="password" value={password} onChange={handleCourChange} type="password" placeholder="Enter Password" className="form-control" />
                    </div>
                    <div className="form-group">
                        <input name="retype-password" value={duPass} onChange={(e) => setDupass(e.target.value)} type="password" placeholder="Retype Password" className="form-control" />
                    </div>
                    <div className="check-policy">
                        <label htmlFor='cour_policy'>Check to Accept</label> 
                        <input name="cour_policy"  checked={cour_policy} onChange={handleCourChange} type="checkbox" />
                        <Link to='courier-policy'>Read Courier Policy</Link>
                    </div>
                    <hr/>
                        <input className="vendor-log-submit btn" name="vendor-log-submit" type="submit" value="Register" />
                        <br/><Link style={{"color":"rgb(219, 33, 76)", "fontWeight":"bold"}} to="/courier">Login Instead</Link>
                </div>
            </form>
        </div>     
    );
}

export default RegisterCourier