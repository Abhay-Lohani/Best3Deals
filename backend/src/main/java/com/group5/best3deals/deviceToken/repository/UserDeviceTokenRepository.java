package com.group5.best3deals.deviceToken.repository;

import com.group5.best3deals.deviceToken.entity.UserDeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDeviceTokenRepository extends JpaRepository<UserDeviceToken, Long> {
    Optional<UserDeviceToken> findByUserId(Long userId);
    Boolean existsByUserId(Long userId);
}
