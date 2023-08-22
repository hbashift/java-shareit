package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDetailedDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.dto.BookingPostResponseDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exceptions.InvalidBookingException;
import ru.practicum.shareit.exceptions.UnavailableBookingException;
import ru.practicum.shareit.exceptions.UnsupportedStatusException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static ru.practicum.shareit.booking.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;
import static ru.practicum.shareit.booking.State.*;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    public static final String ILLEGAL_SATE_MESSAGE = "  state: ";
    public static final String INVALID_BUCKING = "нельзя забронировать свою же вещь";
    public static final String SATE_ALREADY_SET_MESSAGE = "статус уже выставлен state: ";
    public static final String BOOKING_INVALID_MESSAGE = "недопустимые значения времени бронирования: ";
    public static final String UNAVAILABLE_BOOKING_MESSAGE = "в данный момент невозможно забронировать item: ";
    public static final String DENIED_PATCH_ACCESS_MESSAGE = "пользователь не является владельцем вещи userId: ";
    public static final String DENIED_ACCESS_MESSAGE = "пользователь не является владельцем вещи или брони userId: ";

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingPostResponseDto createBooking(BookingPostDto dto, Long userId) {
        if (!isStartBeforeEnd(dto)) {
            throw new IllegalArgumentException(BOOKING_INVALID_MESSAGE +
                    "start: " + dto.getStart() + " end: " + dto.getEnd() + " now: ");
        }

        User user = userRepository.findById(userId).orElseThrow();
        Item item = itemRepository.findById(dto.getItemId()).orElseThrow();

        if (userId.equals(item.getOwner())) {
            throw new InvalidBookingException(INVALID_BUCKING);
        }

        if (!item.getAvailable()) {
            throw new UnavailableBookingException(UNAVAILABLE_BOOKING_MESSAGE + item.getId());
        }

        Booking booking = BookingMapper.toModel(dto, item, user);
        booking = bookingRepository.save(booking);
        return BookingMapper.toPostResponseDto(booking, item);
    }

    @Override
    public BookingResponseDto patchBooking(Long bookingId, Boolean approved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow();

        if (!item.getOwner().equals(userId)) {
            throw new NoSuchElementException(DENIED_PATCH_ACCESS_MESSAGE + userId + " itemId: " + item.getId());
        }
        BookingStatus status = convertToStatus(approved);

        if (booking.getStatus().equals(status)) {
            throw new IllegalArgumentException(SATE_ALREADY_SET_MESSAGE + status);
        }

        booking.setStatus(status);
        booking = bookingRepository.save(booking);
        return BookingMapper.toResponseDto(booking, booking.getBooker(), item);
    }

    @Override
    public BookingDetailedDto findById(Long bookingId, Long userId) {
        checkIfUserExists(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        Long itemOwner = booking.getItem().getOwner();
        Long bookingOwner = booking.getBooker().getId();
        boolean itemOrBookingOwner = userId.equals(bookingOwner) || userId.equals(itemOwner);

        if (!itemOrBookingOwner) {
            throw new NoSuchElementException(DENIED_ACCESS_MESSAGE + userId);
        }
        return BookingMapper.toDetailedDto(booking);
    }

    @Override
    public List<BookingDetailedDto> findAllByBooker(String state, Long userId) {
        State status = parseState(state);
        checkIfUserExists(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;
        Sort sort = Sort.by("start").descending();

        switch (status) {
            case REJECTED :
                bookings = bookingRepository
                .findByBookerIdAndStatus(userId, REJECTED, sort);
                break;
            case WAITING :
                bookings = bookingRepository
                .findByBookerIdAndStatus(userId, WAITING, sort);
                break;
            case CURRENT :
                bookings = bookingRepository.findByBookerIdCurrent(userId, now);
                break;
            case FUTURE :
                bookings = bookingRepository
                .findByBookerIdAndStartIsAfter(userId, now, sort);
                break;
            case PAST :
                bookings = bookingRepository
                .findByBookerIdAndEndIsBefore(userId, now, sort);
                break;
            case ALL :
                bookings = bookingRepository.findByBookerId(userId, sort);
                break;
            default :
                throw new IllegalArgumentException(ILLEGAL_SATE_MESSAGE);
        }
        return BookingMapper.toListDetailedDto(bookings);
    }

    @Override
    public List<BookingDetailedDto> findAllByItemOwner(String stateValue, Long userId) {
        State state = parseState(stateValue);
        checkIfUserExists(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;
        Sort sort = Sort.by("start").descending();

        switch (state) {
            case REJECTED :
                bookings = bookingRepository
                .findBookingByItemOwnerAndStatus(userId, REJECTED, sort);
                break;
            case WAITING :
                bookings = bookingRepository
                .findBookingByItemOwnerAndStatus(userId, WAITING, sort);
                break;
            case CURRENT :
                bookings = bookingRepository.findBookingsByItemOwnerCurrent(userId, now);
                break;
            case FUTURE :
                bookings = bookingRepository
                .findBookingByItemOwnerAndStartIsAfter(userId, now, sort);
                break;
            case PAST :
                bookings = bookingRepository
                .findBookingByItemOwnerAndEndIsBefore(userId, now, sort);
                break;
            case ALL :
                bookings = bookingRepository
                .findBookingByItemOwner(userId, sort);
                break;
            default :
                throw new IllegalArgumentException(ILLEGAL_SATE_MESSAGE);
        }
        return BookingMapper.toListDetailedDto(bookings);
    }

    private void checkIfUserExists(Long userId) {
        userRepository.findById(userId).orElseThrow();
    }

    private State parseState(String state) {
        State status;
        try {
            status = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException(ILLEGAL_SATE_MESSAGE + state);
        }
        return status;
    }

    private boolean isStartBeforeEnd(BookingPostDto dto) {
        return dto.getStart().isBefore(dto.getEnd());
    }

    private BookingStatus convertToStatus(Boolean approved) {
        return approved ? BookingStatus.APPROVED : REJECTED;
    }
}