package ru.practicum.shareit.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class RequestDto {
    private Long id;
    private String description;
    private Long requestor;
    private LocalDateTime created;
}
