package ru.jenningc.SpringDemoBot.model.weather;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Scanner;

import static ru.jenningc.SpringDemoBot.model.weather.WeatherApiClient.fetchApiResponse;

@Slf4j
public class WeatherService {
    public static void getWeatherData(String locationName) {
        JSONObject location = LocationApiHandler.getLocationData(locationName);

        if (location != null) {
            double latitude = (double) location.get("lat");
            double longitude = (double) location.get("lon");

            String weatherApiUrl = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude +
                    "&lon=" + longitude + "&appid=9e5e3f9a81b9825b9b0fa653df28e93b";

            try {
                fetchAndProcessWeatherData(weatherApiUrl, locationName);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    public static void fetchAndProcessWeatherData(String urlString, String locationName) throws IOException, ParseException {
        HttpURLConnection connection = fetchApiResponse(urlString);

        if (connection.getResponseCode() != 200) {
            log.error("Error: Could not connect to API");
        } else {
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(connection.getInputStream());
            while (scanner.hasNext()) {
                resultJson.append(scanner.nextLine());
            }
            scanner.close();
            connection.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));
            WeatherDataMapper.mapWeatherData(resultJsonObj, locationName);
        }
    }
}
