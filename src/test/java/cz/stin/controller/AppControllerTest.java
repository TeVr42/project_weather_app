package cz.stin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import cz.stin.model.Constants;
import cz.stin.model.FavLocation;
import cz.stin.model.Location;
import cz.stin.model.WeatherModel;
import cz.stin.service.FavLocationService;
import cz.stin.service.ForecastService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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

    @Test
    void testIndex() throws JsonProcessingException {
        WeatherModel weatherModel = new WeatherModel();
        Location location = new Location();
        location.setName("Liberec");
        weatherModel.setLocation(location);
        when(session.getAttribute(Constants.ATTRIBUTE_AUTHORIZED)).thenReturn(true);
        when(forecastService.createWeatherModel("Liberec")).thenReturn(weatherModel);

        String result = appController.index(session, model);

        assertEquals("index", result);
        verify(model).addAttribute(Constants.ATTRIBUTE_AUTHORIZED, true);
        verify(model).addAttribute(Constants.ATTRIBUTE_WEATHER_MODEL, weatherModel);
    }

    @Test
    void testIndex_JsonProcessingException() throws JsonProcessingException {
        when(forecastService.createWeatherModel(anyString())).thenThrow(JsonProcessingException.class);

        HttpSession session = mock(HttpSession.class);
        Model model = mock(Model.class);

        String result = appController.index(session, model);

        verify(model).addAttribute(Constants.ATTRIBUTE_ERROR_MESSAGE, Constants.getMessageProcessingMistake());

        assertEquals("error", result);
    }

    @Test
    void testSearchLocation() {
        when(session.getAttribute(Constants.ATTRIBUTE_AUTHORIZED)).thenReturn(true);

        String result = appController.searchLocation(session, model);

        assertEquals("search-location", result);
        verify(model).addAttribute(Constants.ATTRIBUTE_AUTHORIZED, true);
    }


    @Test
    void testWeather() throws JsonProcessingException {
        WeatherModel weatherModel = new WeatherModel();
        Location location = new Location();
        location.setName("Prague");
        weatherModel.setLocation(location);
        when(session.getAttribute(Constants.ATTRIBUTE_AUTHORIZED)).thenReturn(true);
        when(forecastService.createWeatherModel("Prague")).thenReturn(weatherModel);

        String result = appController.weather("Prague", session, model);

        assertEquals("index", result);
        verify(model).addAttribute(Constants.ATTRIBUTE_AUTHORIZED, true);
        verify(model).addAttribute(Constants.ATTRIBUTE_WEATHER_MODEL, weatherModel);
    }

    @Test
    void testWeather_LocationNotFound() throws JsonProcessingException {
        when(session.getAttribute(Constants.ATTRIBUTE_AUTHORIZED)).thenReturn(true);
        when(forecastService.createWeatherModel("UnknownLocation")).thenThrow(HttpClientErrorException.class);

        String result = appController.weather("UnknownLocation", session, model);

        assertEquals("error", result);
        verify(model).addAttribute(Constants.ATTRIBUTE_ERROR_MESSAGE, Constants.getMessageUnknownLocation());
    }

    @Test
    void testWeather_JsonProcessingError() throws JsonProcessingException {
        when(session.getAttribute(Constants.ATTRIBUTE_AUTHORIZED)).thenReturn(true);
        when(forecastService.createWeatherModel("Prague")).thenThrow(JsonProcessingException.class);

        String result = appController.weather("Prague", session, model);

        assertEquals("error", result);
        verify(model).addAttribute(Constants.ATTRIBUTE_ERROR_MESSAGE, Constants.getMessageProcessingMistake());
    }

    @Test
    void testApiInfo() {
        when(session.getAttribute(Constants.ATTRIBUTE_AUTHORIZED)).thenReturn(true);

        String result = appController.apiInfo(session, model, redirectAttributes);

        assertEquals("api-info", result);
        verify(model).addAttribute(Constants.ATTRIBUTE_AUTHORIZED, true);
    }

    @Test
    void testFavoriteLocations_WithAuthorization() {
        when(session.getAttribute(Constants.ATTRIBUTE_AUTHORIZED)).thenReturn(true);
        when(session.getAttribute(Constants.ATTRIBUTE_USERNAME)).thenReturn("testUser");
        List<FavLocation> locations = new ArrayList<>();
        when(favLocationService.findLocationsByUsername("testUser")).thenReturn(locations);

        String result = appController.favoriteLocations(session, model);

        assertEquals("favorites", result);
        verify(model).addAttribute(Constants.ATTRIBUTE_AUTHORIZED, true);
        verify(model).addAttribute(Constants.ATTRIBUTE_LOCATIONS, locations);
    }

    @Test
    void testFavoriteLocations_WithoutAuthorization() {
        when(session.getAttribute(Constants.ATTRIBUTE_AUTHORIZED)).thenReturn(false);

        String result = appController.favoriteLocations(session, model);

        assertEquals("favorites", result);
        verify(model).addAttribute(Constants.ATTRIBUTE_AUTHORIZED, false);
    }


    @Test
    void testFavoriteLocationsPost_WithAuthorization() {
        when(session.getAttribute(Constants.ATTRIBUTE_AUTHORIZED)).thenReturn(true);
        when(session.getAttribute(Constants.ATTRIBUTE_USERNAME)).thenReturn("testUser");
        List<FavLocation> locations = new ArrayList<>();
        when(favLocationService.findLocationsByUsername("testUser")).thenReturn(locations);

        String result = appController.favoriteLocations("location", session, model);

        assertEquals("redirect:/oblibene", result);
        verify(model).addAttribute(Constants.ATTRIBUTE_LOCATIONS, locations);
    }

    @Test
    void testFavoriteLocationsPost_WithoutAuthorization() {
        when(session.getAttribute(Constants.ATTRIBUTE_AUTHORIZED)).thenReturn(false);

        String result = appController.favoriteLocations("location", session, model);

        assertEquals("redirect:/oblibene", result);
    }

    @Test
    void testAddLocation() {
        when(session.getAttribute(Constants.ATTRIBUTE_USERNAME)).thenReturn("testUser");

        String result = appController.addLocation("location", session, model);

        assertEquals("redirect:/oblibene", result);
        FavLocation favLocation = new FavLocation();
        favLocation.setUsername("testUser");
        favLocation.setLocation("location");

        verify(favLocationService).addFavLocation(favLocation);
    }

    @Test
    void testRemoveLocation() {
        when(session.getAttribute(Constants.ATTRIBUTE_USERNAME)).thenReturn("testUser");

        String result = appController.removeLocation("location", session, model);

        assertEquals("redirect:/oblibene", result);
        verify(favLocationService).removeFavLocation("testUser", "location");
    }
}
