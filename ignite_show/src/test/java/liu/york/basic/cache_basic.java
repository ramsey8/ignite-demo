package liu.york.basic;

import liu.york.BaseConfigUtil;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.junit.Test;

public class cache_basic {
    @Test
    public void fun1(){
        TcpDiscoverySpi tcpDiscoverySpi = BaseConfigUtil.getTcpDiscoverySpi("127.0.0.1");

        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setDiscoverySpi(tcpDiscoverySpi);
        cfg.setLocalHost("localhost");

        Ignite ignite = Ignition.start(cfg);

        IgniteCache<String, String> cache = ignite.getOrCreateCache("cache");

        cache.put("key1","value1");
        cache.put("key2","value2");
        cache.put("key3","value3");

        System.out.println("key1  ============> " + cache.get("key1"));
        System.out.println("key2  ============> " + cache.get("key2"));
        System.out.println("key3  ============> " + cache.get("key3"));

        ignite.close();
    }
}