import './styles/vendor.css';
import './styles/product.css';
import { useState } from 'react';
import { UseFetchJSON } from './useFetch';
// import { Link } from 'react-router-dom';
// import CSRFTOKEN from './CSRFTOKEN';
// import { useState } from 'react';
// import { UseFetch } from './useFetch';


const VendTransactions = () => {
    
    const [showbank, setShowBank] = useState(false);
    const [showtxn, setShowTxn] = useState(false);
    const [bankDetails, setBankDetails] = useState({
        "bank_name": "",
        "account_type": "",
        "account_name": "",
        "account_no": ""
    });

    const [banksus, setBankSus] = useState("");
    const [bankerr, setBankErr] = useState("");

    let logvend = JSON.parse(localStorage.getItem('logvend'));

    const {bank_name, account_type, account_name, account_no} = bankDetails;

    const handleBankDetailChange = (e) => {
        let name = e.target.name;
        let value = e.target.value;
        setBankDetails({...bankDetails, [name]:value});
    }

    const handleBankDetailSubmit = (e) => {
        e.preventDefault();
        if (bank_name && account_type && account_name && account_no) {
            let email = logvend.email;
            let bankData = {
                "bank_name": bank_name,
                "account_type": account_type,
                "account_name": account_name,
                "account_no": account_no,
                "vendor_email": email,
                "added_at": new Date().toISOString(),
                "updated_at": new Date().toISOString()
            }

            let apiUrl = "http://localhost:7000/parcel_backends/save_vend_bank/";
            let data = UseFetchJSON(apiUrl, 'POST', bankData);
            data.then((res) => {
                if (res.status === "success") {
                    setBankSus(res.data);
                } else if (res.status === "error") {
                    setBankErr(res.data);
                } else {
                    setBankErr('An error occured');
                }
            }).catch((err) => console.log(err));
        } else {
            setBankErr('Some fields are blank');
        }
    }


    const handleBankDetailUpdate = (e) => {
        e.preventDefault();
        if (bank_name && account_type && account_name && account_no) {
            let email = logvend.email;
            let bankData = {
                "bank_name": bank_name,
                "account_type": account_type,
                "account_name": account_name,
                "account_no": account_no,
                "updated_at": new Date().toISOString()
            }

            let apiUrl = `http://localhost:7000/parcel_backends/update_vend_bank/${email}`;
            let data = UseFetchJSON(apiUrl, 'PATCH', bankData);
            data.then((res) => {
                if (res.status === "success") {
                    setBankSus(res.data);
                } else if (res.status === "error") {
                    setBankErr(res.data);
                } else {
                    setBankErr('An error occured');
                }
            }).catch((err) => console.log(err));
        } else {
            setBankErr('Some fields are blank');
        }
    }

    console.log(bankDetails);

    const handleShowBank = () => {
        setShowBank(true);
        setShowTxn(false);
    }

    const handleShowTxn = () => {
        setShowBank(false);
        setShowTxn(true);
    }
   
    return (
        <div className="Vendor-Frag">
            <div className="button-container">
                <button onClick={handleShowBank}  className='btn prod-button'>Bank Details</button>
                <button onClick={handleShowTxn}  className='btn prod-button'>View Transactions</button>
            </div> <br/>
            {showbank?(<div>
                <form className='container form'>
                    <legend className='Vendor-Log-Legend'>Bank Update</legend> <br/>
                    {bankerr?(<div id='apiAlert' style={{"height": "50px", "textAlign": "center"}} className='alert alert-danger alert-dismissible' role='alert'>
                                    {bankerr}
                                    <button className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                    </div>): "" }
                    {banksus?(<div  style={{"height": "50px", "textAlign": "center"}} className='alert alert-success alert-dismissible' role='alert'>
                                    {banksus}
                                    <button  className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                    </div>): ""}
                    <div className='form-group'>
                        <input onChange={handleBankDetailChange} name='bank_name' value={bank_name} type='text' className='form-control' placeholder='Bank Name'/>
                    </div>
                    <div className='form-group'>
                        <select onChange={handleBankDetailChange} name='account_type' value={account_type} className='form-control'>
                            <option>Select Account Type</option>
                            <option>Savings</option>
                            <option>Current</option>
                        </select>
                    </div>
                    <div className='form-group'>
                        <input onChange={handleBankDetailChange} name='account_name' value={account_name} type='text' className='form-control' placeholder='Account Name'/>
                    </div>
                    <div className='form-group'>
                        <input onChange={handleBankDetailChange} name='account_no' value={account_no} type='text' className='form-control' placeholder='Account Number'/>
                    </div>
                    <div className='bank-btn-group'>
                        <button onClick={handleBankDetailSubmit} className='btn prod-button'>Save</button>
                        <button onClick={handleBankDetailUpdate} className='btn prod-button'>Update</button>
                    </div>
                </form>
            </div>):""}        
            {showtxn?(<div>
                <h4>Txn Details</h4>
            </div>):""}        
        </div>     
    );
}

export default VendTransactions