package com.inventory;

import org.junit.jupiter.api.Test;
import com.inventory.config.TestConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Import(TestConfig.class) // Import test configuration
@ActiveProfiles("test")   // Activate test profile
class InventoryApplicationTests {

	@Test
	void contextLoads() {
	}

}
