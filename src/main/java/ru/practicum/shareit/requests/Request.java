package ru.practicum.shareit.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "requests")
public class Request {
    public static final int MAX_DESCRIPTION_LENGTH = 512;
    public static final String ID_COLUMN_NAME = "request_id";
    public static final String DESCRIPTION_COLUMN_NAME = "description";
    public static final String REQUESTOR_COLUMN_NAME = "requestor";
    public static final String CREATED_COLUMN_NAME = "created";

    @Id
    @Column(name = ID_COLUMN_NAME)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = DESCRIPTION_COLUMN_NAME, nullable = false, length = MAX_DESCRIPTION_LENGTH)
    private String description;
    @Column(name = REQUESTOR_COLUMN_NAME, nullable = false)
    private Long requestor;
    @Column(name = CREATED_COLUMN_NAME, nullable = false)
    private LocalDateTime created;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return id != null
                && Objects.equals(id, request.id)
                && Objects.equals(description, request.description)
                && Objects.equals(requestor, request.requestor)
                && Objects.equals(created, request.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, requestor, created);
    }
}
