package cz.stin.model;

import lombok.Data;

@Data
public class WeatherCondition {
    private double temp_c;
    private String text;
    private String icon;
    private long epochTime;

}
