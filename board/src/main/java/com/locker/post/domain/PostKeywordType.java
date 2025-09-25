package com.locker.post.domain;

import lombok.Getter;

@Getter
public enum PostKeywordType {
    TITLE("제목"),
    AUTHOR("작성자");

    private final String label;

    PostKeywordType(String label) {
        this.label = label;
    }
}