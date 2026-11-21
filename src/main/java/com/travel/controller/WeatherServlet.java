package com.travel.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.travel.config.DBConnection;
import com.travel.service.WeatherApiService;

@WebServlet("/weatherServlet")
public class WeatherServlet extends HttpServlet {
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Fetch form parameters from index.jsp
        String origin = request.getParameter("origin");
        String destination = request.getParameter("destination");
        String latitude = request.getParameter("latitude");
        String longitude = request.getParameter("longitude");

        origin = origin == null ? null : origin.trim();
        destination = destination == null ? null : destination.trim();
        if (origin == null || origin.isBlank() || destination == null || destination.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Origin and destination are required.");
            return;
        }

        BigDecimal latitudeValue;
        BigDecimal longitudeValue;
        try {
            latitudeValue = parseCoordinate(latitude, "latitude", -90, 90);
            longitudeValue = parseCoordinate(longitude, "longitude", -180, 180);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }
        if ((latitudeValue == null) != (longitudeValue == null)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Both latitude and longitude are required when using GPS.");
            return;
        }

        String originWeather;
        // Fetch live weather for Origin (uses GPS if available, otherwise city name)
        if (latitudeValue != null) {
            originWeather = WeatherApiService.fetchWeatherByCoords(
                    latitudeValue.toPlainString(), longitudeValue.toPlainString());
        } else {
            originWeather = WeatherApiService.fetchWeather(origin);
        }

        // Fetch live weather for Destination
        String destWeather = WeatherApiService.fetchWeather(destination);

        // Generate Smart Travel Advice based on destination weather
        String smartAdvice = generateSmartAdvice(destWeather);

        try {
            saveSearch(origin, destination, latitudeValue, longitudeValue,
                    originWeather, destWeather, smartAdvice);
        } catch (SQLException e) {
            log("Unable to save weather search history.", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "The weather search completed, but its results could not be saved.");
            return;
        }

        // Pass data to result.jsp
        request.setAttribute("origin", origin);
        request.setAttribute("destination", destination);
        request.setAttribute("originWeather", originWeather);
        request.setAttribute("destWeather", destWeather);
        request.setAttribute("smartAdvice", smartAdvice);

        // Forward the request and data to result.jsp
        request.getRequestDispatcher("result.jsp").forward(request, response);
    }

    private BigDecimal parseCoordinate(String value, String name, int min, int max) {
        if (value == null || value.isBlank()) {
            return null;
        }

        BigDecimal coordinate;
        try {
            coordinate = new BigDecimal(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid " + name + " coordinate.");
        }
        if (coordinate.compareTo(BigDecimal.valueOf(min)) < 0
                || coordinate.compareTo(BigDecimal.valueOf(max)) > 0) {
            throw new IllegalArgumentException("The " + name + " coordinate is out of range.");
        }
        return coordinate;
    }

    private void saveSearch(String origin, String destination,
            BigDecimal latitude, BigDecimal longitude, String originWeather,
            String destinationWeather, String smartAdvice) throws SQLException {
        String sql = "INSERT INTO trip_history "
                + "(origin, destination, latitude, longitude, origin_weather, "
                + "destination_weather, smart_advice) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, origin);
            statement.setString(2, destination);
            if (latitude == null) {
                statement.setNull(3, Types.DECIMAL);
                statement.setNull(4, Types.DECIMAL);
            } else {
                statement.setBigDecimal(3, latitude);
                statement.setBigDecimal(4, longitude);
            }
            statement.setString(5, originWeather);
            statement.setString(6, destinationWeather);
            statement.setString(7, smartAdvice);
            statement.executeUpdate();
        }
    }

    private String generateSmartAdvice(String weatherInfo) {
        String lower = weatherInfo.toLowerCase();
        if (lower.contains("rain") || lower.contains("drizzle") || lower.contains("thunderstorm")) {
            return "Rain expected at destination! Pack an umbrella, waterproof shoes, and plan indoor activities like museums or cafes.";
        } else if (lower.contains("clear") || lower.contains("sun")) {
            return "Clear skies ahead! Great conditions for outdoor sightseeing, walking tours, and road travel. Carry sunglasses and sunscreen.";
        } else if (lower.contains("snow") || lower.contains("mist") || lower.contains("fog")) {
            return "Low visibility or cold conditions detected. Drive cautiously, check highway alerts, and pack heavy thermal wear.";
        } else {
            return "Moderate weather along your route. Keep a light jacket handy for evening temperature drops.";
        }
    }
}