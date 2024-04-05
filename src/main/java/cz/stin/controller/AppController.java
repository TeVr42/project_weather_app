package cz.stin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.stin.model.WeatherData;
import cz.stin.service.WeatherAPIService;
import cz.stin.service.WeatherJSONService;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class AppController {

    private WeatherAPIService apiService;
    private WeatherJSONService jsonService;

    public AppController() {
        apiService = new WeatherAPIService(new RestTemplateBuilder());
        jsonService = new WeatherJSONService(new ObjectMapper());
    }
    @GetMapping("/")
    public String index(Model model) throws JsonProcessingException {
        String json = apiService.getForecastWeather("Liberec");
        WeatherData weatherData = jsonService.transformForecastJSON(json);
        model.addAttribute("weatherData", weatherData);
        return "index";
    }
}
