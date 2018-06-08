package liu.york;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.junit.Test;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;

import javax.xml.soap.Node;
import java.util.concurrent.TimeUnit;

public class QuickStart {
    @Test
    public void fileStart() throws InterruptedException {
        try (Ignite ignite = Ignition.start("example-ignite.xml")) {
            IgniteCache<String, String> cache = ignite.getOrCreateCache("cache");
            String name = cache.get("name");
            System.out.println("============>" + name);
        }

        TimeUnit.DAYS.sleep(1);
    }

    @Test
    public void fun1() throws InterruptedException {
        TcpDiscoverySpi tcpDiscoverySpi = BaseConfigUtil.getTcpDiscoverySpi("192.168.159.129");
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setDiscoverySpi(tcpDiscoverySpi);
        // local cache config
        CacheConfiguration cacheConfiguration = new CacheConfiguration("localCache");
        cacheConfiguration.setCacheMode(CacheMode.LOCAL);
        cfg.setCacheConfiguration(cacheConfiguration);

        Ignite ignite = Ignition.start(cfg);

        ClusterGroup group = ignite.cluster().forCacheNodes("localCache1");
        group.nodes().forEach(node -> {
            System.out.println("localCache ===============> "+ node.id());
        });

        System.out.println("**********************************");

        ignite.cluster().nodes().forEach(node -> {
            System.out.println("localCache ===============> "+ node.id());
        });

        TimeUnit.SECONDS.sleep(3);
        ignite.close();
    }

    @Test
    public void fun2() throws InterruptedException {
        TcpDiscoverySpi tcpDiscoverySpi = BaseConfigUtil.getTcpDiscoverySpi("localhost");//192.168.159.129
        IgniteConfiguration cfg = new IgniteConfiguration();cfg.setLocalHost("192.168.80.71");
        cfg.setDiscoverySpi(tcpDiscoverySpi);

        CacheConfiguration<String,String> cacheConfiguration = new CacheConfiguration<>("cache11");


//        cacheConfiguration.setAtomicityMode(CacheAtomicityMode.ATOMIC)
        cacheConfiguration.setCacheMode(CacheMode.LOCAL);
        cfg.setCacheConfiguration(cacheConfiguration);

        Ignite ignite = Ignition.start(cfg);

        IgniteCache<String, String> cache = ignite.cache("cache11");

        cache.put("key1","value1");
        cache.put("key2","value2");
        cache.put("key3","value3");

        while (true){
            String value1 = cache.get("key1");
            String value2 = cache.get("key2");
            String value3 = cache.get("key3");
            System.out.println(String.format("key1=%s   key2=%s   key3=%s",value1,value2,value3));
            TimeUnit.SECONDS.sleep(2);
        }

    }

    @Test
    public void fun3() throws InterruptedException {
        TcpDiscoverySpi tcpDiscoverySpi = BaseConfigUtil.getTcpDiscoverySpi("192.168.159.135");

        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setDiscoverySpi(tcpDiscoverySpi);
        Ignite ignite = Ignition.start(cfg);

        TimeUnit.DAYS.sleep(1);

    }

}