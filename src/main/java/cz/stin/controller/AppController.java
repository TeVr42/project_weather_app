package cz.stin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import cz.stin.model.Constants;
import cz.stin.model.FavLocation;
import cz.stin.model.WeatherModel;
import cz.stin.service.FavLocationService;
import cz.stin.service.ForecastService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AppController {

    private final ForecastService forecastService;
    private final FavLocationService favLocationService;

    public AppController(ForecastService forecastService, FavLocationService favLocationService) {
        this.forecastService = forecastService;
        this.favLocationService = favLocationService;
    }

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        try {
            WeatherModel wmodel = forecastService.createWeatherModel("Liberec");
            model.addAttribute(Constants.ATTRIBUTE_WEATHER_MODEL, wmodel);
            model.addAttribute(Constants.ATTRIBUTE_AUTHORIZED, UserController.isAuthorized(session));

            Long locationId = favLocationService.findLocationIdByUsernameAndLocation(
                    (String) session.getAttribute(Constants.ATTRIBUTE_USERNAME), wmodel.getLocation().getName());

            model.addAttribute(Constants.ATTRIBUTE_IS_FAVOURITE, locationId != null);
            return "index";
        } catch (JsonProcessingException e) {
            model.addAttribute(Constants.ATTRIBUTE_ERROR_MESSAGE, Constants.getMessageProcessingMistake());
            return "error";
        }
    }

    @GetMapping("/hledat")
    public String searchLocation(HttpSession session, Model model) {
        model.addAttribute(Constants.ATTRIBUTE_AUTHORIZED, UserController.isAuthorized(session));
        return "search-location";
    }

    @PostMapping("/pocasi")
    public String weather(@RequestParam("locationInput") String location, HttpSession session, Model model) {
        model.addAttribute(Constants.ATTRIBUTE_AUTHORIZED, UserController.isAuthorized(session));
        try {
            WeatherModel wmodel = forecastService.createWeatherModel(location);
            model.addAttribute(Constants.ATTRIBUTE_WEATHER_MODEL, wmodel);
            Long locationId = favLocationService.findLocationIdByUsernameAndLocation(
                    (String) session.getAttribute(Constants.ATTRIBUTE_USERNAME), wmodel.getLocation().getName());
            model.addAttribute(Constants.ATTRIBUTE_IS_FAVOURITE, locationId != null);
            return "index";
        } catch (HttpClientErrorException e) {
            model.addAttribute(Constants.ATTRIBUTE_ERROR_MESSAGE, Constants.getMessageUnknownLocation());
            return "error";
        } catch (JsonProcessingException e) {
            model.addAttribute(Constants.ATTRIBUTE_ERROR_MESSAGE, Constants.getMessageProcessingMistake());
            return "error";
        }
    }

    @GetMapping("/api-info")
    public String apiInfo(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute(Constants.ATTRIBUTE_USER_KEY, System.getenv(Constants.ENV_VAR_USER_TOKEN));
        model.addAttribute(Constants.ATTRIBUTE_AUTHORIZED, UserController.isAuthorized(session));
        return "api-info";
    }

    @GetMapping("/oblibene")
    public String favoriteLocations(HttpSession session, Model model) {
        model.addAttribute(Constants.ATTRIBUTE_AUTHORIZED, UserController.isAuthorized(session));
        if (UserController.isAuthorized(session)) {
            String username = (String) session.getAttribute(Constants.ATTRIBUTE_USERNAME);
            List<FavLocation> locations = favLocationService.findLocationsByUsername(username);
            model.addAttribute(Constants.ATTRIBUTE_LOCATIONS, locations);
        }
        return "favorites";
    }

    @PostMapping("/oblibene")
    public String favoriteLocations(@RequestParam("locationInput") String location, HttpSession session, Model model) {
        if (UserController.isAuthorized(session)) {
            String username = (String) session.getAttribute(Constants.ATTRIBUTE_USERNAME);
            List<FavLocation> locations = favLocationService.findLocationsByUsername(username);
            model.addAttribute(Constants.ATTRIBUTE_LOCATIONS, locations);
        }
        return "redirect:/oblibene";
    }

    @PostMapping("/pridat-misto")
    public String addLocation(@RequestParam("changeLocation") String location, HttpSession session, Model model) {
        FavLocation favLocation = new FavLocation();
        favLocation.setUsername((String) session.getAttribute(Constants.ATTRIBUTE_USERNAME));
        favLocation.setLocation(location);
        favLocationService.addFavLocation(favLocation);
        return "redirect:/oblibene";
    }

    @PostMapping("/zrusit-misto")
    public String removeLocation(@RequestParam("changeLocation") String location, HttpSession session, Model model) {
        FavLocation favLocation = new FavLocation();
        String username = (String) session.getAttribute(Constants.ATTRIBUTE_USERNAME);
        favLocationService.removeFavLocation(username, location);
        return "redirect:/oblibene";
    }
}
