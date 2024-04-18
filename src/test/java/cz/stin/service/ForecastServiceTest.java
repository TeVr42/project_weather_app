package cz.stin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import cz.stin.model.WeatherModel;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;

@SpringBootTest
@AutoConfigureMockMvc
public class ForecastServiceTest {

    @Autowired
    private ForecastService forecastService;

    @Test
    public void testCreateWeatherModelForValidLocation() throws JsonProcessingException {
        WeatherModel weatherModel = forecastService.createWeatherModel("Prague");
        Assertions.assertNotNull(weatherModel);
        Assertions.assertNotNull(weatherModel.getCurrent());
        Assertions.assertNotNull(weatherModel.getForecast());
        Assertions.assertNotNull(weatherModel.getHistory());
    }

    @Test
    public void testCreateWeatherModelForInvalidLocation() throws JsonProcessingException {
        try {
            forecastService.createWeatherModel("NeexistujiciLokace");
            Assertions.fail("Expected HttpClientErrorException was not thrown");
        } catch (HttpClientErrorException e) {
            // Test passed
        }
    }

    @Test
    public void testGetJSONWeatherForValidLocation() throws JsonProcessingException {
        String jsonString = forecastService.getJSONWeather("Liberec");
        Assertions.assertNotNull(jsonString);
    }

    @Test
    public void testGetJSONWeatherForInvalidLocation() throws JsonProcessingException {
        try {
            forecastService.getJSONWeather("NeexistujiciLokace");
            Assertions.fail("Expected HttpClientErrorException was not thrown");
        } catch (HttpClientErrorException e) {
            // Test passed
        }
    }
}
