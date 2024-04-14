package cz.stin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import cz.stin.model.WeatherModel;
import cz.stin.service.ForecastService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AppController {

    private final ForecastService forecastService;

    public AppController(ForecastService forecastService) {
        this.forecastService = forecastService;
    }
    @GetMapping("/")
    public String index(Model model) throws JsonProcessingException {
        WeatherModel wmodel = forecastService.createWeatherModel("Liberec");
        model.addAttribute("wmodel", wmodel);
        return "index";
    }

    @GetMapping("/hledat")
    public String search_location() {
        return "search-location";
    }

    @PostMapping("/pocasi")
    public String weather(@RequestParam("locationInput") String location, Model model) throws JsonProcessingException {
        WeatherModel wmodel = forecastService.createWeatherModel(location);
        model.addAttribute("wmodel", wmodel);
        return "index";
    }

    @GetMapping("/api-info")
    public String apiInfo() {
        return "api-info";
    }


}
