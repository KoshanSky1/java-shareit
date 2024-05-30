package ru.practicum.shareit;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.user.UserController;

@SpringBootTest
class ShareItTests {
	@Autowired
	private UserController userController;

	@Autowired
	private ItemController itemController;

	@Test
	void contextLoads() {
		Assertions.assertThat(userController).isNotNull();
		Assertions.assertThat(itemController).isNotNull();
	}

}
