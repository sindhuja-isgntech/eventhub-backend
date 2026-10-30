package com.isigntech.eventhub;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "app.jwt.secret=test-secret-key-that-is-at-least-32-bytes-long")
class EventhubApplicationTests {

	@Test
	void contextLoads() {
	}

}
