package com.locker.user.domain;

import com.locker.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Builder
@Table(name = "`user`")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id", length = 20, unique = true)
    private String loginId;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "provider",
            nullable = false,
            columnDefinition = "VARCHAR(10) NOT NULL " + "CHECK (provider IN ('LOCAL','GOOGLE','KAKAO'))"
    )
    private Provider provider;

    @Column(name = "provider_id", length = 100)
    private String providerId;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "nickname", length = 20, nullable = false)
    private String nickname;

    @Column(name = "phone_number", length = 20, nullable = false, unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "favorite_team", length = 20, nullable = false)
    private Team favoriteTeam;

    @Column(name = "profile_image_url", length = 255)
    private String profileImageUrl;

    @Column(name = "status_message", length = 200)
    private String statusMessage;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            columnDefinition = "VARCHAR(10) NOT NULL " + "CHECK (status IN (" + "'ACTIVE','SUSPENDED','BANNED','WITHDRAWN','DORMANT'))"
    )
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
            Team favoriteTeam
    ) {
        return User.builder()
                .loginId(loginId)
                .password(encodedPassword)
                .nickname(nickname)
                .phoneNumber(normalizedPhoneNumber)
                .favoriteTeam(favoriteTeam)
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
            String nickname,
            Team favoriteTeam,
            String profileImageUrl
    ) {
        return User.builder()
                .loginId(null)
                .password(null)
                .provider(provider)
                .providerId(providerId)
                .nickname(nickname)
                .favoriteTeam(favoriteTeam)
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
            this.status = Status.ACTIVE;       // 잠금 해제 로직이 있다면
        }
    }

    public void loginFailed(int maxFailCount) {
        this.loginFailCount++;
        if (this.loginFailCount >= maxFailCount) {
            this.status = Status.SUSPENDED;    // 일정 실패 횟수 이상이면 일시 정지
        }
    }

}
