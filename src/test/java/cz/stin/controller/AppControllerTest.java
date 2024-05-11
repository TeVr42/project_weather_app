package cz.stin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import cz.stin.model.Constants;
import cz.stin.model.FavLocation;
import cz.stin.model.Location;
import cz.stin.model.WeatherModel;
import cz.stin.service.FavLocationService;
import cz.stin.service.ForecastService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AppControllerTest {

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private ForecastService forecastService;

    @Mock
    private FavLocationService favLocationService;

    @InjectMocks
    private AppController appController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIndex() throws JsonProcessingException {
        WeatherModel weatherModel = new WeatherModel();
        Location location = new Location();
        location.setName("Liberec");
        weatherModel.setLocation(location);
        when(session.getAttribute("authorized")).thenReturn(true);
        when(forecastService.createWeatherModel("Liberec")).thenReturn(weatherModel);

        String result = appController.index(session, model);

        assertEquals("index", result);
        verify(model).addAttribute("authorized", true);
        verify(model).addAttribute("wmodel", weatherModel);
    }

    @Test
    void testSearchLocation() {
        when(session.getAttribute("authorized")).thenReturn(true);

        String result = appController.searchLocation(session, model);

        assertEquals("search-location", result);
        verify(model).addAttribute("authorized", true);
    }


    @Test
    void testWeather() throws JsonProcessingException {
        WeatherModel weatherModel = new WeatherModel();
        Location location = new Location();
        location.setName("Prague");
        weatherModel.setLocation(location);
        when(session.getAttribute("authorized")).thenReturn(true);
        when(forecastService.createWeatherModel("Prague")).thenReturn(weatherModel);

        String result = appController.weather("Prague", session, model);

        assertEquals("index", result);
        verify(model).addAttribute("authorized", true);
        verify(model).addAttribute("wmodel", weatherModel);
    }

    @Test
    void testWeather_LocationNotFound() throws JsonProcessingException {
        when(session.getAttribute("authorized")).thenReturn(true);
        when(forecastService.createWeatherModel("UnknownLocation")).thenThrow(HttpClientErrorException.class);

        String result = appController.weather("UnknownLocation", session, model);

        assertEquals("error", result);
        verify(model).addAttribute("errorMessage", Constants.getMessageUnknownLocation());
    }

    @Test
    void testWeather_JsonProcessingError() throws JsonProcessingException {
        when(session.getAttribute("authorized")).thenReturn(true);
        when(forecastService.createWeatherModel("Prague")).thenThrow(JsonProcessingException.class);

        String result = appController.weather("Prague", session, model);

        assertEquals("error", result);
        verify(model).addAttribute("errorMessage", Constants.getMessageProcessingMistake());
    }

    @Test
    void testApiInfo() {
        when(session.getAttribute("authorized")).thenReturn(true);

        String result = appController.apiInfo(session, model, redirectAttributes);

        assertEquals("api-info", result);
        verify(model).addAttribute("authorized", true);
    }

    @Test
    void testFavoriteLocations_WithAuthorization() {
        when(session.getAttribute("authorized")).thenReturn(true);
        when(session.getAttribute("username")).thenReturn("testUser");
        List<FavLocation> locations = new ArrayList<>();
        when(favLocationService.findLocationsByUsername("testUser")).thenReturn(locations);

        String result = appController.favoriteLocations(session, model);

        assertEquals("favorites", result);
        verify(model).addAttribute("authorized", true);
        verify(model).addAttribute("locations", locations);
    }

    @Test
    void testFavoriteLocations_WithoutAuthorization() {
        when(session.getAttribute("authorized")).thenReturn(false);

        String result = appController.favoriteLocations(session, model);

        assertEquals("favorites", result);
        verify(model).addAttribute("authorized", false);
    }
}
