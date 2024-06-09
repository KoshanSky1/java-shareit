package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_Id(long bookerId);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start,
                                                               LocalDateTime end);

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end);

    List<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime end);

    List<Booking> findByBooker_IdAndStatus(Long bookerId, BookingStatus status);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "join i.owner as o " +
            "where o.id = ?1 ")
    List<Booking> findByOwner_Id(long bookerId);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "join i.owner as o " +
            "where o.id = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?3 ")
    List<Booking> findByOwner_IdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start,
                                                              LocalDateTime end);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "join i.owner as o " +
            "where o.id = ?1 " +
            "and b.end < ?2 ")
    List<Booking> findByOwner_IdAndEndIsBefore(Long bookerId, LocalDateTime end);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "join i.owner as o " +
            "where o.id = ?1 " +
            "and b.start > ?2 ")
    List<Booking> findBOwner_IdAndStartIsAfter(Long bookerId, LocalDateTime end);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "join i.owner as o " +
            "where o.id = ?1 " +
            "and b.status like ?2 ")
    List<Booking> findByOwner_IdAndStatus(long bookerId, BookingStatus status);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.id = ?1 ")
    List<Booking> findByItem_Id(long itemId);

    Booking findFirstByItem_IdAndStartBeforeAndStatusOrderByEndDesc(Long itemId, LocalDateTime start,
                                                                  BookingStatus status);

    Booking findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime start,
                                                                    BookingStatus status);

}