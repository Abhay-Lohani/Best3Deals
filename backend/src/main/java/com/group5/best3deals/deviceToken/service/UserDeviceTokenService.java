package com.group5.best3deals.deviceToken.service;

import com.group5.best3deals.deviceToken.dto.request.DeviceTokenRequest;
import com.group5.best3deals.deviceToken.dto.response.DeviceTokenResponse;
import com.group5.best3deals.deviceToken.entity.UserDeviceToken;
import com.group5.best3deals.deviceToken.repository.UserDeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserDeviceTokenService {

    private final UserDeviceTokenRepository userDeviceTokenRepository;

    public UserDeviceToken getDeviceTokenByUserId(Long userId) {
        return userDeviceTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("User device token not found with user " + userId));
    }

    public List<UserDeviceToken> getAllDeviceTokens() {
        return userDeviceTokenRepository.findAll();
    }

    public DeviceTokenResponse saveDeviceToken(DeviceTokenRequest deviceTokenRequest, Long userId) {
        // If a user has already registered a device
        if (userDeviceTokenRepository.existsByUserId(userId)) {
            throw new IllegalStateException(
                    "User has already registered a token. Please delete the existing record before creating a new one");
        }

        UserDeviceToken userDeviceToken = UserDeviceToken.builder()
                .deviceToken(deviceTokenRequest.getDeviceToken())
                .userId(userId)
                .build();

        userDeviceTokenRepository.save(userDeviceToken);

        return new DeviceTokenResponse(
                userDeviceToken.getId(),
                userDeviceToken.getDeviceToken(),
                userDeviceToken.getUserId()
        );
    }

    public void deleteDeviceTokenByUserId(Long userId) {
        UserDeviceToken token = getDeviceTokenByUserId(userId);

        userDeviceTokenRepository.deleteById(token.getId());
    }
}
