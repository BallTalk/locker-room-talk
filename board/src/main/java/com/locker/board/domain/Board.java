package com.locker.board.domain;

import com.locker.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "`board`")
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private BoardType type;

    @Column(name = "team_code", length = 20)
    private String teamCode;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "is_active", nullable = false, length = 1)
    private String isActive;

    @Column(name = "allow_anonymous", nullable = false, length = 1)
    private String allowAnonymous;

    @Column(name = "post_count", nullable = false)
    private Integer postCount;

    @Column(name = "comment_count", nullable = false)
    private Integer commentCount;
}
