package cz.stin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.stin.model.WeatherModel;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class JSONTransformServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private JSONTransformService jsonTransformService;

    @Test
    void weatherToJSON_ValidWeatherModel_ReturnsJSONString() throws JsonProcessingException {
        WeatherModel weatherModel = new WeatherModel();

        when(objectMapper.writeValueAsString(any(WeatherModel.class))).thenReturn("{}");

        String jsonString = jsonTransformService.weatherToJSON(weatherModel);

        assertNotNull(jsonString);
        assertFalse(jsonString.isEmpty());
    }
}
