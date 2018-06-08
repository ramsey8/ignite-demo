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

import java.util.concurrent.TimeUnit;

public class Cache2 {
    @Test
    public void fun1() throws InterruptedException {
        TcpDiscoverySpi tcpDiscoverySpi = BaseConfigUtil.getTcpDiscoverySpi("192.168.159.129");

//        CacheConfiguration cacheConfiguration = new CacheConfiguration("cache1");
//        cacheConfiguration.setCacheMode(CacheMode.LOCAL);

        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setDiscoverySpi(tcpDiscoverySpi);
//        cfg.setCacheConfiguration(cacheConfiguration);
        cfg.setClientMode(true);

        Ignite ignite = Ignition.start(cfg);
        IgniteCache<String, String> cache1 = ignite.getOrCreateCache("cache1");
        cache1.put("key1","value1");
        cache1.put("key2","value2");
        cache1.put("key3","value3");
        TimeUnit.DAYS.sleep(1);
    }
}