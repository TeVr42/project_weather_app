package cz.stin.model;

import lombok.Data;
import java.util.List;

@Data
public class WeatherData {
    private Location location;
    private List<WeatherCondition> conditions;

}
