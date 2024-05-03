package cz.stin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import cz.stin.model.AppUser;
import cz.stin.model.WeatherModel;
import cz.stin.service.ForecastService;
import cz.stin.service.UserService;
import cz.stin.validators.InputValidators;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class AppController {

    private final ForecastService forecastService;
    private final UserService userService;

    public AppController(ForecastService forecastService, UserService userService) {
        this.forecastService = forecastService;
        this.userService = userService;
    }

    private boolean isAuthorized(HttpSession session) {
        Object authorized = session.getAttribute("authorized");
        return authorized != null && (Boolean) authorized;
    }

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        try {
            WeatherModel wmodel = forecastService.createWeatherModel("Liberec");
            model.addAttribute("wmodel", wmodel);
            model.addAttribute("authorized", isAuthorized(session));
            return "index";
        } catch (JsonProcessingException e) {
            model.addAttribute("errorMessage", "Při zpracování dat se vyskytla chyba, omlouváme se ale požadevek momentálně nejsme schopni naplnit.");
            return "error";
        }
    }

    @GetMapping("/hledat")
    public String searchLocation(HttpSession session, Model model) {
        model.addAttribute("authorized", isAuthorized(session));
        return "search-location";
    }

    @PostMapping("/pocasi")
    public String weather(@RequestParam("locationInput") String location, HttpSession session, Model model) {
        try {
            WeatherModel wmodel = forecastService.createWeatherModel(location);
            model.addAttribute("wmodel", wmodel);
            model.addAttribute("authorized", isAuthorized(session));
            return "index";
        } catch (HttpClientErrorException e) {
            model.addAttribute("errorMessage", "Tuhle lokaci bohužel neznám, zkuste prosím jinou.");
            model.addAttribute("authorized", isAuthorized(session));
            return "error";
        } catch (JsonProcessingException e) {
            model.addAttribute("errorMessage", "Při zpracování dat se vyskytla chyba, omlouváme se ale požadevek momentálně nejsme schopni naplnit.");
            model.addAttribute("authorized", isAuthorized(session));
            return "error";
        }
    }

    @GetMapping("/api-info")
    public String apiInfo(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("userKey", System.getenv("USER_TOKEN"));
        model.addAttribute("authorized", isAuthorized(session));
        return "api-info";
    }

    @GetMapping("/oblibene")
    public String favoriteLocations(HttpSession session, Model model) {
        model.addAttribute("authorized", isAuthorized(session));
        return "favorites";
    }

    @GetMapping("/prihlaseni")
    public String login(HttpSession session, Model model) {
        if (isAuthorized(session)) {
            return "redirect:/";
        }
        String message = (String) model.getAttribute("message");
        if (message != null) {
            model.addAttribute("message", message);
        }
        model.addAttribute("authorized", isAuthorized(session));
        return "login";
    }

    @PostMapping("/prihlaseni")
    public String login(
            @RequestParam("usernameInput") String username,
            @RequestParam("passwordInput") String password,
            HttpSession session,
            Model model) {

        model.addAttribute("authorized", isAuthorized(session));

        if (!InputValidators.isValidUsername(username)) {
            model.addAttribute("message", "Neplatné číslo uživatelské jméno, může obsahovat pouze malá a velká písmena, číslice a podtržítka _");
            return "login";
        }

        AppUser foundAppUser = userService.findUserByUsername(username);
        if (foundAppUser == null) {
            model.addAttribute("message", "Uživatelské jméno neexistuje, pokud jste tu poprvé zaregistrujte se");
            return "login";
        }

        if (!foundAppUser.getPassword().equals(password)) {
            model.addAttribute("message", "Neplatné heslo");
            return "login";
        }
        session.setAttribute("authorized", true);
        return "redirect:/";
    }

    @GetMapping("/registrace")
    public String register(HttpSession session, Model model) {
        if (isAuthorized(session)) {
            return "redirect:/";
        }
        model.addAttribute("authorized", isAuthorized(session));
        return "register";
    }

    @PostMapping("/registrace")
    public String register(
            @RequestParam("usernameInput") String username,
            @RequestParam("passwordInput") String password,
            @RequestParam("cardNumberInput") String cardNumber,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        model.addAttribute("authorized", isAuthorized(session));
        AppUser existingAppUser = userService.findUserByUsername(username);
        if (existingAppUser != null) {
            model.addAttribute("message", "Uživatelské jméno již existuje");
            return "register";
        }

        if (!InputValidators.isValidUsername(username)) {
            model.addAttribute("message", "Neplatné číslo uživatelské jméno, může obsahovat pouze malá a velká písmena, číslice a podtržítka _");
            return "register";
        }

        if (!InputValidators.isValidCardNumber(cardNumber)) {
            model.addAttribute("message", "Neplatné číslo karty");
            return "register";
        }

        if (!InputValidators.isValidPassword(password)) {
            model.addAttribute("message", "Neplatné heslo, musí být delší než 5 znaků a obsahovat pouze písmena, číslice a znaky: _ & * ;");
            return "register";
        }

        AppUser appUser = new AppUser();
        appUser.setUsername(username);
        appUser.setPassword(password);
        appUser.setCardNumber(cardNumber);

        userService.addUser(appUser);
        redirectAttributes.addFlashAttribute("message", "Registrace proběhla úspěšně, můžete se přihlásit");
        return "redirect:/prihlaseni";
    }

    @GetMapping("/odhlasit")
    public String logout(HttpSession session, Model model) {
        session.setAttribute("authorized", false);
        return "redirect:/";
    }

}
