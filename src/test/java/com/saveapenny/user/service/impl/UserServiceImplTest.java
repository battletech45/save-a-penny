package com.saveapenny.user.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.saveapenny.auth.service.RefreshTokenService;
import com.saveapenny.user.dto.ChangePasswordRequest;
import com.saveapenny.user.dto.UpdateUserProfileRequest;
import com.saveapenny.user.dto.UserProfileResponse;
import com.saveapenny.user.entity.User;
import com.saveapenny.user.exception.InvalidPasswordException;
import com.saveapenny.user.exception.PasswordReuseNotAllowedException;
import com.saveapenny.user.exception.UserNotFoundException;
import com.saveapenny.user.mapper.UserMapper;
import com.saveapenny.user.repository.UserRepository;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UUID userId;
    private User user;
    private UserProfileResponse profileResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = User.builder()
                .id(userId)
                .email("john@example.com")
                .passwordHash("hashed-current")
                .fullName("John Doe")
                .active(true)
                .createdAt(OffsetDateTime.now().minusDays(1))
                .updatedAt(OffsetDateTime.now())
                .build();

        profileResponse = UserProfileResponse.builder()
                .id(userId)
                .email("john@example.com")
                .fullName("John Doe")
                .active(true)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Test
    void getCurrentUser_returnsProfile_whenUserExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toUserProfileResponse(user)).thenReturn(profileResponse);

        UserProfileResponse result = userService.getCurrentUser(userId);

        assertEquals(userId, result.getId());
        verify(userRepository).findById(userId);
        verify(userMapper).toUserProfileResponse(user);
    }

    @Test
    void getCurrentUser_throws_whenUserMissing() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getCurrentUser(userId));
    }

    @Test
    void updateCurrentUserProfile_updatesNameAndReturnsProfile() {
        UpdateUserProfileRequest request = UpdateUserProfileRequest.builder().fullName("Jane Doe").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toUserProfileResponse(any(User.class))).thenReturn(
                UserProfileResponse.builder()
                        .id(userId)
                        .email("john@example.com")
                        .fullName("Jane Doe")
                        .active(true)
                        .createdAt(user.getCreatedAt())
                        .updatedAt(user.getUpdatedAt())
                        .build());

        UserProfileResponse result = userService.updateCurrentUserProfile(userId, request);

        assertEquals("Jane Doe", result.getFullName());
        verify(userRepository).save(user);
    }

    @Test
    void changeCurrentUserPassword_changesPassword_whenCurrentPasswordMatches() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("current-credential")
                .newPassword("next-credential")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("current-credential", "hashed-current")).thenReturn(true);
        when(passwordEncoder.matches("next-credential", "hashed-current")).thenReturn(false);
        when(passwordEncoder.encode("next-credential")).thenReturn("hashed-new");

        userService.changeCurrentUserPassword(userId, request);

        assertEquals("hashed-new", user.getPasswordHash());
        verify(userRepository).save(user);
        verify(refreshTokenService).revokeAllByUser(user);
    }

    @Test
    void changeCurrentUserPassword_throws_whenCurrentPasswordWrong() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("invalid-credential")
                .newPassword("next-credential")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("invalid-credential", "hashed-current")).thenReturn(false);

        assertThrows(InvalidPasswordException.class, () -> userService.changeCurrentUserPassword(userId, request));
    }

    @Test
    void changeCurrentUserPassword_throws_whenNewPasswordSameAsCurrent() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("same-credential")
                .newPassword("same-credential")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("same-credential", "hashed-current")).thenReturn(true);
        when(passwordEncoder.matches("same-credential", "hashed-current")).thenReturn(true);

        PasswordReuseNotAllowedException ex = assertThrows(
                PasswordReuseNotAllowedException.class,
                () -> userService.changeCurrentUserPassword(userId, request));

        assertTrue(ex.getMessage().contains("different"));
    }
}
