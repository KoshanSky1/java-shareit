package ru.practicum.shareit;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.user.UserController;

@SpringBootTest
class ShareItTests {
    @Autowired
    private UserController userController;

    @Autowired
    private ItemController itemController;

    @Autowired
    private BookingController bookingController;

    @Autowired
    private ItemRequestController itemRequestController;

    @Test
    void contextLoads() {
        Assertions.assertThat(userController).isNotNull();
        Assertions.assertThat(itemController).isNotNull();
        Assertions.assertThat(bookingController).isNotNull();
        Assertions.assertThat(itemRequestController).isNotNull();
    }

}