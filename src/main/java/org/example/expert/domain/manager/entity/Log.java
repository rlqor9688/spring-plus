package org.example.expert.domain.manager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.common.entity.Timestamped;

@Getter
@Entity
@Table(name = "logs")
@NoArgsConstructor
@AllArgsConstructor
public class Log extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유저 정보
    private Long userId;

    // 매니저 정보
    private Long managerId;

    //일정 정보
    private Long todoId;

    //메시지
    private String message;

    public Log(Long userId, Long managerId, Long todoId, String message) {
        this.userId = userId;
        this.managerId = managerId;
        this.todoId = todoId;
        this.message = message;
    }
}
