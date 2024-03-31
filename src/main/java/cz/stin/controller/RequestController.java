package cz.stin.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RequestController {

    private String apiKey = System.getenv("API_KEY");

    @RequestMapping("/api")
    public String hello() {
        return "Hello world";
    }

}
