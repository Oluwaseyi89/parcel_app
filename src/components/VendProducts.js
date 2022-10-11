import { useEffect, useReducer, memo, useCallback, Fragment, useRef, useState } from 'react';
import './styles/product.css';
import './styles/vendor.css';
import { UseFetch, UseFetchJSON } from './useFetch';
// import { Link } from 'react-router-dom';
// import CSRFTOKEN from './CSRFTOKEN';
// import { useState } from 'react';
// import { UseFetch } from './useFetch';


const VendProducts = () => {

    const [produpload, setProdUpload] = useState(false);
    const [viewprod, setViewProd] = useState(false);
    const [uploadnote, setUploadNote] = useState(false);
    const [prodphoto, setProdPhoto] = useState(null);
    const [tempProd, setTempProd] = useState([]);
    const [product, setProduct] = useState({
        "prod_name": "",
        "prod_model": "",
        "prod_price": "",
        "prod_qty": "",
        "prod_desc": "",
        "prod_disc": ""
    });

    const [editprod, setEditProd] = useState({
        "edit_price": "",
        "edit_disc": "",
        "edit_qty": ""
    });

    const [prodsus, setProdSus] = useState("");
    const [proderr, setProdErr] = useState("");

    const handleProdEditChange =  useCallback((id, e) => {
        return dispatch({
            type: "CHANGE_PROD",
            payload: {"id": id, "e": e}
        });
      },[]);  

    const handleProdDelete =  useCallback((id) => {
        return dispatch({
            type: "DELETE_PROD",
            payload: id
        });
      },[]);  

    const initialState = {
        "products": null
    }
    

    const { prod_name, prod_model, prod_price, prod_qty, prod_desc, prod_disc } = product;
    const handleProdUpload = () => {
        setProdUpload(true);
        setViewProd(false);
        setUploadNote(false);
    }


    const handleEditClick =  useCallback((id) => {
      return dispatch({
          type: "EDIT",
          payload: id,
      });
    },[]);

    const getProducts = useCallback((data) => {
      return dispatch({
          type: "GET_PRODUCTS",
          payload: data,
      });
    },[]);
   

    const handleSubmitClick = useCallback((id) => {
        return dispatch({
            type: "SUBMIT_EDITTED",
            payload: id,
        });
      },[]);

    const reducer = (state, action) => {

        if (action.type === "GET_PRODUCTS") {
            let adjData = action.payload.map((prod) => {
                return {...prod, "edit": false, "submit": false}
            });
            return {...state, products:adjData}        
        }

        if(action.type === "EDIT") {
            let editFrag = state.products.map((prod)=>{
                if (prod.id === action.payload) {
                    return { ...prod, edit:true }
                }
                return {...prod};
               });   
            return { ...state, products:editFrag }
        }

        if(action.type === "DELETE_PROD") {
            let newProds = state.products.filter((prod) => prod.id !== action.payload);
            state.products.map((prod)=>{
                if (prod.id === action.payload) {
                    let apiUrl = `http://localhost:7000/parcel_product/del_product/${action.payload}/`;
                    let data = UseFetchJSON(apiUrl, 'DELETE');
                    data.then((res)=> {
                        if (res.status==="success") {
                            setProdSus(res.data);
                        } else {
                            setProdErr("An error occured");
                        }
                    }).catch((err) => console.log(err));
                }
               
                 return null;
               })  
            return { ...state, products: newProds }
        }

        if(action.type === "SUBMIT_EDITTED") {
            let submitFrag = state.products.map((prod)=>{
                if (prod.id === action.payload) {
                    let {edit_price, edit_disc, edit_qty} = editprod;
                    let updateData = {
                        "prod_price": edit_price,
                        "prod_qty": edit_qty,
                        "prod_disc": edit_disc,
                        "updated_at": new Date().toISOString()
                    }
                    
                    if (edit_price && edit_qty && edit_disc) {
                        let apiUrl = `http://localhost:7000/parcel_product/update_product/${action.payload}/`;
                        let data = UseFetchJSON(apiUrl, 'POST', updateData);
                        data.then((res)=>{
                            if (res.status === "success") {
                                setProdSus(res.data);
                                setEditProd({...editprod, edit_price:"", edit_disc:"", edit_qty:""})
                            } else if (res.status === "error") {
                                setProdErr(res.data);
                            } else {
                                setProdErr("An error occured");
                            }
                        }).catch((err) => setProdErr(err.message));
                    } else {
                        setProdErr("Some fields are empty, no changes made")
                    }
                   
                   
                    return { ...prod, edit:false }
                }
                return {...prod};
               });   
            return { ...state, products:submitFrag }
        }

        if(action.type === "CHANGE_PROD") {
            let changeFrag = state.products.map((prod)=>{
                if (prod.id === action.payload.id) {
                    let e = action.payload.e;
                    let name = e.target.name;
                    let value = e.target.value;
                   setEditProd({...editprod, [name]:value});
                    // console.log(editprod);
                    return { ...prod, prod_price:editprod.edit_price, 
                        prod_disc:editprod.edit_disc, prod_qty:editprod.edit_qty  }
                }
                return {...prod};
               });   
            return { ...state, products:changeFrag }
        }

        return state;
    }

  
    let logvend = JSON.parse(localStorage.getItem('logvend'));

    const handleProductChange = (e) => {
        let name = e.target.name;
        let value = e.target.value;
        setProduct({...product, [name]:value})
    }

   
    const handleViewProd = () => {
        setProdUpload(false);
        setUploadNote(false);
        setViewProd(true);
    }

    const handleUploadNote = () => {
        setProdUpload(false);
        setUploadNote(true);
        setViewProd(false);
    }

    const handleProductSubmit = (e) => {
        e.preventDefault();
        if (prodphoto && prod_name && prod_model && 
            prod_price && prod_desc && prod_disc) {
                const formData = new FormData();
            let full_path = $("#prod-file").val();
            let base_name = full_path.split('\\').pop().split('/').pop();
            let vendor_name = logvend.last_name + " " + logvend.first_name;
            console.log(base_name);
            formData.append("vendor_name", vendor_name);
            formData.append("vendor_phone", logvend.phone_no);
            formData.append("vendor_email", logvend.email);
            formData.append("vend_photo", logvend.vend_photo);
            formData.append("prod_cat", logvend.bus_category);
            formData.append("prod_name", prod_name);
            formData.append("prod_model", prod_model);
            formData.append("prod_photo", prodphoto, base_name);
            formData.append("prod_price", prod_price);
            formData.append("prod_qty", prod_qty);
            formData.append("prod_disc", prod_disc);
            formData.append("prod_desc", prod_desc);
            formData.append("img_base", base_name);
            formData.append("upload_date", new Date().toISOString());

            let apiUrl = "http://localhost:7000/parcel_product/product_upload/";

            let data = UseFetch(apiUrl, 'POST', formData);
            data.then((res) => {
                if (res.status === "success") {
                    setProdSus(res.data);
                } else if (res.status === "error") {
                    setProdErr(res.data);
                } else {
                    setProdErr("An error occured");
                }
            }).catch((err) => console.log(err));

            } else {
                setProdErr("Some fields are blank");
            }
    }

    const $ = window.$;
    const handlePhotoUpload = (e) => {
        let tmp_path = URL.createObjectURL(e.target.files[0]);
        $("#target-img").fadeIn("slow").attr('src', tmp_path);
        setProdPhoto(e.target.files[0]);
    }
  
    useEffect(()=>{        
        const fetchProducts = (email) => {
            email = logvend.email;
            let apiUrl = `http://localhost:7000/parcel_product/get_dist_ven_product/${email}/`; 
            let data = UseFetchJSON(apiUrl, 'GET');
            data.then((res) => {
                getProducts(res.data);
            });
        }
      fetchProducts();
    },[logvend.email, getProducts]);

    useEffect((email)=>{
        email = logvend.email;
        let apiUrl = `http://localhost:7000/parcel_product/get_dist_temp_product/${email}/`; 
        let data = UseFetchJSON(apiUrl, 'GET');
        data.then((res) => {
            setTempProd(res.data);
        });

    },[logvend.email]);


    const [state, dispatch] = useReducer(reducer, initialState)
    console.log(state);

    let notifications = [];
    if (state.products !== null) {
        let apprNote = {
            "status": "success",
            "data": `You have ${state.products.length} approved products.`
        }
        if (state.products.length > 0)  notifications.push(apprNote);
    }

    if (tempProd !== null) {
        let unapprNote = {
            "status": "error",
            "data": `You have ${tempProd.length} unapproved products`
        }
        if (tempProd.length > 0) notifications.push(unapprNote);
    }
      
   
    return (
        <div className="prod-container">
            <div className="button-container">
                <button onClick={handleProdUpload} className='btn prod-button'>Upload Products</button>
                <button onClick={handleViewProd} className='btn prod-button'>View Products</button>
                <button onClick={handleUploadNote} className='btn prod-button'>Upload Notifications</button>
            </div>
            <div className='prod-container'>
                {produpload?<form onSubmit={handleProductSubmit}>
                    <legend style={{fontSize: "1.2em"}} className='Vendor-Log-Legend'>Product Upload</legend>
                    <div className='photo-container'>
                        <div className='upload-label'>
                            <label htmlFor='prod_img'>Product Image</label>
                            <input id='prod-file' onChange={handlePhotoUpload} name='prod_img' type='file'/>
                        </div> 
                        <div className='avatar-container'>
                            {prodphoto?<img id='target-img' alt='avatar' className='img-thumbnail' src=''/>:""}
                        </div>
                    </div> <br/>
                    {proderr?(<div id='apiAlert' style={{"height": "50px", "textAlign": "center"}} className='alert alert-danger alert-dismissible' role='alert'>
                                    {proderr}
                                    <button className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                    </div>): "" }
                    {prodsus?(<div  style={{"height": "50px", "textAlign": "center"}} className='alert alert-success alert-dismissible' role='alert'>
                                    {prodsus}
                                    <button  className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                    </div>): ""}

                    <div className='form-group'>
                        <input name='prod_category' value={"Product Category: " + logvend.bus_category} type='text' readOnly="readonly" className='form-control' placeholder='Product Name'/>
                    </div>
                    <div className='form-group'>
                        <input onChange={handleProductChange} name='prod_name' value={prod_name} type='text' className='form-control' placeholder='Product Name'/>
                    </div>
                    <div className='form-group'>
                        <input onChange={handleProductChange}  name='prod_model' value={prod_model} type='text' className='form-control' placeholder='Product Model'/>
                    </div>
                    <div className='form-group'>
                        <input onChange={handleProductChange}  name='prod_price' value={prod_price} type='text' className='form-control' placeholder='Product Price'/>
                    </div>
                    <div className='form-group'>
                        <input onChange={handleProductChange}  name='prod_disc' value={prod_disc} type='text' className='form-control' placeholder='Percentage Discount Allowed (Figure only)'/>
                    </div>
                    <div className='form-group'>
                        <input onChange={handleProductChange}  name='prod_qty' value={prod_qty} type='text' className='form-control' placeholder='Product Quantity'/>
                    </div>
                    <div className='form-group'>
                        <textarea onChange={handleProductChange}  name='prod_desc' value={prod_desc} className='form-control form-descrip' placeholder='Product Description'></textarea>
                    </div>
                    <button className='btn prod-button'>Submit</button>
                </form>:""}
                {viewprod?<div>
                    <h4 style={{textAlign:"center"}}>You have {state.products.length} Products</h4> <br/>
                    {proderr?(<div id='apiAlert' style={{"height": "50px", "textAlign": "center"}} className='alert alert-danger alert-dismissible' role='alert'>
                                    {proderr}
                                    <button className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                    </div>): "" }
                    {prodsus?(<div  style={{"height": "50px", "textAlign": "center"}} className='alert alert-success alert-dismissible' role='alert'>
                                    {prodsus}
                                    <button  className='close' role='alert' data-dismiss='alert'><span>&times;</span></button>
                    </div>): ""}

                    <div className='show-prod-container'>
                        {state.products.map((prod, index)=>(
                            <ProductFragment index={index} handleProdDelete={handleProdDelete} editprod={editprod} handleProdEditChange={handleProdEditChange} prod={prod} handleSubmitClick={handleSubmitClick} handleEditClick={handleEditClick}/>
                        ))} 
                    </div>
                </div>:""}
                {uploadnote?<div className="container">
                    {notifications.map((item, index) => {
                       return (
                       <div className={item.status==="success"?"alert alert-success":"alert alert-danger"}>
                           <p style={{textAlign:"center", fontSize:"1.2em"}} key={index}>{item.data}</p>
                       </div>)
                    })}
                </div>:""}
            </div>
        </div>     
    );
}

const ProductFragment = memo(({prod, index, editprod, handleProdDelete, handleProdEditChange, handleEditClick, handleSubmitClick})=> {
    return (
        <div className='prod-frag img-thumbnail' key={index}>
                            <div className='prod-frag-img'>
                                <img className='img-thumbnail' alt='product' src={prod.prod_photo}/>
                            </div>
                            <div className='prod-detail'>
                                <div>
                                    <label className='badge'>Name:</label>
                                   <input type='text' readOnly="readonly" value={prod.prod_name}/>
                                </div>
                                <div>
                                    <label className='badge'>Model:</label>
                                    <input type='text' readOnly="readonly" value={prod.prod_model}/>
                                </div>
                                <div>
                                    <label className='badge'>Price:</label>
                                    {prod.edit?<input name="edit_price" onChange={(e)=>handleProdEditChange(prod.id, e)} type='text' value={editprod.edit_price}/>
                                    :<input   type='text' readOnly="readonly" value={prod.prod_price}/>}
                                </div>
                                <div>
                                    <label className='badge'>Discount:</label>
                                    {prod.edit?<input name="edit_disc" onChange={(e)=>handleProdEditChange(prod.id, e)} type='text' value={editprod.edit_disc}/>
                                    :<input   type='text' readOnly="readonly" value={prod.prod_disc}/>}
                                </div>
                                <div>
                                    <label className='badge'>Quantity:</label>
                                    {prod.edit?<input name="edit_qty" onChange={(e)=>handleProdEditChange(prod.id, e)} type='text' value={editprod.edit_qty}/>
                                    :<input   type='text' readOnly="readonly" value={prod.prod_qty}/>}
                                </div> <br/>
                                <div className='prod-btn'>
                                    <button onClick={()=>handleEditClick(prod.id)} className="btn">Edit</button>
                                    {prod.edit && <button onClick={()=>handleSubmitClick(prod.id)} style={{backgroundColor:"chartreuse", color:"black"}}  className="btn">Update</button>}
                                    {prod.edit && <button onClick={()=>handleProdDelete(prod.id)} style={{backgroundColor:"rgb(219, 33, 76)", color:"white"}}   className="btn">Delete</button>}
                                </div>
                            </div>
                        </div>
    )
});

export default VendProducts