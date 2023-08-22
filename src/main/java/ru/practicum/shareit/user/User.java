package ru.practicum.shareit.user;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "users")
public class User {
    public static final int MAX_EMAIL_LENGTH = 512;
    public static final String NAME_COLUMN_NAME = "name";
    public static final String ID_COLUMN_NAME = "user_id";
    public static final String EMAIL_COLUMN_NAME = "email";

    @Id
    @Column(name = ID_COLUMN_NAME)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = NAME_COLUMN_NAME, nullable = false)
    private String name;
    @Column(name = EMAIL_COLUMN_NAME, nullable = false, length = MAX_EMAIL_LENGTH)
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null
                && Objects.equals(id, user.id)
                && Objects.equals(name, user.name)
                && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email);
    }
}
