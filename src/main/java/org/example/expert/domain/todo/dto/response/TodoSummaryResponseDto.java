package org.example.expert.domain.todo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor

public class TodoSummaryResponseDto {
    private final String title;
    private final Long managerCount;
    private final Long commentCount;
}
