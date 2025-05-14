package org.example.expert.domain.todo.controller;

import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.manager.service.LogService;
import org.example.expert.domain.manager.service.ManagerService;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ManagerServiceTest {

    @InjectMocks
    private ManagerService managerService;

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ManagerRepository managerRepository;

    @Mock
    private LogService logService;

    @Test
    void Manager_등록_중_예외가_발생해도_로그는_저장된다() {
        // given
        long todoId = 1L;
        long userId = 10L;
        long managerUserId = 20L;

        AuthUser authUser = new AuthUser(userId, "email", UserRole.USER, "nickname");
        User writer = new User("email", "pw",UserRole.USER, "nickname");
        ReflectionTestUtils.setField(writer, "id", userId);  // ID 수동 설정
        User manager = new User("m@email", "pw", UserRole.USER, "Manager");
        ReflectionTestUtils.setField(manager, "id", managerUserId); // ID 수동 설정
        Todo todo = new Todo("todo", "contents", "weather", writer);

        ManagerSaveRequest request = new ManagerSaveRequest(managerUserId);

        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));
        when(userRepository.findById(managerUserId)).thenReturn(Optional.of(manager));
        // LogService는 정상 동작한다고 가정
        when(logService.saveLog(any())).thenReturn(null);

        // manager 저장 시 예외 발생하도록 설정
        when(managerRepository.save(any())).thenThrow(new RuntimeException("DB 에러"));

        // 디버깅 로그
        System.out.println("logService mock class: " + logService.getClass());

        // when & then
        assertThrows(RuntimeException.class, () ->
                managerService.saveManager(authUser, todoId, request)
        );

        // 로그는 호출되었는지 검증
        verify(logService, times(1)).saveLog(any());
    }
}
