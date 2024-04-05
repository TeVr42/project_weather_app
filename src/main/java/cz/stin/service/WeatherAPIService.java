package cz.stin.service;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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

    public String getForecastWeather(String location, String date) {
        return getWeatherData(location, "forecast", date);
    }

    public String getHistoricalWeather(String location, String date) {
        return getWeatherData(location, "history", date);
    }

    private String getWeatherData(String location, String weatherType) {
        return getWeatherData(location, weatherType, null);
    }

    private String getWeatherData(String location, String weatherType, String date) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://api.weatherapi.com/v1/{weatherType}.json")
                .queryParam("key", apiKey).queryParam("q", location);

        if (date != null) {
            builder.queryParam("dt", date);
        }

        if (weatherType.equals("history")) {
            builder.queryParam("end_dt", "2024-04-05");
            if (date == null) {
                throw new IllegalArgumentException("Date parameter is required for historical weather");
            }
        }

        if (weatherType.equals("forecast")) {
            builder.queryParam("days", 3);
        }

        ResponseEntity<String> response = restTemplate.getForEntity(builder.buildAndExpand(weatherType).toUri(), String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("HTTP request failed with error code: " + response.getStatusCodeValue());
        }
    }
}
