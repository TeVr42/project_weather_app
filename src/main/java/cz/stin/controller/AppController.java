package cz.stin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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
            model.addAttribute("wmodel", wmodel);
            model.addAttribute("authorized", UserController.isAuthorized(session));

            Long locationId = favLocationService.findLocationIdByUsernameAndLocation(
                    (String) session.getAttribute("username"), wmodel.getLocation().getName());

            model.addAttribute("isFavorite", locationId != null);
            return "index";
        } catch (JsonProcessingException e) {
            model.addAttribute("errorMessage", "Při zpracování dat se vyskytla chyba, omlouváme se ale požadevek momentálně nejsme schopni naplnit.");
            return "error";
        }
    }

    @GetMapping("/hledat")
    public String searchLocation(HttpSession session, Model model) {
        model.addAttribute("authorized", UserController.isAuthorized(session));
        return "search-location";
    }

    @PostMapping("/pocasi")
    public String weather(@RequestParam("locationInput") String location, HttpSession session, Model model) {
        model.addAttribute("authorized", UserController.isAuthorized(session));
        try {
            WeatherModel wmodel = forecastService.createWeatherModel(location);
            model.addAttribute("wmodel", wmodel);
            Long locationId = favLocationService.findLocationIdByUsernameAndLocation(
                    (String) session.getAttribute("username"), wmodel.getLocation().getName());
            model.addAttribute("isFavorite", locationId != null);
            return "index";
        } catch (HttpClientErrorException e) {
            model.addAttribute("errorMessage", "Tuhle lokaci bohužel neznám, zkuste prosím jinou.");
            return "error";
        } catch (JsonProcessingException e) {
            model.addAttribute("errorMessage", "Při zpracování dat se vyskytla chyba, omlouváme se ale požadevek momentálně nejsme schopni naplnit.");
            return "error";
        }
    }

    @GetMapping("/api-info")
    public String apiInfo(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("userKey", System.getenv("USER_TOKEN"));
        model.addAttribute("authorized", UserController.isAuthorized(session));
        return "api-info";
    }

    @GetMapping("/oblibene")
    public String favoriteLocations(HttpSession session, Model model) {
        model.addAttribute("authorized", UserController.isAuthorized(session));
        if (UserController.isAuthorized(session)) {
            String username = (String) session.getAttribute("username");
            List<FavLocation> locations = favLocationService.findLocationsByUsername(username);
            model.addAttribute("locations", locations);
        }
        return "favorites";
    }

    @PostMapping("/oblibene")
    public String favoriteLocations(@RequestParam("locationInput") String location, HttpSession session, Model model) {
        if (UserController.isAuthorized(session)) {
            String username = (String) session.getAttribute("username");
            List<FavLocation> locations = favLocationService.findLocationsByUsername(username);
            model.addAttribute("locations", locations);
        }
        return "redirect:/oblibene";
    }

    @PostMapping("/pridat-misto")
    public String addLocation(@RequestParam("changeLocation") String location, HttpSession session, Model model) {
        FavLocation favLocation = new FavLocation();
        favLocation.setUsername((String) session.getAttribute("username"));
        favLocation.setLocation(location);
        favLocationService.addFavLocation(favLocation);
        return "redirect:/oblibene";
    }

    @PostMapping("/zrusit-misto")
    public String removeLocation(@RequestParam("changeLocation") String location, HttpSession session, Model model) {
        FavLocation favLocation = new FavLocation();
        String username = (String) session.getAttribute("username");
        favLocationService.removeFavLocation(username, location);
        return "redirect:/oblibene";
    }
}
