package com.locker.user.domain;

import com.locker.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @Column(name = "provider_user_id", length = 100)
    private String providerUserId;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "nickname", length = 20, nullable = false)
    private String nickname;

    @Column(name = "favorite_team_id", length = 20, nullable = false)
    private String favoriteTeamId;

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
            String favoriteTeamId
    ) {
        return User.builder()
                .loginId(loginId)
                .password(encodedPassword)
                .nickname(nickname)
                .favoriteTeamId(favoriteTeamId)
                .provider(Provider.LOCAL)
                .status(Status.ACTIVE)
                .loginFailCount(0)
                .build();
    }

    // 추가 예정
    public void loginSucceeded(LocalDateTime now) {
        this.lastLoginAt = now;
        this.loginFailCount = 0;               // 실패 카운트 리셋
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

    public void withdraw(LocalDateTime when) {
        this.status = Status.WITHDRAWN;
        this.deletedAt = when;
    }

}
