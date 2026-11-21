function getLocation() {
    const statusText = document.getElementById("gps-status");
    
    if (!navigator.geolocation) {
        statusText.innerText = "Geolocation is not supported by your browser.";
        return;
    }

    statusText.innerText = "Locating your position...";

    navigator.geolocation.getCurrentPosition(
        (position) => {
            const lat = position.coords.latitude;
            const lon = position.coords.longitude;

            // Save values to the hidden form inputs
            document.getElementById("latitude").value = lat;
            document.getElementById("longitude").value = lon;
            document.getElementById("origin").value = `Lat: ${lat.toFixed(4)}, Lon: ${lon.toFixed(4)}`;
            
            statusText.innerText = "GPS Location captured successfully!";
            statusText.style.color = "green";
        },
        (error) => {
            switch(error.code) {
                case error.PERMISSION_DENIED:
                    statusText.innerText = "User denied the request for Geolocation.";
                    break;
                case error.POSITION_UNAVAILABLE:
                    statusText.innerText = "Location information is unavailable.";
                    break;
                case error.TIMEOUT:
                    statusText.innerText = "The request to get user location timed out.";
                    break;
                default:
                    statusText.innerText = "An unknown error occurred.";
                    break;
            }
            statusText.style.color = "red";
        }
    );
}