package cz.stin.service;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class WeatherAPIService {

    private final RestTemplate restTemplate;
    private final String apiKey;

    public WeatherAPIService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
        this.apiKey = System.getenv("API_KEY");
    }

    public String getCurrentWeather(String location) {
        return getWeatherData(location, "current");
    }

    public String getForecastWeather(String location) {
        return getWeatherData(location, "forecast");
    }

    public String getHistoricalWeather(String location) {
        LocalDate currentDate = LocalDate.now();
        LocalDate fiveDaysAgoDate = currentDate.minusDays(5);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = fiveDaysAgoDate.format(formatter);

        return getWeatherData(location, "history", formattedDate);
    }

    public String getHistoricalWeather(String location, String date) {
        return getWeatherData(location, "history", date);
    }

    private String getWeatherData(String location, String weatherType) {
        return getWeatherData(location, weatherType, null);
    }

    private String getResponse(URI url) {
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("HTTP request failed with error code: " + response.getStatusCodeValue());
        }
    }

    private String getWeatherData(String location, String weatherType, String date) {
        UriComponentsBuilder builder = buildBaseUri(location);

        switch (weatherType) {
            case "history":
                handleHistory(builder, date);
                break;
            case "forecast":
                handleForecast(builder);
                break;
            case "current":
                break;
            default:
                throw new IllegalArgumentException("Invalid weather type");
        }

        return getResponse(builder.buildAndExpand(weatherType).toUri());
    }

    private UriComponentsBuilder buildBaseUri(String location) {
        return UriComponentsBuilder.fromUriString("http://api.weatherapi.com/v1/{weatherType}.json")
                .queryParam("key", apiKey)
                .queryParam("q", location)
                .queryParam("lang", "cs");
    }

    private void handleHistory(UriComponentsBuilder builder, String date) {
        if (date == null) {
            throw new IllegalArgumentException("Date parameter is required for historical weather");
        } else {
            builder.queryParam("dt", date);
        }

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedCurrentDate = currentDate.format(formatter);
        builder.queryParam("end_dt", formattedCurrentDate);
    }
    private void handleForecast(UriComponentsBuilder builder) {
        builder.queryParam("days", 3);
    }

}
