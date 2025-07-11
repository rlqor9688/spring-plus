package org.example.expert.domain.todo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.dto.response.TodoSummaryResponseDto;
import org.example.expert.domain.todo.service.TodoService;
import org.example.expert.security.CustomUserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/todos")
    public ResponseEntity<TodoSaveResponse> saveTodo(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody TodoSaveRequest todoSaveRequest
    ) {
        AuthUser authUser = principal.toAuthUser();
        return ResponseEntity.ok(todoService.saveTodo(authUser, todoSaveRequest));
    }

    @GetMapping("/todos")
    public ResponseEntity<Page<TodoResponse>> getTodos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String weather,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        LocalDateTime start = (startDate != null) ? toStartOfDay(startDate) : null;
        LocalDateTime end = (endDate != null) ? toEndOfDay(endDate): LocalDateTime.now();
        return ResponseEntity.ok(todoService.getTodos(page, size, weather, start, end));
    }

    @GetMapping("/todos/search")
    public ResponseEntity<Page<TodoSummaryResponseDto>> searchTodos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String nickname
    ) {
        LocalDateTime start = (startDate!= null) ? toStartOfDay(startDate) : null;
        LocalDateTime end = (endDate !=null) ? toEndOfDay(endDate) : LocalDateTime.now();
        return ResponseEntity.ok(todoService.searchTodos(page, size, title, start, end, nickname));
    }

    private LocalDateTime toStartOfDay(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("날짜 형식이 잘못되었습니다. yyyy-MM-dd 형식을 사용하세요.");
        }
    }

    private LocalDateTime toEndOfDay(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atTime(LocalTime.MAX);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("날짜 형식이 잘못되었습니다. yyyy-MM-dd 형식을 사용하세요.");
        }
    }

    @GetMapping("/todos/{todoId}")
    public ResponseEntity<TodoResponse> getTodo(@PathVariable long todoId) {
        return ResponseEntity.ok(todoService.getTodo(todoId));
    }
}
