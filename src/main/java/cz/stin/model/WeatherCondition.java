package cz.stin.model;

import lombok.Data;
import java.util.Date;

@Data
public class WeatherCondition {
    private double temp_c;
    private String text;
    private String icon;
    private Date time;

}
