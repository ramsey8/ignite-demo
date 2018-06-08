package liu.york.backup;

import liu.york.BaseConfigUtil;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.junit.Test;

import javax.xml.crypto.dsig.keyinfo.KeyName;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BackCache {
    @Test
    public void fun1() throws InterruptedException {
        TcpDiscoverySpi tcpDiscoverySpi = BaseConfigUtil.getTcpDiscoverySpi("127.0.0.1");
        CacheConfiguration<String, String> cacheConfiguration = new CacheConfiguration<>("cache");
        cacheConfiguration.setCacheMode(CacheMode.PARTITIONED);
        // TODO: 2018/5/29
        cacheConfiguration.setBackups(1);
        

        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setDiscoverySpi(tcpDiscoverySpi);
        cfg.setCacheConfiguration(cacheConfiguration);

        Ignite ignite = Ignition.start(cfg);

        IgniteCache<String, String> cache = ignite.cache("cache");

        for(int i=0;i<10;i++){
            cache.put(String.valueOf(i), "value" + i);
        }

        while (true){
            for(int i=0;i<10;i++){
                System.out.print(cache.get(String.valueOf(i)) + "   ");
            }
            System.out.println();
            TimeUnit.SECONDS.sleep(3);
        }
    }
}