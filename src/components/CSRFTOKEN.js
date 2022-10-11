const CSRFTOKEN = () => {
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
    return (
      <input name="csrfmiddlewaretoken" value={csrftoken} type="hidden" />
    )
  }

export default CSRFTOKEN