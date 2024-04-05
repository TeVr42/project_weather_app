package cz.stin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import cz.stin.model.Location;
import cz.stin.model.WeatherCondition;
import cz.stin.model.WeatherData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

public class WeatherJSONService {

    private ObjectMapper objectMapper;

    public WeatherJSONService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    public WeatherData transformCurrentJSON(String json) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(json);
        JsonNode locationNode = rootNode.get("location");
        JsonNode currentWeatherNode = rootNode.get("current");

        Location location = createLocation(locationNode);
        WeatherCondition currentWeather = createWeatherCondition(currentWeatherNode);

        WeatherData weatherData = new WeatherData();
        weatherData.setLocation(location);

        List<WeatherCondition> weatherConditions = new ArrayList<>();
        weatherConditions.add(currentWeather);
        weatherData.setConditions(weatherConditions);

        return weatherData;
    }

    public WeatherData transformForecastJSON(String json) {
        return null;
    }

    public WeatherData transformHistoryJSON(String json) {
        return null;
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

    private WeatherCondition createWeatherCondition(JsonNode weatherNode) {
        double tempC = weatherNode.get("temp_c").asDouble();
        String text = weatherNode.get("condition").get("text").asText();
        String icon = weatherNode.get("condition").get("icon").asText();

        WeatherCondition weatherCondition = new WeatherCondition();
        weatherCondition.setTemp_c(tempC);
        weatherCondition.setText(text);
        weatherCondition.setIcon(icon);

        return weatherCondition;
    }
}
