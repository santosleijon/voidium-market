package com.github.santosleijon.voidiummarket;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest
@EmbeddedKafka(partitions = 1)
class VoidiumMarketApplicationTests {

	@Test
	void contextLoads() {
	}

}
