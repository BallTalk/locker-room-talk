package com.locker.domain;

import com.locker.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "`user`")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id", length = 20, unique = true)
    private String loginId;

    @Enumerated(EnumType.STRING)

    @Column(name = "provider", nullable = false, length = 10)
    private Provider provider;

    @Column(name = "provider_id", length = 100)
    private String providerId;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "nickname", length = 20, nullable = false)
    private String nickname;

    @Column(name = "phone_number", length = 20, nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "team_code", length = 20, nullable = false)
    private String teamCode;

    @Column(name = "profile_image_url", length = 255)
    private String profileImageUrl;

    @Column(name = "status_message", length = 200)
    private String statusMessage;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private Status status;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "login_fail_count", nullable = false)
    private Integer loginFailCount;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public static User createLocalUser(
            String loginId,
            String encodedPassword,
            String nickname,
            String normalizedPhoneNumber,
            String teamCode
    ) {
        return User.builder()
                .loginId(loginId)
                .password(encodedPassword)
                .nickname(nickname)
                .phoneNumber(normalizedPhoneNumber)
                .teamCode(teamCode)
                .provider(Provider.LOCAL)
                .status(Status.ACTIVE)
                .loginFailCount(0)
                .build();
    }

    public static String normalizePhone(String raw) {
        return raw.replaceAll("\\D", "");
    }

    public static User createOAuthUser(
            Provider provider,
            String providerId,
            String loginId,
            String hashedPassword,
            String nickname,
            String teamCode,
            String profileImageUrl
    ) {
        return User.builder()
                .loginId(loginId)
                .password(hashedPassword)
                .provider(provider)
                .providerId(providerId)
                .nickname(nickname)
                .teamCode(teamCode)
                .profileImageUrl(profileImageUrl)
                .status(Status.ACTIVE)
                .loginFailCount(0)
                .build();
    }

    public void updateProfile(String nickname, String profileImageUrl, String statusMessage) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.statusMessage = statusMessage;
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void withdraw(LocalDateTime when) {
        this.status = Status.WITHDRAWN;
        this.deletedAt = when;
    }

    public void loginSucceeded(LocalDateTime now) {
        this.lastLoginAt = now;
        this.loginFailCount = 0;
        if (this.status == Status.SUSPENDED) {
            this.status = Status.ACTIVE;
        }
    }

    public void loginFailed(int maxFailCount) {
        this.loginFailCount++;
        if (this.loginFailCount >= maxFailCount) {
            this.status = Status.SUSPENDED;
        }
    }

}
