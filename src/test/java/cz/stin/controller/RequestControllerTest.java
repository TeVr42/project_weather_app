package cz.stin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import cz.stin.model.Constants;
import cz.stin.service.ForecastService;
import org.hamcrest.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.HttpClientErrorException;

@SpringBootTest
@AutoConfigureMockMvc
public class RequestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ForecastService forecastService;

    @Test
    void getHello() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/hello").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Hello world")));
    }

    @Test
    public void testWeatherAPIEndpoint_Unauthorized() throws Exception {
        when(forecastService.getJSONWeather(Mockito.anyString())).thenReturn("{\"error\":\"Unauthorized request\"}");
        mvc.perform(MockMvcRequestBuilders.get("/api")
                        .param("key", "invalid_key")
                        .param("location", "Liberec"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{\"error\":\"Unauthorized request\"}"));
    }

    @Test
    public void testWeatherAPIEndpoint_ValidRequest() throws Exception {
        when(forecastService.getJSONWeather(Mockito.anyString())).thenReturn("{\"weather\":\"Sunny\"}");
        mvc.perform(MockMvcRequestBuilders.get("/api")
                        .param("key", System.getenv(Constants.ENV_VAR_USER_TOKEN))
                        .param("location", "Liberec"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{\"weather\":\"Sunny\"}"));
    }

    @Test
    public void testWeatherAPIEndpoint_HttpClientErrorException() throws Exception {
        when(forecastService.getJSONWeather(Mockito.anyString())).thenReturn("{\"error\":\"Invalid location\"}");
        mvc.perform(MockMvcRequestBuilders.get("/api")
                        .param("key", System.getenv(Constants.ENV_VAR_USER_TOKEN))
                        .param("location", "invalid_location"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{\"error\":\"Invalid location\"}"));

    }

    @Test
    public void testWeatherAPIEndpoint_UnauthorizedAndInvalid() throws Exception {
        when(forecastService.getJSONWeather(Mockito.anyString())).thenReturn(Constants.getJsonUnauthorizedRequest());
        mvc.perform(MockMvcRequestBuilders.get("/api")
                        .param("key", "invalid_key")
                        .param("location", "invalid_location"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(Constants.getJsonUnauthorizedRequest()));
    }

    @Test
    public void testWeatherAPIEndpoint_JsonProcessingException() throws Exception {
        when(forecastService.getJSONWeather(Mockito.anyString())).thenThrow(JsonProcessingException.class);
        mvc.perform(MockMvcRequestBuilders.get("/api")
                        .param("key", System.getenv(Constants.ENV_VAR_USER_TOKEN))
                        .param("location", "Liberec"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testWeatherAPIEndpoint_HttpClientErrorException_mock() throws Exception {
        when(forecastService.getJSONWeather(Mockito.anyString())).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        mvc.perform(MockMvcRequestBuilders.get("/api")
                        .param("key", System.getenv(Constants.ENV_VAR_USER_TOKEN))
                        .param("location", "Liberec"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
