package com.example.TP.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Collections;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@MappedSuperclass
public class ExtendedModel extends BaseModel {

    @Column(name = "attributes", columnDefinition = "JSON")
    private String attributes;

    public void setAttributes(Map<String, Object> attributes) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            this.attributes = objectMapper.writeValueAsString(attributes);
        } catch (JsonProcessingException e) {
            // Handle the exception gracefully, possibly by logging it or returning a default value
            e.printStackTrace();
            this.attributes = "{}"; // Set to an empty JSON object if serialization fails
        }
    }

    public Map<String, Object> getAttributes() {
        try {
            if (attributes == null || attributes.isEmpty()) {
                return Collections.emptyMap(); // Return an empty map if attributes are null or empty
            }
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(attributes, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            // Handle the exception gracefully, possibly by logging it or returning a default value
            e.printStackTrace();
            return Collections.emptyMap(); // Return an empty map if deserialization fails
        }
    }
}
