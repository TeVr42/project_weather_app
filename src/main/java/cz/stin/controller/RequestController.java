package cz.stin.controller;

import cz.stin.service.WeatherAPIService;
import cz.stin.service.JSONTransformService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RequestController {

    private WeatherAPIService apiService;
    private JSONTransformService jsonService;

    public RequestController(WeatherAPIService apiService, JSONTransformService jsonService) {
        this.apiService = apiService;
        this.jsonService = jsonService;
    }

    @RequestMapping("/hello")
    public String hello() {
        return "Hello world";
    }

    @RequestMapping("/apitest")
    public String weatherAPItestovani() {
        return apiService.getCurrentWeather("Prague");
    }

    @RequestMapping("/apitestii")
    public String weatherAPItestovaniII() {
        return apiService.getForecastWeather("Prague");
    }

    @RequestMapping("/apitestiii")
    public String weatherAPItestovaniIII() {
        return apiService.getHistoricalWeather("Prague", "2024-04-03");
    }


    @RequestMapping(value = "/api", method = RequestMethod.GET)
    public String weatherAPI(String location) {
        return apiService.getCurrentWeather(location);
    }


}
