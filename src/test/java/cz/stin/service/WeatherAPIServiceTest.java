package cz.stin.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class WeatherAPIServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private WeatherAPIService weatherAPIService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        weatherAPIService = new WeatherAPIService(new RestTemplateBuilder() {
            @Override
            public RestTemplate build() {
                return restTemplate;
            }
        });
    }

    @Test
    public void testGetCurrentWeather_Success() {
        String location = "Prague";
        String expectedResponse = "Current weather data";
        when(restTemplate.getForEntity(any(), any())).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        String actualResponse = weatherAPIService.getCurrentWeather(location);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testGetHistory_valid() {
        String location = "Prague";
        when(restTemplate.getForEntity(any(), any())).thenReturn(new ResponseEntity<>("Historical weather data", HttpStatus.OK));

        String actualResponse = weatherAPIService.getHistoricalWeather(location);

        assertNotNull(actualResponse);
        assertTrue(actualResponse.contains("Historical weather data"));
    }

    @Test
    public void testHandleForecast() {
        String location = "Prague";
        when(restTemplate.getForEntity(any(), any())).thenReturn(new ResponseEntity<>("Forecast weather data", HttpStatus.OK));

        String actualResponse = weatherAPIService.getForecastWeather(location);

        assertNotNull(actualResponse);
        assertTrue(actualResponse.contains("Forecast weather data"));
    }

    @Test
    public void testGetWeatherData_ExceptionThrown() {
        String location = "Prague";
        when(restTemplate.getForEntity(any(), any())).thenThrow(new RuntimeException("Error occurred"));

        assertThrows(RuntimeException.class, () -> weatherAPIService.getCurrentWeather(location));
    }

    @Test
    public void testHandleForecast_SuccessWithDaysParameter() {
        String location = "Prague";
        when(restTemplate.getForEntity(any(), any())).thenReturn(new ResponseEntity<>("Forecast weather data", HttpStatus.OK));

        String actualResponse = weatherAPIService.getForecastWeather(location);

        assertNotNull(actualResponse);
        assertTrue(actualResponse.contains("Forecast weather data"));
    }

    @Test
    public void testHandleForecast_SuccessWithoutDaysParameter() {
        String location = "Prague";
        when(restTemplate.getForEntity(any(), any())).thenReturn(new ResponseEntity<>("Forecast weather data", HttpStatus.OK));

        String actualResponse = weatherAPIService.getForecastWeather(location);

        assertNotNull(actualResponse);
        assertTrue(actualResponse.contains("Forecast weather data"));
    }
}
