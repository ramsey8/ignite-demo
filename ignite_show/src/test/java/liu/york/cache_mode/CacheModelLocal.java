package liu.york.cache_mode;

import liu.york.BaseConfigUtil;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.junit.Test;

import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import java.util.concurrent.TimeUnit;

public class CacheModelLocal {
    @Test
    public void fun1() throws InterruptedException {
        CacheConfiguration<String, String> cacheCfg = new CacheConfiguration<>();
        // 分区模式：数据再平衡
        cacheCfg.setCacheMode(CacheMode.PARTITIONED);
        cacheCfg.setName("localCacheName");


        TcpDiscoverySpi tcpDiscoverySpi = BaseConfigUtil.getTcpDiscoverySpi("127.0.0.1");
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setDiscoverySpi(tcpDiscoverySpi);
        cfg.setCacheConfiguration(cacheCfg);

        Ignite ignite = Ignition.start(cfg);

        IgniteCache<String, String> localCache = ignite.cache("localCacheName");
        for(int i=0;i<10;i++){
            localCache.put(String.valueOf(i),"value" + i);
        }

        while (true){
            for(int i=0;i<10;i++){
                System.out.print(localCache.get(String.valueOf(i)) + "   ");
            }
            System.out.println();
            TimeUnit.SECONDS.sleep(4);
        }
    }
}