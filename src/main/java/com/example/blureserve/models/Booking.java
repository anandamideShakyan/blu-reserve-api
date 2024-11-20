package com.example.blureserve.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Booking {
    private String userId;
    private String location;
    private String slot;
    private String date;
    private List<String> seats;

    // Getters and setters (or use Lombok annotations)
}
