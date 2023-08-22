package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "bookings")
public class Booking {
    public static final String ITEM_COLUMN_NAME = "item";
    public static final String END_COLUMN_NAME = "end_time";
    public static final String ID_COLUMN_NAME = "booking_id";
    public static final String BOOKER_COLUMN_NAME = "booker";
    public static final String STATUS_COLUMN_NAME = "status";
    public static final String START_COLUMN_NAME = "start_time";

    @Id
    @Column(name = ID_COLUMN_NAME)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = START_COLUMN_NAME, nullable = false)
    private LocalDateTime start;
    @Column(name = END_COLUMN_NAME, nullable = false)
    private LocalDateTime end;
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker;
    @Column(name = STATUS_COLUMN_NAME, nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return id != null
                && Objects.equals(id, booking.id)
                && Objects.equals(start, booking.start)
                && Objects.equals(end, booking.end)
                && Objects.equals(item, booking.item)
                && Objects.equals(booker, booking.booker)
                && status == booking.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, start, end, item, booker, status);
    }
}