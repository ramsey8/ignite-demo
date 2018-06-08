package liu.york.cache_timeout;

import liu.york.BaseConfigUtil;
import liu.york.cache_select.User;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.junit.Test;

import javax.cache.expiry.*;
import java.util.concurrent.TimeUnit;

public class TimeOutCache {
    @Test
    public void server() throws InterruptedException {

        TcpDiscoverySpi tcpDiscoverySpi = BaseConfigUtil.getTcpDiscoverySpi("127.0.0.1");
        CacheConfiguration<String, User> cacheConfiguration = new CacheConfiguration<>("cache");
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setDiscoverySpi(tcpDiscoverySpi);
        cfg.setCacheConfiguration(cacheConfiguration);
        Ignite ignite = Ignition.start(cfg);

        IgniteCache<String, String> cache = ignite.cache("cache");
        cache.put("key1","value1");
        IgniteCache<String, String> cache1 = cache.withExpiryPolicy(new TouchedExpiryPolicy(new Duration(TimeUnit.SECONDS, 5)));
        cache1.put("key2","value2");

        while (true){
            System.out.println("key1=" + cache1.get("key1"));
            System.out.println("key2=" + cache1.get("key2"));
            TimeUnit.SECONDS.sleep(2);
        }
    }
}