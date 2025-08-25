package com.example.TP.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.Collections;
import java.util.Map;

@Entity
@Table(name = "destinations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Hotel extends ExtendedModel{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "slug")
    private String slug;

    @Column(name = "address")
    private String address;

    @Column(name = "phone")
    private String phone;

    @Column(name = "description")
    private String description;

    @Column(name = "destination_id")
    private Long destinationId;

    @Column(name = "star_rating")
    private int starRating;

    @Column(name = "check_in_time")
    private LocalTime checkInTime;

    @Column(name = "check_out_time")
    private LocalTime checkOutTime;  //"14:00"

    @Column(name = "amenities", columnDefinition = "JSON")
    private String amenities;

    public void setAmenities(Map<String, Object> amenities) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            this.amenities = objectMapper.writeValueAsString(amenities);
        } catch (JsonProcessingException e) {
            // Handle the exception gracefully, possibly by logging it or returning a default value
            e.printStackTrace();
            this.amenities = "{}"; // Set to an empty JSON object if serialization fails
        }
    }

    public Map<String, Object> getAmenities() {
        try {
            if (amenities == null || amenities.isEmpty()) {
                return Collections.emptyMap(); // Return an empty map if amenities are null or empty
            }
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(amenities, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            // Handle the exception gracefully, possibly by logging it or returning a default value
            e.printStackTrace();
            return Collections.emptyMap(); // Return an empty map if deserialization fails
        }
    }

}
