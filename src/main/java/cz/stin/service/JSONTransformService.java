package cz.stin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import cz.stin.model.Location;
import cz.stin.model.WeatherCondition;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import cz.stin.model.WeatherModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class JSONTransformService {

    private final ObjectMapper objectMapper;

    public JSONTransformService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    public WeatherModel transformCurrentJSON(String json, WeatherModel wmodel) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(json);

        addLocation(json, wmodel);

        JsonNode currentWeatherNode = rootNode.get("current");
        WeatherCondition currentWeather = createWeatherCondition(currentWeatherNode, "last_updated_epoch");

        wmodel.setCurrent(currentWeather);

        return wmodel;
    }

    public WeatherModel transformForecastJSON(String json, WeatherModel wmodel) throws JsonProcessingException {
        addLocation(json, wmodel);
        wmodel.setForecast(createConditions(json));
        return wmodel;
    }

    public WeatherModel transformHistoryJSON(String json, WeatherModel wmodel) throws JsonProcessingException {
        addLocation(json, wmodel);
        wmodel.setHistory(createConditions(json));
        return wmodel;
    }

    private WeatherModel addLocation(String json, WeatherModel wmodel) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(json);
        if (wmodel.getLocation() == null) {
            JsonNode locationNode = rootNode.get("location");
            Location location = createLocation(locationNode);
            wmodel.setLocation(location);
        }
        return wmodel;
    }

    private List<WeatherCondition> createConditions(String json) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(json);
        JsonNode forecastNode = rootNode.get("forecast");
        JsonNode dayNodes = forecastNode.get("forecastday");

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

        return weatherConditions;
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

    // model v parametru a přidat info z JSON, pote třída co bude hromadně vytvářet weathermodel se vším všudy
}
