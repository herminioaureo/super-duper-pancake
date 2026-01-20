package com.herminio.superduper.pancake;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PancakeApplicationTests {

	final String validEmail = "myemail@example.com";
	final String invalidEmail = "myemail@.com";

	@Test
	public void testEmailRegex_validEmail() {
		assert(com.herminio.superduper.pancake.util.Util.isValidEmail(validEmail));
	}

	@Test
	public void testEmailRegex_invalidEmail() {
		assert(!com.herminio.superduper.pancake.util.Util.isValidEmail(invalidEmail));
	}

}
