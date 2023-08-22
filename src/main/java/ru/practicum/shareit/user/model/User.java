package ru.practicum.shareit.user.model;

import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class User {
    private Long id;

    @NotNull(message = "User's login shouldn't be an empty.")
    private String name;

    @Email(message = "Incorrect email")
    @NotNull(message = "User's email shouldn't be an empty")
    private String email;
}