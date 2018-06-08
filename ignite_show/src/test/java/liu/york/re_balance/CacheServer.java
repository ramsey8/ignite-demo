package liu.york.re_balance;

import liu.york.BaseConfigUtil;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CacheServer {
    @Test
    public void server() throws InterruptedException {
        TcpDiscoverySpi tcpDiscoverySpi = BaseConfigUtil.getTcpDiscoverySpi("127.0.0.1");
        CacheConfiguration<String, String> cacheConfiguration = new CacheConfiguration<>("cache");
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setDiscoverySpi(tcpDiscoverySpi);
        cfg.setCacheConfiguration(cacheConfiguration);

        Ignite ignite = Ignition.start(cfg);
        IgniteCache<String, String> cache = ignite.cache("cache");

        HashSet<String> set = new HashSet<>();
        for(int i=0;i<10;i++){
            cache.put("key" + i, "value" + i);
            set.add("key" + i);
        }

        while (true){
            Map<String, String> all = cache.getAll(set);
            all.forEach((k,v) -> System.out.print(k + "  "));
            System.out.println();
            TimeUnit.SECONDS.sleep(2);
        }
    }
}