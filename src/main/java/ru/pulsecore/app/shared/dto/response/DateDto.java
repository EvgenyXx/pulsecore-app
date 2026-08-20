package ru.pulsecore.app.shared.dto.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class DateDto {
    private String date;

    private String timezone;
    private Integer timezone_type;



}