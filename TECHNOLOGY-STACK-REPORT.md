# Weather & Smart Travel Website: Technology Report

## Overview

This is a Java web application that accepts an origin and destination, retrieves current weather for both locations, generates basic travel advice, and records each search in MySQL.

## Technologies by layer

| Layer | Technologies | Role |
|---|---|---|
| Frontend | JSP, HTML5, CSS, vanilla JavaScript | Renders the home and results pages; collects trip details and optional GPS coordinates. No frontend framework or client-side package manager is used. |
| Web/application server | Apache Tomcat 10.1.60; Jakarta Servlet API 6.0 | Runs the WAR and dispatches requests to the Java servlets. |
| Backend | Java, Jakarta Servlets, JDBC | Handles weather searches, GPS input validation, advice generation, and database writes. |
| Weather integration | OpenWeatherMap Current Weather API; `HttpURLConnection`; `org.json` 20240303 | Requests weather by city or coordinates, parses the JSON response, and displays the condition and temperature in Celsius. |
| Database | MySQL; MySQL Connector/J 8.3.0 | Stores weather-search history in `trip_history`. |
| Build and packaging | Apache Maven 3.9.15; Maven WAR Plugin 3.4.0 | Compiles the Maven project and packages it as `weather-smart-travel.war`. The project compiler source and target are Java 17. |

## Frontend

- [src/webapp/index.jsp](src/webapp/index.jsp) provides the origin and destination form. It submits a POST request to `weatherServlet`.
- [src/webapp/js/geolocation.js](src/webapp/js/geolocation.js) uses the browser Geolocation API, with the visitor's permission, to populate hidden latitude and longitude fields.
- [src/webapp/result.jsp](src/webapp/result.jsp) displays the route, weather results, and travel advice returned by the backend.
- [src/webapp/css/style.css](src/webapp/css/style.css) supplies the page layout and visual styling.

## Server and request routing

- [src/webapp/WEB-INF/web.xml](src/webapp/WEB-INF/web.xml) sets `index.jsp` as the welcome page.
- The weather servlet route is declared with a Jakarta `@WebServlet` annotation: `/weatherServlet`.
- The Maven build packages the contents of `src/webapp` into the WAR. The deployed local application is available at `http://localhost:8080/weather-smart-travel/`.

## Backend and data flow

1. The browser posts the origin, destination, and optional coordinates to `/weatherServlet`.
2. [src/main/java/com/travel/controller/WeatherServlet.java](src/main/java/com/travel/controller/WeatherServlet.java) validates coordinate ranges and coordinates the weather lookup and advice generation.
3. [src/main/java/com/travel/service/WeatherApiService.java](src/main/java/com/travel/service/WeatherApiService.java) calls OpenWeatherMap. It reads the API key from the `OPENWEATHER_API_KEY` environment variable; without it, weather is reported as unavailable.
4. The servlet generates advice based on destination-weather keywords, saves the search to `trip_history`, and forwards the results to `result.jsp`.
5. [src/main/java/com/travel/config/DBConnection.java](src/main/java/com/travel/config/DBConnection.java) opens JDBC connections to the local `weather_travel_db` database.

The application expects a `trip_history` table containing `origin`, `destination`, `latitude`, `longitude`, `origin_weather`, `destination_weather`, and `smart_advice`. The search-history timestamp is expected to be database-generated.

## Security and operational notes

- Database credentials are read from the `DB_USER` and `DB_PASSWORD` environment variables. The optional `DB_URL` variable overrides the local default.
- The JDBC URL currently disables SSL and enables public-key retrieval for local MySQL connectivity. Use properly configured TLS for remote or production database connections.
