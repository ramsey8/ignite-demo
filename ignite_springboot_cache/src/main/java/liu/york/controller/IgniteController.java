package liu.york.controller;

import liu.york.model.User;
import liu.york.service.IgniteService;
import org.apache.ignite.cache.spring.SpringCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/ignite_cache")
public class IgniteController {

    @Autowired
    private IgniteService igniteService;
    @Autowired
    private SpringCacheManager springCacheManager;

    @RequestMapping("/fun1")
    public void fun1(){
        Collection<String> cacheNames = springCacheManager.getCacheNames();

        cacheNames.forEach(name -> System.out.println(name));

        Cache cache = springCacheManager.getCache("spring_cache_test");
        String name = cache.get("name", String.class);
        System.out.println(" name ============> " + name);

    }

    @RequestMapping("/getValue")
    public void getValue(@RequestParam("key") String key){
        System.out.println("======================> request coming ...");

        String name = igniteService.getKey(key);
        System.out.println("get value from cache by key =============> " + name);

        System.out.println("======================> request end");
    }

    @RequestMapping("/getUser")
    public void getUser(@RequestParam("userId") String userId){
        System.out.println("======================> request coming ...");
        User user = new User();
        user.setUserId(userId);
        user.setUserName("LiuYork");
        user.setAge(26);

        User queryUser = igniteService.getUserById(user);
        System.out.println(queryUser);
    }

}