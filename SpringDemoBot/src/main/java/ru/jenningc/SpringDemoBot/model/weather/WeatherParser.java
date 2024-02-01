package ru.jenningc.SpringDemoBot.model.weather;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@Slf4j
public class WeatherParser {
    public static JSONObject parseJson(String json) {
        try {
            JSONParser parser = new JSONParser();
            JSONArray locationData = (JSONArray) parser.parse(json);
            return (JSONObject) locationData.get(0);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
