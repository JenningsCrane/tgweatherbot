package ru.jenningc.SpringDemoBot.model.weather;

public class Weather {
    private static String townName;
    private static Integer temperature;
    private static Integer feelTemperature;
    private static long humidity;
    private static String weatherType;

    public static double getWind() {
        return wind;
    }

    public static void setWind(double wind) {
        Weather.wind = wind;
    }

    private static double wind;

    public static String getTownName() {
        return townName;
    }

    public static String getWeatherType() {
        return weatherType;
    }

    public static void setWeatherType(String weatherType) {
        if (weatherType.equals("Clouds")) {
            Weather.weatherType = "Облачно";
        } else if (weatherType.equals("Rain")) {
            Weather.weatherType = "Дождливо";
        } else if (weatherType.equals("Snow")) {
            Weather.weatherType = "Идет снег";
        } else if (weatherType.equals("Drizzle")) {
            Weather.weatherType = "Морось";
        } else if (weatherType.equals("Clear")) {
            Weather.weatherType = "Ясно";
        }
    }

    public static void setTownName(String townName) {
        Weather.townName = townName;
    }

    public static Integer getTemperature() {
        return temperature;
    }

    public static void setTemperature(Integer temperature) {
        Weather.temperature = temperature;
    }

    public static Integer getFeelTemperature() {
        return feelTemperature;
    }

    public static void setFeelTemperature(Integer feelTemperature) {
        Weather.feelTemperature = feelTemperature;
    }

    public static long getHumidity() {
        return humidity;
    }

    public static void setHumidity(long humidity) {
        Weather.humidity = humidity;
    }

    public static String weatherString(){
        return  "Город: " + townName + "\n\n" +
                "На улице: " + weatherType + "\n\n" +
                "Температура: " + (temperature >= 0 ? (temperature + "° тепла") : (temperature + "°")) + "\n\n" +
                "Ощущается как: " + (feelTemperature >= 0 ? (feelTemperature + "° тепла") : (feelTemperature + "°")) + "\n\n" +
                "Влажность: " + humidity + "%\n\n" +
                "Скорость ветра: " + wind + " м/с\n\n";
    }
}
