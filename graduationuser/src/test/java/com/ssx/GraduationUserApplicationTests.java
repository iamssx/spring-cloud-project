package com.ssx;

import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GraduationUserApplicationTests {

	@Test
	public void contextLoads() {
		Long number = (Long) NumberUtils.createNumber("10");

	}

}
