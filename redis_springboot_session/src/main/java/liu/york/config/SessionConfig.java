package liu.york.config;

import org.springframework.context.annotation.ImportResource;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;

//@EnableRedisHttpSession
@EnableSpringHttpSession
@ImportResource("classpath:spring-ignite-session.xml")
public class SessionConfig {
//    @Bean
//    public JedisConnectionFactory getRedisFactory(){
//        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
//        return jedisConnectionFactory;
//    }
}