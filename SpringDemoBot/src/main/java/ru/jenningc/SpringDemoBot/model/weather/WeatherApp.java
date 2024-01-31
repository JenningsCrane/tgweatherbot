package ru.jenningc.SpringDemoBot.model.weather;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

@Slf4j
public class WeatherApp {
    public static void getWeatherData(String locationName) {
        JSONObject location = getLocationData(locationName);
        double latitude = (double) location.get("lat");
        double longitude = (double) location.get("lon");

        String urlString = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude +
                "&appid=9e5e3f9a81b9825b9b0fa653df28e93b";

        try {
            HttpURLConnection connection = fetchApiResponse(urlString);

            if (connection.getResponseCode() != 200) {
                log.error("Error:  Could not connect to API");
            }

            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(connection.getInputStream());
            while (scanner.hasNext()) {
                resultJson.append(scanner.nextLine());
            }

            scanner.close();
            connection.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            JSONObject main = (JSONObject) resultJsonObj.get("main");
            JSONArray weather = (JSONArray) resultJsonObj.get("weather");
            JSONObject weatherObj = (JSONObject) weather.get(0);
            JSONObject wind = (JSONObject) resultJsonObj.get("wind");

            Weather.setTownName(locationName);
            Weather.setTemperature(toCelsius((double) main.get("temp")));
            Weather.setHumidity((long) main.get("humidity"));
            Weather.setFeelTemperature(toCelsius((double) main.get("feels_like")));
            Weather.setWeatherType(String.valueOf(weatherObj.get("main")));
            Weather.setWind((double) wind.get("speed"));

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public static JSONObject getLocationData(String locationName) {
        locationName = locationName.replace(" ", "+");

        String urlString = "http://api.openweathermap.org/geo/1.0/direct?q=" + locationName +
                "&limit=5&appid=9e5e3f9a81b9825b9b0fa653df28e93b";

        try {
            HttpURLConnection connection = fetchApiResponse(urlString);

            if (connection.getResponseCode() != 200) {
                log.error("Error:  Could not connect to API");
                return null;
            } else {
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(connection.getInputStream());

                while(scanner.hasNext()) {
                    resultJson.append(scanner.nextLine());
                }

                scanner.close();

                connection.disconnect();

                JSONParser parser = new JSONParser();

                JSONArray locationData = (JSONArray) parser.parse(String.valueOf(resultJson));

                return (JSONObject) locationData.get(0);
            }
        } catch (Exception e) {
            log.error("Error with weather: " + e.getMessage());
        }

        return null;
    }

    private static HttpURLConnection fetchApiResponse (String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            connection.connect();
            return connection;
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return null;
    }

    private static int toCelsius(double tmpKelvin) {
        return (int) (tmpKelvin - 273.15);
    }
}
