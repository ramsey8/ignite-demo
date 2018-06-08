package liu.york.cache_expiry;

import liu.york.BaseConfigUtil;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.junit.Test;

import javax.cache.expiry.*;
import java.util.concurrent.TimeUnit;

public class CacheExpiryPolicy {
    @Test
    public void testExpiry() throws InterruptedException {
        // 最后创建时间
        CacheConfiguration cacheCreatedExpiryPolicy  = new CacheConfiguration("cache1");
        // 最后访问时间
        CacheConfiguration cacheAccessedExpiryPolicy = new CacheConfiguration("cache2");
        // 最后更新时间
        CacheConfiguration cacheModifiedExpiryPolicy = new CacheConfiguration("cache3");
        // 最后更新、访问时间
        CacheConfiguration cacheTouchedExpiryPolicy  = new CacheConfiguration("cache4");
        // 不过期
        CacheConfiguration cacheEternalExpiryPolicy  = new CacheConfiguration("cache5");

        cacheCreatedExpiryPolicy.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS,5)));
        cacheAccessedExpiryPolicy.setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS,5)));
        cacheModifiedExpiryPolicy.setExpiryPolicyFactory(ModifiedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS,5)));
        cacheTouchedExpiryPolicy.setExpiryPolicyFactory(TouchedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS,5)));
        cacheEternalExpiryPolicy.setExpiryPolicyFactory(EternalExpiryPolicy.factoryOf());

        TcpDiscoverySpi tcpDiscoverySpi = BaseConfigUtil.getTcpDiscoverySpi("127.0.0.1");
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setCacheConfiguration(
                cacheCreatedExpiryPolicy,
                cacheAccessedExpiryPolicy,
                cacheModifiedExpiryPolicy,
                cacheTouchedExpiryPolicy,
                cacheEternalExpiryPolicy);
        cfg.setDiscoverySpi(tcpDiscoverySpi);
        cfg.setLocalHost("localhost");
        Ignite ignite = Ignition.start(cfg);

        IgniteCache<Object, Object> cache1 = ignite.cache("cache1");cache1.put("key","value");
        IgniteCache<Object, Object> cache2 = ignite.cache("cache2");cache2.put("key","value");
        IgniteCache<Object, Object> cache3 = ignite.cache("cache3");cache3.put("key","value");
        IgniteCache<Object, Object> cache4 = ignite.cache("cache4");cache4.put("key","value");
        IgniteCache<Object, Object> cache5 = ignite.cache("cache5");cache5.put("key","value");

        IgniteCache<Object, Object> cache6 = cache5.withExpiryPolicy(new CreatedExpiryPolicy(new Duration(TimeUnit.SECONDS, 5)));
        cache6.put("key6","value");
        System.out.println(cache5 == cache6);
        // 测试最后访问时间  最后应该是 2 4 5 还有值
        while (true){
            System.out.println("cache1 ==========> " + cache1.get("key"));
            System.out.println("cache2 ==========> " + cache2.get("key"));
            System.out.println("cache3 ==========> " + cache3.get("key"));
            System.out.println("cache4 ==========> " + cache4.get("key"));
            System.out.println("cache5 ==========> " + cache5.get("key"));
            System.out.println("cache6 ==========> " + cache5.get("key6"));
            TimeUnit.SECONDS.sleep(2);
        }

        // 测试最后更新时间  最后应该是 3 4 5 还有值
//        TimeUnit.SECONDS.sleep(3);
//        cache1.put("key","value1");
//        cache2.put("key","value2");
//        cache3.put("key","value3");
//        cache4.put("key","value4");
//        cache5.put("key","value5");
//        TimeUnit.SECONDS.sleep(3);
//        System.out.println("cache1 ==========> " + cache1.get("key"));
//        System.out.println("cache2 ==========> " + cache2.get("key"));
//        System.out.println("cache3 ==========> " + cache3.get("key"));
//        System.out.println("cache4 ==========> " + cache4.get("key"));
//        System.out.println("cache5 ==========> " + cache5.get("key"));
//        ignite.close();
    }
}