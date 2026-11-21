<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.travel.util.HtmlEscaper" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Trip Weather & Smart Recommendations</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="container">
        <h1>Your Smart Travel Report</h1>
        
        <div class="card">
            <h3>Route: <%= HtmlEscaper.escapeHtml(request.getAttribute("origin")) %> ➔ <%= HtmlEscaper.escapeHtml(request.getAttribute("destination")) %></h3>
            
            <div class="weather-grid">
                <div class="weather-box">
                    <h4>Starting Point Weather</h4>
                    <p><%= HtmlEscaper.escapeHtml(request.getAttribute("originWeather")) %></p>
                </div>
                <div class="weather-box">
                    <h4>Destination Weather</h4>
                    <p><%= HtmlEscaper.escapeHtml(request.getAttribute("destWeather")) %></p>
                </div>
            </div>

            <div class="advice-box">
                <h4>💡 Smart Travel Advice</h4>
                <p><%= HtmlEscaper.escapeHtml(request.getAttribute("smartAdvice")) %></p>
            </div>
        </div>

        <br>
        <a href="index.jsp" class="back-btn">Plan Another Trip</a>
    </div>
</body>
</html>