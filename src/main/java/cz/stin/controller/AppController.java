package cz.stin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import cz.stin.model.WeatherModel;
import cz.stin.service.ForecastService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class AppController {

    private ForecastService forecastService;

    public AppController(ForecastService forecastService) {
        this.forecastService = forecastService;
    }
    @GetMapping("/")
    public String index(Model model) throws JsonProcessingException {
        WeatherModel wmodel = forecastService.createWeatherModel("Liberec");
        model.addAttribute("wmodel", wmodel);
        return "index";
    }
}
