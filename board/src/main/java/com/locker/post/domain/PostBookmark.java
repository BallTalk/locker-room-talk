package com.locker.post.domain;

import com.locker.post.domain.id.PostBookmarkId;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "`post_bookmark`")
@IdClass(PostBookmarkId.class)
public class PostBookmark {

    @Id
    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;
}