package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.controller.State;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.api.BookingRepository;
import ru.practicum.shareit.booking.service.api.BookingService;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.api.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.api.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Transactional
    @Override
    public BookingOutputDto addBooking(BookingInputDto bookingInputDto, long bookerId) {
        validateBookingData(bookingInputDto);
        Item item = findItemById(bookingInputDto.getItemId());
        if (!item.getAvailable()) {
            log.error("BookingService: item with id={} not available", item.getId());
            throw new NotAvailableException(
                    String.format("Item with id=%d not available", item.getId()));
        }
        if (isAlreadyBooked(bookingInputDto)) {
            log.error("BookingService: item with id={} not available", item.getId());
            throw new NotAvailableException(
                    String.format("Item with id=%d not available", item.getId()));
        }
        if (item.getOwner().getId() == (bookerId)) {
            log.error("BookingService: bookerId={} equals ownerId of item with id={} ", bookerId, item.getId());
            throw new NotFoundException(
                    String.format("bookerId=%d equals ownerId of item with id=%d", bookerId, item.getId()));
        }
        User booker = findUserById(bookerId);
        Booking newBooking = bookingMapper.toBooking(bookingInputDto, item, booker);
        Booking addedBooking = bookingRepository.save(newBooking);
        log.info("bookingService: was add booking={}", addedBooking);
        return bookingMapper.toBookingOutputDto(addedBooking);
    }

    @Transactional
    @Override
    public BookingOutputDto updateBooking(long bookingId, long ownerId, boolean isApproved) {
        findUserById(ownerId);
        Booking booking = findBookingById(bookingId);
        if (booking.getItem().getOwner().getId() != ownerId) {
            log.error("BookingService: only owner have access to item");
            throw new NotFoundException("only owner have access to item");
        }
        if (booking.getStatus() != Status.WAITING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("BookingService: booking with bookingId=%d not WAITING status", bookingId));
        }
        Status newStatus = isApproved ? Status.APPROVED : Status.REJECTED;
        booking.setStatus(newStatus);

        return bookingMapper.toBookingOutputDto(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    @Override
    public BookingOutputDto getBookingByIdAndBookerId(long bookingId, long userId) {
        findUserById(userId);
        Booking booking = findBookingByIdAndUserId(bookingId, userId);
        log.info("bookingService: was returned booking={}, by id={}, userId={}", booking, bookingId, userId);
        return bookingMapper.toBookingOutputDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingOutputDto> getAllUsersBookings(Long bookerId, State state) {
        findUserById(bookerId);
        List<Booking> allUsersBookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                allUsersBookings =
                        bookingRepository.findAllByBooker_Id(bookerId,
                                Sort.by(Sort.Direction.DESC, "id"));
                break;
            case PAST:
                allUsersBookings =
                        bookingRepository.findAllByBooker_IdAndEndBefore(bookerId, now,
                                Sort.by(Sort.Direction.DESC, "id"));
                break;
            case FUTURE:
                allUsersBookings =
                        bookingRepository.findAllByBooker_IdAndStartAfter(bookerId, now,
                                Sort.by(Sort.Direction.DESC, "id"));
                break;
            case CURRENT:
                allUsersBookings =
                        bookingRepository.findAllByBooker_IdAndStartBeforeAndEndAfter(bookerId,
                                now,
                                now,
                                Sort.by(Sort.Direction.ASC, "id"));
                break;
            case WAITING:
                allUsersBookings =
                        bookingRepository.findAllByBooker_IdAndStatus(bookerId, Status.WAITING,
                                Sort.by(Sort.Direction.DESC, "id"));
                break;
            case REJECTED:
                allUsersBookings =
                        bookingRepository.findAllByBooker_IdAndStatus(bookerId, Status.REJECTED,
                                Sort.by(Sort.Direction.DESC, "id"));
                break;
        }
        List<BookingOutputDto> allBookingsDto = bookingMapper.map(allUsersBookings);
        log.info("bookingService: was returned all {} bookings for bookerId={}", allBookingsDto.size(), bookerId);
        return allBookingsDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingOutputDto> getAllOwnersBookings(Long ownerId, State state) {
        findUserById(ownerId);
        List<Booking> allUsersBookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                allUsersBookings =
                        bookingRepository.findAllByItemOwnerId(ownerId,
                                Sort.by(Sort.Direction.DESC, "id"));
                break;
            case PAST:
                allUsersBookings =
                        bookingRepository.findAllByItemOwnerIdAndEndBefore(ownerId, now,
                                Sort.by(Sort.Direction.DESC, "id"));
                break;
            case FUTURE:
                allUsersBookings =
                        bookingRepository.findAllByItemOwnerIdAndStartAfter(ownerId, now,
                                Sort.by(Sort.Direction.DESC, "id"));
                break;
            case CURRENT:
                allUsersBookings =
                        bookingRepository.findAllByItem_Owner_IdAndStartBeforeAndEndAfter(ownerId,
                                now,
                                now,
                                Sort.by(Sort.Direction.DESC, "id"));
                break;
            case WAITING:
                allUsersBookings =
                        bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, Status.WAITING,
                                Sort.by(Sort.Direction.DESC, "id"));
                break;
            case REJECTED:
                allUsersBookings =
                        bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, Status.REJECTED,
                                Sort.by(Sort.Direction.DESC, "id"));
                break;
        }
        List<BookingOutputDto> allBookingsDto = bookingMapper.map(allUsersBookings);
        log.info("bookingService: was returned all {} bookings for ownerId={}", allBookingsDto.size(), ownerId);
        return allBookingsDto;
    }

    private Booking findBookingByIdAndUserId(Long bookingId, Long userId) {
        Booking booking = findBookingById(bookingId);
        if (booking.getBooker().getId().equals(userId) ||
                booking.getItem().getOwner().getId().equals(userId)) {
            return booking;
        }
        throw new NotFoundException(String.format("booking with id=%d with (bookerId or ownerId)=%d not found",
                bookingId,
                userId));
    }

    private void validateBookingData(BookingInputDto bookingInputDto) {
        if (!bookingInputDto.getEnd().isAfter(bookingInputDto.getStart())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "end date must be after start date");
        }
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("user with id=%d not found", userId)));
    }

    private Item findItemById(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("item with id=%d not found", itemId)));
    }

    private Booking findBookingById(long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException(String.format("booking with id=%d not found", bookingId)));
    }


    private boolean isAlreadyBooked(BookingInputDto bookingInputDto) {
        Optional<Booking> doubleBooking = bookingRepository.findFirstByItem_IdAndEndAfterAndStartBeforeAndStatus(
                bookingInputDto.getItemId(),
                bookingInputDto.getStart(),
                bookingInputDto.getEnd(),
                Status.APPROVED);
        return doubleBooking.isPresent();
    }

}
