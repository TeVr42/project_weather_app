package cz.stin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import cz.stin.service.ForecastService;
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
        mvc.perform(MockMvcRequestBuilders.get("/api")
                        .param("key", "invalid_key")
                        .param("location", "Liberec"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{\"error\":\"Unauthorized request\"}"));
    }

    @Test
    public void testWeatherAPIEndpoint_ValidRequest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api")
                        .param("key", System.getenv("USER_TOKEN")) // Provide a valid key
                        .param("location", "Liberec"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testWeatherAPIEndpoint_HttpClientErrorException() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api")
                        .param("key", System.getenv("USER_TOKEN"))
                        .param("location", "yxusncjyx"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testWeatherAPIEndpoint_UnauthorizedAndInvalid() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api")
                        .param("key", "invalid_key")
                        .param("location", "yxusncjyx"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{\"error\":\"Unauthorized request\"}"));
    }

    @Test
    public void testWeatherAPIEndpoint_JsonProcessingException() throws Exception {
        when(forecastService.getJSONWeather(Mockito.anyString())).thenThrow(JsonProcessingException.class);

        mvc.perform(MockMvcRequestBuilders.get("/api")
                        .param("key", System.getenv("USER_TOKEN"))
                        .param("location", "Liberec"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testWeatherAPIEndpoint_HttpClientErrorException_mock() throws Exception {
        when(forecastService.getJSONWeather(Mockito.anyString())).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        mvc.perform(MockMvcRequestBuilders.get("/api")
                        .param("key", System.getenv("USER_TOKEN"))
                        .param("location", "Liberec"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
