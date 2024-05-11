package cz.stin.controller;

import cz.stin.model.AppUser;
import cz.stin.service.UserService;
import cz.stin.validator.InputValidators;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public static boolean isAuthorized(HttpSession session) {
        Object authorized = session.getAttribute("authorized");
        return authorized != null && (Boolean) authorized;
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

        if (!validateUsername(username, model) || !validatePassword(username, password, session, model)) {
            return "login";
        }

        return "redirect:/";
    }

    private boolean validateUsername(String username, Model model) {
        if (!InputValidators.isValidUsername(username)) {
            model.addAttribute("message", "Neplatné číslo uživatelské jméno, může obsahovat pouze malá a velká písmena, číslice a podtržítka _");
            return false;
        }

        AppUser foundAppUser = userService.findUserByUsername(username);
        if (foundAppUser == null) {
            model.addAttribute("message", "Uživatelské jméno neexistuje, pokud jste tu poprvé zaregistrujte se");
            return false;
        }

        return true;
    }

    private boolean validatePassword(String username, String password, HttpSession session, Model model) {
        AppUser foundAppUser = userService.findUserByUsername(username);
        if (!foundAppUser.getPassword().equals(password)) {
            session.setAttribute("authorized", false);
            model.addAttribute("message", "Neplatné heslo");
            return false;
        }

        session.setAttribute("authorized", true);
        session.setAttribute("username", foundAppUser.getUsername());
        return true;
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

        if (!validateRegistration(username, password, cardNumber, model)) {
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

    private boolean validateRegistration(String username, String password, String cardNumber, Model model) {
        AppUser existingAppUser = userService.findUserByUsername(username);
        if (existingAppUser != null) {
            model.addAttribute("message", "Uživatelské jméno již existuje");
            return false;
        }

        if (!InputValidators.isValidUsername(username)) {
            model.addAttribute("message", "Neplatné číslo uživatelské jméno, může obsahovat pouze malá a velká písmena, číslice a podtržítka");
            return false;
        }

        if (!InputValidators.isValidCardNumber(cardNumber)) {
            model.addAttribute("message", "Neplatné číslo karty");
            return false;
        }

        if (!InputValidators.isValidPassword(password)) {
            model.addAttribute("message", "Neplatné heslo, musí být delší než 5 znaků a obsahovat pouze písmena, číslice a znaky: _ & * ;");
            return false;
        }

        return true;
    }

    @GetMapping("/odhlasit")
    public String logout(HttpSession session, Model model) {
        session.setAttribute("authorized", false);
        session.setAttribute("username", "");
        return "redirect:/";
    }

}
