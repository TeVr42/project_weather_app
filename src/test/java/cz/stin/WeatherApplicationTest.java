package cz.stin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WeatherApplication.class)
public class WeatherApplicationTest {

    @Autowired
    private WeatherApplication weatherApplication;

    @Test
    public void contextLoads() {
        assertNotNull(weatherApplication);
    }
}
