package ru.jenningc.SpringDemoBot.model.weather;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;

import java.net.HttpURLConnection;
import java.util.Scanner;

@Slf4j
public class LocationApiHandler {
    public static JSONObject getLocationData(String locationName) {
        locationName = locationName.replace(" ", "+");

        String urlString = "http://api.openweathermap.org/geo/1.0/direct?q=" + locationName +
                "&limit=5&appid=9e5e3f9a81b9825b9b0fa653df28e93b";

        try {
            HttpURLConnection connection = WeatherApiClient.fetchApiResponse(urlString);

            if (connection.getResponseCode() != 200) {
                log.error("Error: Could not connect to API");
                return null;
            } else {
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(connection.getInputStream());

                while(scanner.hasNext()) {
                    resultJson.append(scanner.nextLine());
                }

                scanner.close();

                connection.disconnect();

                return WeatherParser.parseJson(resultJson.toString());
            }
        } catch (Exception e) {
            log.error("Error with weather: " + e.getMessage());
        }

        return null;
    }
}
