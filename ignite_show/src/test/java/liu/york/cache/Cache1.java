package liu.york.cache;

import liu.york.BaseConfigUtil;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.junit.Test;

import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Cache1 {
    @Test
    public void fun() throws InterruptedException {

        TcpDiscoverySpi tcpDiscoverySpi = BaseConfigUtil.getTcpDiscoverySpi("127.0.0.1");
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setDiscoverySpi(tcpDiscoverySpi);
        cfg.setLocalHost("localhost");
        CacheConfiguration cacheConfiguration = new CacheConfiguration();
        cacheConfiguration.setName("cache");
//        cacheConfiguration.setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS,5)));

        cfg.setCacheConfiguration(cacheConfiguration);

        Ignite ignite = Ignition.start(cfg);
        IgniteCache<String, String> cache = ignite.getOrCreateCache("cache");

        IgniteCache<String, String> cache1 = cache.withExpiryPolicy(new CreatedExpiryPolicy(new Duration(TimeUnit.SECONDS, 2)));
        cache.put("key1","value1");
        cache1.put("key","value");
        while (true){
//            System.out.println("cache =====>" + cache.get("key1"));
            System.out.println("cache1 =====>" + cache.get("key"));
            TimeUnit.SECONDS.sleep(2);
        }
    }
}