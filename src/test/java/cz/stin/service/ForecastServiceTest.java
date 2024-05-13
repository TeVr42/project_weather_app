package cz.stin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import cz.stin.model.WeatherModel;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ForecastServiceTest {
    @Mock
    private WeatherAPIService apiService;

    @Mock
    private JSONTransformService jsonService;

    @Autowired
    @InjectMocks
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


    @Test
    void createWeatherModel_ValidLocation_ReturnsWeatherModel() throws JsonProcessingException {
        WeatherModel expectedModel = new WeatherModel();
        when(apiService.getCurrentWeather(anyString())).thenReturn("currentWeatherJSON");
        when(apiService.getForecastWeather(anyString())).thenReturn("forecastWeatherJSON");
        when(apiService.getHistoricalWeather(anyString())).thenReturn("historyWeatherJSON");
        doNothing().when(jsonService).transformCurrentJSON(anyString(), any(WeatherModel.class));
        doNothing().when(jsonService).transformForecastJSON(anyString(), any(WeatherModel.class));
        doNothing().when(jsonService).transformHistoryJSON(anyString(), any(WeatherModel.class));

        WeatherModel actualModel = forecastService.createWeatherModel("location");

        assertNotNull(actualModel);
    }

    @Test
    void getJSONWeather_ValidLocation_ReturnsJSONString() throws JsonProcessingException {
        WeatherModel weatherModel = new WeatherModel();
        when(apiService.getCurrentWeather(anyString())).thenReturn("currentWeatherJSON");
        when(apiService.getForecastWeather(anyString())).thenReturn("forecastWeatherJSON");
        when(apiService.getHistoricalWeather(anyString())).thenReturn("historyWeatherJSON");
        doNothing().when(jsonService).transformCurrentJSON(anyString(), any(WeatherModel.class));
        doNothing().when(jsonService).transformForecastJSON(anyString(), any(WeatherModel.class));
        doNothing().when(jsonService).transformHistoryJSON(anyString(), any(WeatherModel.class));
        when(jsonService.weatherToJSON(any(WeatherModel.class))).thenReturn("weatherJSON");

        String actualJSON = forecastService.getJSONWeather("location");

        assertNotNull(actualJSON);
    }
}
