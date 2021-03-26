package nl.tjonahen.resto;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RabbitListenerTest(spy = false, capture = false)
public class BarApplicationTests {

	@Test
	public void contextLoads() {
            // for now a placeholder to have actual integration tests
	}

}
