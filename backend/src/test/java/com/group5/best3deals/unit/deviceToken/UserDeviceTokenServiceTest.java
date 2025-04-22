package com.group5.best3deals.unit.deviceToken;

import com.group5.best3deals.deviceToken.dto.request.DeviceTokenRequest;
import com.group5.best3deals.deviceToken.dto.response.DeviceTokenResponse;
import com.group5.best3deals.deviceToken.entity.UserDeviceToken;
import com.group5.best3deals.deviceToken.repository.UserDeviceTokenRepository;
import com.group5.best3deals.deviceToken.service.UserDeviceTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDeviceTokenServiceTest {

    @Mock
    private UserDeviceTokenRepository userDeviceTokenRepository;

    @InjectMocks
    private UserDeviceTokenService userDeviceTokenService;

    private UserDeviceToken userDeviceToken;
    private DeviceTokenRequest deviceTokenRequest;

    private final Long wrongUserId = 100L;

    @BeforeEach
    public void setUp() {
        userDeviceToken = UserDeviceToken.builder()
                .id(1L)
                .deviceToken("sample_token")
                .userId(1L)
                .build();

        deviceTokenRequest = new DeviceTokenRequest("new_sample_token");
    }

    @Test
    public void testGetDeviceTokenByUserId_ShouldReturnUserDeviceToken() {
        when(userDeviceTokenRepository.findByUserId(userDeviceToken.getUserId()))
                .thenReturn(Optional.of(userDeviceToken));

        UserDeviceToken response = userDeviceTokenService.getDeviceTokenByUserId(userDeviceToken.getUserId());

        assertNotNull(response);
        assertEquals(userDeviceToken.getDeviceToken(), response.getDeviceToken());
        assertEquals(userDeviceToken.getUserId(), response.getUserId());
    }

    @Test
    public void testGetDeviceTokenByUserId_ShouldThrowException_WhenNotFound() {
        when(userDeviceTokenRepository.findByUserId(wrongUserId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userDeviceTokenService.getDeviceTokenByUserId(wrongUserId));
    }

    @Test
    public void testGetAllDeviceTokens_ShouldReturnList() {
        when(userDeviceTokenRepository.findAll()).thenReturn(List.of(userDeviceToken));

        List<UserDeviceToken> tokenList = userDeviceTokenService.getAllDeviceTokens();

        assertNotNull(tokenList);
        assertEquals(1, tokenList.size());
        assertEquals(userDeviceToken, tokenList.get(0));
    }

    // Test save
    @Test
    public void testSaveDeviceToken_ShouldReturnDeviceTokenResponse() {
        when(userDeviceTokenRepository.existsByUserId(userDeviceToken.getUserId())).thenReturn(false);
        when(userDeviceTokenRepository.save(any(UserDeviceToken.class))).thenReturn(userDeviceToken);

        DeviceTokenResponse response =
                userDeviceTokenService.saveDeviceToken(deviceTokenRequest, userDeviceToken.getUserId());

        assertNotNull(response);
        assertEquals(deviceTokenRequest.getDeviceToken(), response.getDeviceToken());
        assertEquals(userDeviceToken.getUserId(), response.getUserId());
        verify(userDeviceTokenRepository, times(1)).save(any(UserDeviceToken.class));
    }

    @Test
    public void testSaveDeviceToken_ShouldThrowException_WhenTokenAlreadyExists() {
        when(userDeviceTokenRepository.existsByUserId(userDeviceToken.getUserId())).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> userDeviceTokenService.saveDeviceToken(deviceTokenRequest, userDeviceToken.getUserId()));
    }

    // Test delete
    @Test
    public void testDeleteDeviceTokenByUserId_ShouldDeleteToken() {
        when(userDeviceTokenRepository.findByUserId(userDeviceToken.getUserId())).thenReturn(Optional.of(userDeviceToken));

        userDeviceTokenService.deleteDeviceTokenByUserId(userDeviceToken.getUserId());

        verify(userDeviceTokenRepository, times(1)).deleteById(userDeviceToken.getId());
    }
}
