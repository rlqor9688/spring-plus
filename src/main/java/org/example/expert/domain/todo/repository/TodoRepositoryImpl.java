package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.response.TodoSummaryResponseDto;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class TodoRepositoryImpl implements TodoRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public  TodoRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        QTodo todo = QTodo.todo;
        QUser user = QUser.user;

        Todo result = queryFactory
                .selectFrom(todo)
                .leftJoin(todo.user, user).fetchJoin()
                .where(todo.id.eq(todoId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Page<TodoSummaryResponseDto> searchTodos(
            String title,
            LocalDateTime start,
            LocalDateTime end,
            String nickname,
            Pageable pageable
    ) {
        QTodo todo = QTodo.todo;
        QUser user = QUser.user;
        QComment comment = QComment.comment;
        QManager manager = QManager.manager;

        List<TodoSummaryResponseDto> results = queryFactory
                .select(Projections.constructor(
                        TodoSummaryResponseDto.class,
                        todo.title,
                        manager.id.countDistinct(),
                        comment.id.countDistinct()
                ))
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(todo.comments, comment)
                .leftJoin(todo.user, user)
                .where(
                        title != null ? todo.title.contains(title) : null,
                        nickname != null ? user.nickname.contains(nickname) : null,
                        start != null && end != null ? todo.createdAt.between(start, end) : null
                )
                .groupBy(todo.id)
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        //전체 개수 쿼리
        long total = queryFactory
                .select(todo.count())
                .from(todo)
                .leftJoin(todo.user, user)
                .where(
                        title != null ? todo.title.contains(title) : null,
                        nickname != null ? user.nickname.contains(nickname) : null,
                        start != null && end != null ? todo.createdAt.between(start, end) : null
                )
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }
}
