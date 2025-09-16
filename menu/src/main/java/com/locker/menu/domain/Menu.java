package com.locker.menu.domain;

import com.locker.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "menu")
public class Menu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "position", nullable = false, length = 10)
    private String position;

    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(name = "ref_type", length = 20)
    private String refType;

    @Column(name = "ref_id", length = 50)
    private String refId;

    @Column(name = "path", length = 255)
    private String path;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "visible_yn", nullable = false, length = 1)
    private String visibleYn;

    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;

    @Column(name = "updated_by", nullable = false, length = 50)
    private String updatedBy;
}
