package cz.stin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.stin.model.WeatherCondition;
import cz.stin.model.WeatherData;
import cz.stin.service.WeatherAPIService;
import cz.stin.service.WeatherJSONService;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RequestController {

    private WeatherAPIService apiService;
    private WeatherJSONService jsonService;

    public RequestController() {
        apiService = new WeatherAPIService(new RestTemplateBuilder());
        jsonService = new WeatherJSONService(new ObjectMapper());
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

    @RequestMapping("/apitestiv")
    public String weatherAPItestovaniIV() throws JsonProcessingException {
        String json = apiService.getCurrentWeather("Prague");
        WeatherData wdata =  jsonService.transformCurrentJSON(json);
        List<WeatherCondition> wlist = wdata.getConditions();
        return wlist.get(0).getText();
    }

    @RequestMapping(value = "/api", method = RequestMethod.GET)
    public String weatherAPI(String location) {
        return apiService.getCurrentWeather(location);
    }


}
