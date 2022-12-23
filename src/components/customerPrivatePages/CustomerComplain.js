import '../styles/customer.css';
import { useState, useReducer, useCallback, useEffect } from 'react';
import { UseFetchJSON } from '../useFetch';



const CustomerComplain = () => {

    const initialComplaints = {
        "complaints": []
    }

    let loggedCus = JSON.parse(localStorage.getItem('logcus')) || null;

    const [proderr, setProdErr] = useState("");
    const [prodsus, setProdSus] = useState("");
    const [shouldFetch, setShouldFetch] = useState(true);

    const [complaintForm, setComplaintForm] = useState({
        "customer_email": loggedCus.email,
        "complaint_subject": "",
        "courier_involved": "",
        "complaint_detail": "",
        "is_resolved": false,
        "is_satisfied": false,
        "created_at": new Date().toISOString(),
        "updated_at": new Date().toISOString()
    });

    const { complaint_subject, courier_involved, complaint_detail } = complaintForm;

    const handleComplaintFormChange = (e) => {
        let name = e.target.name;
        let value = e.target.value;
        setComplaintForm({ ...complaintForm, [name]:value });
    }

    console.log(complaintForm);

    const handleComplaintFormSubmission = (e) => {
        e.preventDefault();

        if(loggedCus && complaint_subject && courier_involved && complaint_detail) {

            let apiUrl = "http://localhost:7000/parcel_backends/customer_complain/";

            let apiOperation = UseFetchJSON(apiUrl, "POST", complaintForm);
            apiOperation.then((res) => {
                if(res.status === "success") {
                    setProdSus(res.data);
                    setShouldFetch(true);
                } else {
                    setProdErr(res.data);
                }
            }).catch((err) => console.log(err.message));

        } else {
            setProdErr("Enter the blank fields");
        }
    }

    const getComplaints = useCallback((data) => {
        return dispatch({
            type: "GET_COMPLAINTS",
            payload: data,
        });
      }, []);

    const updateComplaint = (id, e) => {
        return dispatch({
            type: "UPDATE_COMPLAINT",
            payload: {"id": id, "e": e}
        });
    }

    const reducer = (state=initialComplaints, action) => {

        if(action.type === "GET_COMPLAINTS") {      
            let filteredComplaints = action.payload.filter(complaint => complaint.is_satisfied === false); 
         return {...state, complaints: filteredComplaints} 
        }

        if(action.type === "UPDATE_COMPLAINT") {
            
            let modComplain = state.complaints.map((complain) => {
                if(complain.id === action.payload.id) {
                    let e = action.payload.e;
                    let id = action.payload.id;
                    let updateData = {
                        "is_satisfied": e.target.checked,
                        "updated_at": new Date().toISOString()
                    }
                    let apiUrl = `http://localhost:7000/parcel_backends/update_complain/${id}/`
                    let apiOperation = UseFetchJSON(apiUrl, "PATCH", updateData);
                    apiOperation.then((res) => {
                        if(res.status === "success") {
                            setProdSus(res.data);
                        } else {
                            setProdErr(res.data);
                        }
                    }).catch((err) => setProdErr(err.message));
                    return { ...complain, is_satisfied: e.target.checked  }
                } 
                return { ...complain }
            });

            return { ...state, complaints: modComplain }
        }

        return state;
    }

    useEffect(()=>{  
        
        function fetchComplains() {
            let apiUrl = `http://localhost:7000/parcel_backends/get_dist_complain/${loggedCus.email}/`; 
            let data = UseFetchJSON(apiUrl, 'GET');
            data.then((res) => {
                let complaints = res.data;
                getComplaints(complaints);
            }).catch((err) => console.log(err.message));
        }

        if(shouldFetch) fetchComplains(); 
        setShouldFetch(false);   
     
    },[loggedCus.email, getComplaints, shouldFetch]);

    const [state, dispatch] = useReducer(reducer, initialComplaints);
    console.log(state);




    return (
     <div className='container'>
         <form onSubmit={handleComplaintFormSubmission}>
            <br/>
            <legend className='Vendor-Log-Legend'>Complain Form</legend>
            {proderr?(<div id='apiAlert' style={{"height": "50px", "textAlign": "center"}} className='alert alert-danger alert-dismissible' role='alert'>
                                    {proderr}
                                    <button className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                    </div>): "" }
                    {prodsus?(<div  style={{"height": "50px", "textAlign": "center"}} className='alert alert-success alert-dismissible' role='alert'>
                                    {prodsus}
                                    <button  className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                    </div>): ""}
            <div className='form-group'>
                <input name='complaint_subject' value={complaint_subject} onChange={handleComplaintFormChange} className='form-control' type='text' placeholder='Complain Subject'/>
            </div>
            <div className='form-group'>
                <input name='courier_involved' value={courier_involved} onChange={handleComplaintFormChange} className='form-control' type='text' placeholder="Courier's Full Name"/>
            </div>
            <div className='form-group'>
               <textarea name='complaint_detail' value={complaint_detail} onChange={handleComplaintFormChange} style={{height: "200px"}} className='form-control' placeholder='Complain Details'></textarea>
            </div>
            <br/>
            <div className='form-group'>
              <input type='submit' value='Submit' className='complaint-btn btn'/>
            </div>
         </form>
            <br/>
            <hr/>
            <br/>
            <h4 className='Vendor-Log-Legend'>Submitted Complains</h4>
            <div>
                {state.complaints.map((complaint) => {
                    return (
                        <div className='complain-div'>
                            <div>
                                <ul className='complain-list'>
                                    <li style={{fontSize: "1.5em"}}><strong>{complaint.complaint_subject}</strong></li>
                                    <li>Courier: <strong style={{fontSize: "1.2em"}}>{complaint.courier_involved}</strong></li>
                                    <li>Status: <strong style={{fontSize: "1.2em", color: complaint.is_resolved ? "green" : "red"}}>{complaint.is_resolved ? "Resolved" : "Pending"}</strong></li>
                                </ul>                            
                            </div>
                            <div>
                                <ul className='complain-list'>
                                    <li>Date: <strong style={{fontSize: "1.2em"}}>{complaint.created_at}</strong></li>
                                    <br/>
                                    {complaint.is_resolved ? (<li><strong style={{fontSize: "1.2em"}}>Check, if satisfied: </strong><input type='checkbox' value={complaint.is_satisfied} checked={complaint.is_satisfied} onChange={(e) => updateComplaint(complaint.id, e)} /></li>) : ""}
                                </ul>
                            </div>
                        </div>
                    )
                })}
            </div>
     </div>
    );
}

export default CustomerComplain