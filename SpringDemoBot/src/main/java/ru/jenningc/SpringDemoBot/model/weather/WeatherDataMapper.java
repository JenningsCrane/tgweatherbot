package ru.jenningc.SpringDemoBot.model.weather;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class WeatherDataMapper {
    public static void mapWeatherData(JSONObject resultJsonObj, String locationName) {
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
    }

    private static int toCelsius(double tmpKelvin) {
        return (int) (tmpKelvin - 273.15);
    }
}
