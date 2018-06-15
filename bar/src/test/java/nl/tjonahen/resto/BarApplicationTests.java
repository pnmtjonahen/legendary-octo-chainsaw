package nl.tjonahen.resto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@RabbitListenerTest(spy = false, capture = false)
public class BarApplicationTests {

	@Test
	public void contextLoads() {
	}

}
