import '../styles/customer.css';



const CustomerComplain = () => {
    return (
     <div className='container'>
         <form>
            <br/>
            <legend className='Vendor-Log-Legend'>Complain Form</legend>
            <div className='form-group'>
                <input className='form-control' type='text' placeholder='Complain Subject'/>
            </div>
            <div className='form-group'>
                <input className='form-control' type='text' placeholder='Courier Involved'/>
            </div>
            <div className='form-group'>
               <textarea style={{height: "200px"}} className='form-control' placeholder='Complain Details'></textarea>
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
     </div>
    );
}

export default CustomerComplain