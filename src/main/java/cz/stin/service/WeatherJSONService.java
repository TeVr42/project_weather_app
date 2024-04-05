package cz.stin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import cz.stin.model.Location;
import cz.stin.model.WeatherCondition;
import cz.stin.model.WeatherData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WeatherJSONService {

    private final ObjectMapper objectMapper;

    public WeatherJSONService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    public WeatherData transformCurrentJSON(String json) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(json);
        JsonNode locationNode = rootNode.get("location");
        JsonNode currentWeatherNode = rootNode.get("current");

        Location location = createLocation(locationNode);
        WeatherCondition currentWeather = createWeatherCondition(currentWeatherNode, "last_updated_epoch");

        WeatherData weatherData = new WeatherData();
        weatherData.setLocation(location);

        List<WeatherCondition> weatherConditions = new ArrayList<>();
        weatherConditions.add(currentWeather);
        weatherData.setConditions(weatherConditions);

        return weatherData;
    }

    public WeatherData transformForecastJSON(String json) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(json);
        JsonNode locationNode = rootNode.get("location");
        JsonNode forecastNode = rootNode.get("forecast");
        JsonNode dayNodes = forecastNode.get("forecastday");

        Location location = createLocation(locationNode);
        WeatherData weatherData = new WeatherData();
        weatherData.setLocation(location);

        List<WeatherCondition> weatherConditions = new ArrayList<>();

        for (JsonNode dayNode : dayNodes) {
            JsonNode hourNode = dayNode.get("hour");

            for (JsonNode hourForecast : hourNode) {
                WeatherCondition weatherCondition = createWeatherCondition(hourForecast, "time_epoch");
                Date date = new Date(weatherCondition.getEpochTime() *1000);
                int hour = date.getHours();
                if (hour % 6 == 0) {
                    weatherConditions.add(weatherCondition);
                }
            }
        }
        weatherData.setConditions(weatherConditions);

        return weatherData;
    }

    private Location createLocation(JsonNode locationNode) {
        String name = locationNode.get("name").asText();
        String region = locationNode.get("region").asText();
        String country = locationNode.get("country").asText();

        Location location = new Location();
        location.setName(name);
        location.setRegion(region);
        location.setCountry(country);

        return location;
    }

    private WeatherCondition createWeatherCondition(JsonNode weatherNode, String timeName) {
        double tempC = weatherNode.get("temp_c").asDouble();
        String text = weatherNode.get("condition").get("text").asText();
        String icon = weatherNode.get("condition").get("icon").asText();
        long time = weatherNode.get(timeName).asLong();

        WeatherCondition weatherCondition = new WeatherCondition();
        weatherCondition.setTemp_c(tempC);
        weatherCondition.setText(text);
        weatherCondition.setIcon(icon);
        weatherCondition.setEpochTime(time);

        return weatherCondition;
    }
}
