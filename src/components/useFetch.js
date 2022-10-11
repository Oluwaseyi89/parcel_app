export const UseFetch = async (url, meth, bod) => {
    function getCookie(name) {
        let cookieValue = null;
        if(document.cookie && document.cookie !== '') {
            const cookies = document.cookie.split(';');
            for (let i=0; i<cookies.length; i++) {
                const cookie = cookies[i].trim();
                if (cookie.substring(0, name.length + 1)) {
                    cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                    break;
                }
            }
        }
        return cookieValue;
    }
    
    const csrftoken = getCookie('csrftoken');

    const res = await fetch(url, {
        method: meth,
        mode: "cors",
        // credentials: "include",
        headers: {
            // "Content-Type": "multipart/form-data",
            // "Accept": "application/json",
            "X-CSRFToken": csrftoken,
            "X-Requested-With": "XMLHttpRequest"
            // "Access-Control-Allow-Credentials": true
        },
        body: bod
    });

    const data = await res.json();

    return data;
}

export const UseFetchJSON = async (url, meth, bod) => {
    function getCookie(name) {
        let cookieValue = null;
        if(document.cookie && document.cookie !== '') {
            const cookies = document.cookie.split(';');
            for (let i=0; i<cookies.length; i++) {
                const cookie = cookies[i].trim();
                if (cookie.substring(0, name.length + 1)) {
                    cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                    break;
                }
            }
        }
        return cookieValue;
    }
    
    const csrftoken = getCookie('csrftoken');

    const res = await fetch(url, {
        method: meth,
        mode: "cors",
        // credentials: "include",
        headers: {
            "Content-Type": "application/json",
            "Accept": "application/json",
            // "Access-Control-Allow-Origin": "*",
            "X-CSRFToken": csrftoken,
            "X-Requested-With": "XMLHttpRequest"
            // "Access-Control-Allow-Credentials": true
        },
        body: JSON.stringify(bod)
    });

    const data = await res.json();

    return data;
}