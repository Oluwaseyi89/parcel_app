import React from 'react';
import { useState, useEffect } from 'react';


import { colors } from '../config/colors';
import { UseFetch, UseFetchJSON } from '../utils/useFetch';

import {
    View, Text, StyleSheet, Platform, Image, ScrollView, TextInput, TouchableOpacity, ImageBackground, Button
} from 'react-native';

import AsyncStorage from '@react-native-async-storage/async-storage';

import ImagePicker from 'react-native-image-crop-picker';
import CheckBox from '@react-native-community/checkbox';



const RegisterCourierScreen = ({ navigation }) => {   
    
       
    
    const [details, setDetails] = useState({
        "first_name": "",
        "last_name": "",
        "bus_country": "",
        "bus_state": "",
        "bus_street": "",
        "cac_reg_no": "",
        "nin": "",
        "phone_no": "",
        "email": "",
        "cour_photo": null,
        "cour_policy": false,
        "password": "",
        "reg_date": new Date().toISOString(),
        "is_email_verified": false
    });

    const [imageUri, setImageUri] = useState();
    

    const [retPass, setRetPass] = useState("");

    const { first_name, last_name, bus_country, bus_state, bus_street,
            cac_reg_no, nin, phone_no, email, cour_photo, cour_policy, 
            password, reg_date, is_email_verified } = details;

    
    function handleFetchPhoto () {
        let options = {
            width: 300,
            height: 400,
            cropping: true,
            includeBase64: true,
            mediaType: 'photo'
        };

        ImagePicker.openPicker(options).then((res) => {
            let imageData = { uri: `data:${res.mime};base64,${res.data}` };
            setImageUri(imageData);           
            let uploadFile = {
                uri: Platform.OS === 'android' ? res['path'] : res.path.replace('file://', ''),
                type: res['mime'],
                size: res.size,
                extension: "." + res.mime.split('/').pop(),
                name: Platform.OS === 'ios' ? res['filename'] : res.path.split('/').pop()                                
            };
            setDetails({ ...details, cour_photo: uploadFile });
        }).catch((err) => alert(err.message));
    }

    
   
    function handleFirstNameChange(first_name) {
        setDetails({ ...details, first_name: first_name });
    }

    function handleLastNameChange(last_name) {
        setDetails({ ...details, last_name: last_name });
    }

    function handleBusCountryChange(bus_country) {
        setDetails({ ...details, bus_country: bus_country });
    }

    function handleBusStateChange(bus_state) {
        setDetails({ ...details, bus_state: bus_state });
    }
    
    function handleBusStreetChange(bus_street) {
        setDetails({ ...details, bus_street: bus_street });
    }

    function handleCacNoChange(cac_reg_no) {
        setDetails({ ...details, cac_reg_no: cac_reg_no });
    }

    function handleNinChange(nin) {
        setDetails({ ...details, nin: nin });
    }

    function handlePhoneChange(phone_no) {
        setDetails({ ...details, phone_no: phone_no });
    }

    function handleEmailChange(email) {
        setDetails({ ...details, email: email });
    }   

    function handleCourPolicyChange(cour_policy) {
        setDetails({ ...details, cour_policy: cour_policy });
        console.log(cour_policy);
    }

    function handlePasswordChange(password) {
        setDetails({ ...details, password: password });
    }

    function handleRetPassChange(retPass) {
        setRetPass(retPass);
    }

    function checkInput() {
        let inputs = [first_name, last_name, bus_country, bus_state, bus_street,
            cac_reg_no, nin, phone_no, email, password, retPass];          
        
        let count = 0;

        for(let i = 0; i < inputs.length; i++) {
            if(inputs[i] === "") count++;
        } 

        return count;
    }

    async function handleCourierRegistration() {  
        let fetchedIP = "";
        let inputErrorCount = checkInput();  
        await AsyncStorage.getItem("@IPStore:ipAdd")
                .then((res) => {
                    fetchedIP = res;
                }).catch((err) => alert(err.message));  
        
        if(fetchedIP != "" && cour_policy && inputErrorCount == 0 && password == retPass && cour_photo != null) {

            let routeUrl = "http://" + fetchedIP + ":7000/parcel_backends/reg_temp_cour_mobile/";
            
            const formDetails = new FormData();

            formDetails.append('first_name', first_name);
            formDetails.append('last_name', last_name);
            formDetails.append('bus_country', bus_country);
            formDetails.append('bus_state', bus_state);
            formDetails.append('bus_street', bus_street);            
            formDetails.append('cac_reg_no', cac_reg_no);
            formDetails.append('nin', nin);
            formDetails.append('phone_no', phone_no);
            formDetails.append('email', email);
            formDetails.append('password', password);
            formDetails.append('cour_photo', cour_photo);
            formDetails.append('cour_policy', cour_policy);
            formDetails.append('reg_date', new Date().toISOString());
            formDetails.append('is_email_verified', false);

            let apiOperation = UseFetch(routeUrl, "POST", formDetails);

            apiOperation.then((res) => {
                if(res.status === "success") {
                    alert(res.data);
                } else if(res.status === "error") {
                    alert(res.data);
                } else {
                    alert("An error occured...");
                }
            }).catch((err) => alert(err.message));

        } else if(cour_photo === null) {
            alert("Upload a photo please.");
        } else if (password != retPass) {
            alert("Passwords do not match!.");
        } else if (inputErrorCount > 0) {
            let message = inputErrorCount + " input fields are left blank";
            alert(message);
        } else if(!cour_policy) alert("Check the Courier Policy button please.");
    }
    
   
    return (
        <ScrollView>
        <View style={styles.mainContainer}>
            <View style={styles.container}>               
                <Text style={styles.heading}>COURIER REGISTRATION FORM</Text>
                <View style={styles.selectPhotoContainer}>
                    <TouchableOpacity onPress={handleFetchPhoto}>
                        <Text style={styles.selectPhoto}>Select Photo</Text>
                    </TouchableOpacity>
                    <Image style={styles.photo} source={ imageUri ? imageUri : require('../assets/IMG-20220722-WA0002.jpg')} resizeMode="contain"/>
                </View>                                            
                <TextInput style={styles.textInput} placeholder="First Name" value={first_name} onChangeText={handleFirstNameChange} textContentType='givenName'/>
                <View style={styles.underLine} />              
                <TextInput style={styles.textInput} placeholder="Last Name" value={last_name} onChangeText={handleLastNameChange} textContentType='familyName'/>
                <View style={styles.underLine} />              
                <TextInput style={styles.textInput} placeholder="Business Country" value={bus_country} onChangeText={handleBusCountryChange} textContentType='countryName'/>
                <View style={styles.underLine} />              
                <TextInput style={styles.textInput} placeholder="Business State/Province" value={bus_state} onChangeText={handleBusStateChange} textContentType='addressState'/>
                <View style={styles.underLine} />              
                <TextInput style={styles.textInput} placeholder="Business Street Address" value={bus_street} onChangeText={handleBusStreetChange} textContentType='fullStreetAddress'/>
                <View style={styles.underLine} />              
                <TextInput style={styles.textInput} placeholder="Business CAC Reg. No." value={cac_reg_no} onChangeText={handleCacNoChange} textContentType='addressState'/>
                <View style={styles.underLine} />              
                <TextInput style={styles.textInput} placeholder="National Identificatin Number (NIN)" value={nin} onChangeText={handleNinChange} textContentType='addressState'/>
                <View style={styles.underLine} />              
                <TextInput style={styles.textInput} placeholder="Phone Number" value={phone_no} onChangeText={handlePhoneChange} textContentType='telephoneNumber'/>
                <View style={styles.underLine} />              
                <TextInput style={styles.textInput} placeholder="E-Mail Address" value={email} onChangeText={handleEmailChange} textContentType='emailAddress'/>
                <View style={styles.underLine} />              
                <TextInput style={styles.textInput} placeholder="Password" value={password} onChangeText={handlePasswordChange} textContentType='password' secureTextEntry={true}/>
                <View style={styles.underLine} />              
                <TextInput style={styles.textInput} placeholder="Retype Password" value={retPass} onChangeText={handleRetPassChange} textContentType='password' secureTextEntry={true}/>
                <View style={styles.underLine} />
                <View style={styles.checkBoxContainer}>
                    <Text style={styles.policyText}>Read our Courier Policy here...</Text>
                    <View style={styles.checkBoxWrapper}>    
                        <CheckBox tintColors={{true: colors.neonRed, false: colors.silver}} value={cour_policy} onValueChange={handleCourPolicyChange} />
                        <Text style={styles.policyText}>Click to Accept</Text> 
                    </View>
                </View>
                <TouchableOpacity onPress={handleCourierRegistration}>
                    <Text style={styles.buttonDefault}>Register</Text>
                </TouchableOpacity>              
            </View>            
        </View>
        </ScrollView>
    )
}

const styles = StyleSheet.create({
    mainContainer: {
        flex: 1
    },
    container: {
        width: "80%",
        alignSelf: "center",
        height: "100%"
    },
    underLine: {
        height: 1,
        backgroundColor: colors.neonRed,
        marginTop: 0
    },
    textInput: {
        fontSize: 15,
        marginTop: 10,
        marginBottom: -12,
        textAlign: "center"
    },
    buttonDefault: {
        color: colors.pearlWhite,
        backgroundColor: colors.neonRed,
        textAlign: "center",
        borderRadius: 3,
        height: 30,
        paddingTop: 5,
        marginTop: 50,
        marginBottom: 45
    }, 
    selectPhoto: {
        height: 30,
        width: 85,
        borderRadius: 5,
        backgroundColor: colors.silver,
        color: colors.neonRed,
        textAlign: "center",
        paddingTop: 5
    },
    selectPhotoContainer: {
        height: 120,
        width: "100%",
        display: "flex",
        flexDirection: "row",
        justifyContent: "space-between",
        marginTop: 25,
        alignItems: "center"
    },
    photo: {
        height: 100,
        width: 100
    },
    heading: {
        textAlign: "center",
        marginTop: 25,
        fontWeight: "bold",
        fontSize: 18,
        color: colors.neonRed
    },
    policyText: {
        color: colors.neonRed,
        fontSize: 12,
        marginTop: 10
    },
    checkBoxContainer: {
        display: "flex",
        flexDirection: "column",
        alignItems: "flex-start",
        marginTop: 15
    },
    checkBoxWrapper: {
        display: "flex",
        flexDirection: "row",
    }      
});

export default RegisterCourierScreen