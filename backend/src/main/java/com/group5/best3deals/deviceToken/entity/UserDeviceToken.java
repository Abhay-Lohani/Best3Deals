package com.group5.best3deals.deviceToken.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class UserDeviceToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String deviceToken;

    @Column(nullable = false)
    private Long userId;

    @Builder
    public UserDeviceToken(Long id, String deviceToken, Long userId) {
        this.id = id;
        this.deviceToken = deviceToken;
        this.userId = userId;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDeviceToken)) return false;
        UserDeviceToken that = (UserDeviceToken) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
