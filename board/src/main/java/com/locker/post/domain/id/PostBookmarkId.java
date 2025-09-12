package com.locker.post.domain.id;


import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PostBookmarkId implements Serializable {
    private Long postId;
    private Long userId;
}