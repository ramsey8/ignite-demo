package liu.york;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableCaching
public class IgniteSpringBootCacheApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(IgniteSpringBootCacheApplication.class, args);
	}
}
