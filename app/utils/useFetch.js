export const UseFetch = async (url, meth, bod) => {  
    const res = await fetch(url, {
        method: meth,
        mode: "cors",        
        headers: {            
            "X-Requested-With": "XMLHttpRequest"           
        },
        body: bod
    });

    const data = await res.json();

    return data;
}

export const UseFetchJSON = async (url, meth, bod) => { 

    const res = await fetch(url, {
        method: meth,
        mode: "cors",       
        headers: {
            "Content-Type": "application/json",
            "Accept": "application/json",          
            "X-Requested-With": "XMLHttpRequest"            
        },
        body: JSON.stringify(bod)
    });

    const data = await res.json();

    return data;
}