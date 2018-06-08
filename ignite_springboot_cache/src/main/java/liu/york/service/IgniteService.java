package liu.york.service;

import liu.york.model.User;
import org.apache.ignite.cache.spring.SpringCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class IgniteService {

    @Cacheable("spring_cache_test")
    public String getKey(String key){
        System.out.println("====================> service coming ...");
        String value = key + "-value";
        System.out.println("====================> service end");
        return value;
    }

    // springEL
    @Cacheable(value = "spring_cache_test",key = "#user.userId")
    public User getUserById(User user){
        System.out.println("====================> service coming ...");

        System.out.println("====================> service end");
        return user;
    }
}