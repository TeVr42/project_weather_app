package cz.stin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import cz.stin.model.WeatherModel;
import org.springframework.stereotype.Component;

@Component
public class ForecastService {

    private WeatherAPIService apiService;
    private JSONTransformService jsonService;

    public ForecastService(WeatherAPIService apiService, JSONTransformService jsonService) {
        this.apiService = apiService;
        this.jsonService = jsonService;
    }
    private void getCurrentWeather(String location, WeatherModel wmodel) throws JsonProcessingException {
        String json = apiService.getCurrentWeather(location);
        jsonService.transformCurrentJSON(json, wmodel);
    }

    private void getForecastWeather(String location, WeatherModel wmodel) throws JsonProcessingException {
        String json = apiService.getForecastWeather(location);
        jsonService.transformForecastJSON(json, wmodel);
    }

    private void getHistoryWeather(String location, WeatherModel wmodel) throws JsonProcessingException {
        String json = apiService.getHistoricalWeather(location);
        jsonService.transformHistoryJSON(json, wmodel);
    }

    public WeatherModel createWeatherModel(String location) throws JsonProcessingException {
        WeatherModel wmodel = new WeatherModel();
        getCurrentWeather(location, wmodel);
        getForecastWeather(location, wmodel);
        getHistoryWeather(location, wmodel);
        return wmodel;
    }
}
