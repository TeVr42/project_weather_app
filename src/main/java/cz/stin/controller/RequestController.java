package cz.stin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import cz.stin.service.ForecastService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@RestController
public class RequestController {
    private final ForecastService forecastService;

    public RequestController(ForecastService forecastService) {
        this.forecastService = forecastService;
    }

    @RequestMapping("/hello")
    public String hello() {
        return "Hello world";
    }

    @RequestMapping(value = "/api", method = RequestMethod.GET)
    public String weatherAPI(String key, String location) {
        if (!key.equals(System.getenv("USER_TOKEN"))) {
            return "{\"error\":\"Unauthorized request\"}";
        }
        try {
            return forecastService.getJSONWeather(location);
        } catch (HttpClientErrorException e) {
            return e.getResponseBodyAsString();

        } catch (JsonProcessingException e) {
            return "{\"error\": \"JSON Processing exception\", \"text\":" + e.getMessage() + "}";
        }
    }

}
