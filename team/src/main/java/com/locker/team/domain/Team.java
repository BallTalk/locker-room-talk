package com.locker.team.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "team")
public class Team {

    @Id
    @Column(length = 20)
    private String code;

    @Column(name = "name_kr", length = 100, nullable = false)
    private String nameKr;

    @Column(name = "name_en", length = 100, nullable = false)
    private String nameEn;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @Column(name = "is_active", length = 1, nullable = false)
    private String isActive;

    @Column(name = "created_by", length = 50, nullable = false, updatable = false)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_by", length = 50, nullable = false)
    private String updatedBy;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}
