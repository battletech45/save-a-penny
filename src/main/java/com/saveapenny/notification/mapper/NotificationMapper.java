package com.saveapenny.notification.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saveapenny.notification.dto.CreateNotificationRequest;
import com.saveapenny.notification.dto.NotificationResponse;
import com.saveapenny.notification.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class NotificationMapper {

    @Autowired
    protected ObjectMapper objectMapper;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "metadata", expression = "java(jsonNodeToString(request.getMetadata()))")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract Notification toEntity(CreateNotificationRequest request);

    @Mapping(target = "metadata", expression = "java(stringToJsonNode(notification.getMetadata()))")
    public abstract NotificationResponse toResponse(Notification notification);

    public String jsonNodeToString(JsonNode jsonNode) {
        if (jsonNode == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Failed to serialize notification metadata.", ex);
        }
    }

    public JsonNode stringToJsonNode(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Failed to deserialize notification metadata.", ex);
        }
    }
}
