package ru.jenningc.SpringDemoBot.model.weather;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

@Slf4j
public class WeatherApp {
    public static JSONObject getWeatherData(String locationName) {
        JSONArray locationData = getLocationData(locationName);
        return null;
    }

    public static JSONArray getLocationData(String locationName) {
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
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                return locationData;

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
}
