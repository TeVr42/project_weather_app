package cz.stin.controller;

import cz.stin.service.WeatherAPIService;
import cz.stin.service.JSONTransformService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RequestController {

    private final WeatherAPIService apiService;
    private final JSONTransformService jsonService;

    public RequestController(WeatherAPIService apiService, JSONTransformService jsonService) {
        this.apiService = apiService;
        this.jsonService = jsonService;
    }

    @RequestMapping("/hello")
    public String hello() {
        return "Hello world";
    }

    @RequestMapping(value = "/api", method = RequestMethod.GET)
    public String weatherAPI(String location) {
        return apiService.getCurrentWeather(location);
    }

}
