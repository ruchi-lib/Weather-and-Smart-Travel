package com.travel.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherApiService {
    private static final Logger LOGGER = Logger.getLogger(WeatherApiService.class.getName());
    private static final String API_KEY = System.getenv("OPENWEATHER_API_KEY");
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    public static String fetchWeather(String city) {
        if (API_KEY == null || API_KEY.isBlank()) {
            return "Weather unavailable: OPENWEATHER_API_KEY is not configured";
        }
        if (city == null || city.isBlank()) {
            return "Weather unavailable: no origin or destination provided";
        }
        return requestWeather("q=" + encode(city), "city");
    }

    public static String fetchWeatherByCoords(String lat, String lon) {
        if (API_KEY == null || API_KEY.isBlank()) {
            return "GPS weather unavailable: OPENWEATHER_API_KEY is not configured";
        }
        if (lat == null || lat.isBlank() || lon == null || lon.isBlank()) {
            return "GPS weather unavailable: coordinates are missing";
        }
        return requestWeather("lat=" + encode(lat) + "&lon=" + encode(lon), "GPS");
    }

    private static String requestWeather(String query, String lookupType) {
        HttpURLConnection connection = null;
        try {
            String url = BASE_URL + "?" + query + "&appid=" + encode(API_KEY) + "&units=metric";
            connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);

            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                LOGGER.warning("OpenWeatherMap " + lookupType + " request returned HTTP " + status + ".");
                return "Weather data unavailable (HTTP " + status + ")";
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            JSONObject json = new JSONObject(response.toString());
            String weatherCondition = json.getJSONArray("weather").getJSONObject(0).getString("description");
            double temperature = json.getJSONObject("main").getDouble("temp");
            return weatherCondition + ", " + temperature + "°C";
        } catch (IOException | JSONException | IllegalArgumentException e) {
            LOGGER.warning("OpenWeatherMap " + lookupType + " request failed ("
                    + e.getClass().getSimpleName() + ").");
            return "Weather data unavailable";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
