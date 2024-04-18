package cz.stin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class AppControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void testIndexPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("index"));
    }

    @Test
    public void testWeather_ValidLocation() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/pocasi")
                        .param("locationInput", "Prague"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("index"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("wmodel"))
                .andExpect(MockMvcResultMatchers.model().attributeHasNoErrors("wmodel"));
    }

    @Test
    public void testWeather_UnknownLocation() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/pocasi")
                        .param("locationInput", "NeexistujiciLokace....."))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("error"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("errorMessage"))
                .andExpect(MockMvcResultMatchers.model().attribute("errorMessage", "Tuhle lokaci bohužel neznám, zkuste prosím jinou."));
    }

    @Test
    public void testSearchPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/hledat"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("search-location"));
    }

    @Test
    public void testAPIInfoPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api-info"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("api-info"));
    }

}
