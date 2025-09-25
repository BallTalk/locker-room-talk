package com.locker.post.infra;

import com.locker.post.domain.Post;
import com.locker.post.domain.PostKeywordType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import static com.locker.post.domain.QPost.post;
import static com.locker.user.domain.QUser.user;

@Component
@RequiredArgsConstructor
class PostQueryDsl {

    private final JPAQueryFactory queryFactory;

    private BooleanExpression keywordCondition(String keyword, PostKeywordType keywordType) {
        if (keyword == null || keyword.isBlank()) return null;

        // null(전체) : 기본 (제목+내용)
        if (keywordType == null) {
            return post.title.containsIgnoreCase(keyword)
                    .or(post.content.containsIgnoreCase(keyword));
        }

        return switch (keywordType) {
            case TITLE -> post.title.containsIgnoreCase(keyword);
            case AUTHOR -> user.nickname.containsIgnoreCase(keyword)
                    .and(user.id.eq(post.authorId));
        };
    }

    // 현재 검색어 있는경우 복합인덱스 제대로 타지 않음
    public Page<Post> findByBoardId(Long boardId, Pageable pageable, String keyword, PostKeywordType keywordType) {
        List<Post> posts = queryFactory
                .selectFrom(post)
                .where(
                        post.boardId.eq(boardId),
                        post.status.eq("ACTIVE"),
                        keywordCondition(keyword, keywordType)
                )
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(post.count())
                .from(post)
                .where(
                        post.boardId.eq(boardId),
                        post.status.eq("ACTIVE"),
                        keywordCondition(keyword, keywordType)
                )
                .fetchOne();

        return new PageImpl<>(posts, pageable, total != null ? total : 0L);
    }
}
