package com.saveapenny.user.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.saveapenny.user.dto.UserProfileResponse;
import com.saveapenny.user.entity.User;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toUserProfileResponse_mapsFieldsCorrectly() {
        UUID id = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now().minusDays(1);
        OffsetDateTime updatedAt = OffsetDateTime.now();

        User user = User.builder()
                .id(id)
                .email("john@example.com")
                .fullName("John Doe")
                .active(true)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        UserProfileResponse response = userMapper.toUserProfileResponse(user);

        assertEquals(id, response.getId());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("John Doe", response.getFullName());
        assertEquals(true, response.getActive());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(updatedAt, response.getUpdatedAt());
    }
}
