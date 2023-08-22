package ru.practicum.shareit.user;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class User {
    private Long id;

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;


}
