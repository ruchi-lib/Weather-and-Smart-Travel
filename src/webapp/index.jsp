<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Weather Positioning & Smart Travelling System</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="container">
        <h1>Weather & Smart Travelling System</h1>
        <p class="subtitle">Plan your trip with real-time weather alerts and smart recommendations.</p>

        <form action="weatherServlet" method="POST">
            <!-- Hidden inputs to store live GPS coordinates if user allows location access -->
            <input type="hidden" id="latitude" name="latitude">
            <input type="hidden" id="longitude" name="longitude">

            <div class="form-group">
                <label for="origin">Starting Location (Origin):</label>
                <input type="text" id="origin" name="origin" required placeholder="e.g., New York or click GPS button">
                <button type="button" id="gps-btn" onclick="getLocation()">Use My Live GPS</button>
            </div>

            <div class="form-group">
                <label for="destination">Destination:</label>
                <input type="text" id="destination" name="destination" required placeholder="e.g., Boston">
            </div>

            <button type="submit" class="submit-btn">Check Route & Weather</button>
        </form>
        <p id="gps-status"></p>
    </div>

    <!-- Link the JavaScript file for Geolocation -->
    <script src="js/geolocation.js"></script>
</body>
</html>