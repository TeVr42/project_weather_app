package cz.stin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class RequestControllerTest {

    @Autowired
    private MockMvc mvc;
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

}
