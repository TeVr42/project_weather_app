package cz.stin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import cz.stin.model.AppUser;
import cz.stin.model.WeatherModel;
import cz.stin.service.ForecastService;
import cz.stin.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;

@Controller
public class AppController {

    private final ForecastService forecastService;
    private final UserService userService;

    public AppController(ForecastService forecastService, UserService userService) {
        this.forecastService = forecastService;
        this.userService = userService;
    }
    @GetMapping("/")
    public String index(Model model) {
        try {
            WeatherModel wmodel = forecastService.createWeatherModel("Liberec");
            model.addAttribute("wmodel", wmodel);
            return "index";
        } catch (JsonProcessingException e) {
            model.addAttribute("errorMessage", "Při zpracování dat se vyskytla chyba, omlouváme se ale požadevek momentálně nejsme schopni naplnit.");
            return "error";
        }
    }

    @GetMapping("/hledat")
    public String searchLocation() {
        return "search-location";
    }

    @PostMapping("/pocasi")
    public String weather(@RequestParam("locationInput") String location, Model model) {
        try {
            WeatherModel wmodel = forecastService.createWeatherModel(location);
            model.addAttribute("wmodel", wmodel);
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
    public String apiInfo(Model model) {
        model.addAttribute("userKey", System.getenv("USER_TOKEN"));
        return "api-info";
    }

    @GetMapping("/prihlaseni")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/registrace")
    public String register(Model model) {
        return "register";
    }

    @PostMapping("/registrace")
    public String register(
            @RequestParam("usernameInput") String username,
            @RequestParam("passwordInput") String password,
            @RequestParam("cardNumberInput") String cardNumber,
            Model model) {

        AppUser existingAppUser = userService.findUserByUsername(username);
        if (existingAppUser != null) {
            model.addAttribute("error", "Uživatelské jméno již existuje");
            return "register";
        }

        AppUser appUser = new AppUser();
        appUser.setUsername(username);
        appUser.setPassword(password);
        appUser.setCardNumber(cardNumber);

        userService.addUser(appUser);
        return "redirect:/prihlaseni";
    }


}
