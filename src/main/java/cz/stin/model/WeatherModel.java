package cz.stin.model;

import lombok.Data;
import java.util.List;

@Data
public class WeatherModel {
    private Location location;
    private WeatherCondition current;
    private List<WeatherCondition> forecast;
    private List<WeatherCondition> history;


}