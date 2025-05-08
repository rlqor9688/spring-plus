package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.response.TodoSummaryResponseDto;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TodoRepositoryCustom {
    Optional<Todo> findByIdWithUser(Long todoId);
    Page<TodoSummaryResponseDto> searchTodos(
            String title,
            LocalDateTime start,
            LocalDateTime end,
            String nickname,
            Pageable pageable);
}
