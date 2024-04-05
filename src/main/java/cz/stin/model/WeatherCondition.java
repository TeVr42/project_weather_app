package cz.stin.model;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class WeatherCondition {
    private double temp_c;
    private String text;
    private String icon;
    private long epochTime;

    public String getFormattedTime() {
        Date date = new Date(epochTime * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        String formattedDate = sdf.format(date);
        return formattedDate;
    }
}
