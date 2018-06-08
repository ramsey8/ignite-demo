package liu.york.transaction;

import liu.york.BaseConfigUtil;
import liu.york.cache_select.User;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteTransactions;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.transactions.Transaction;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class CacheTransaction {
    @Test
    public void server() throws InterruptedException {
        TcpDiscoverySpi tcpDiscoverySpi = BaseConfigUtil.getTcpDiscoverySpi("127.0.0.1");
        CacheConfiguration<String, String> cacheConfiguration = new CacheConfiguration<>("cache");

        // 默认是原子模式，开启事务必须手动开启
        cacheConfiguration.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);

        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setDiscoverySpi(tcpDiscoverySpi);
        cfg.setLocalHost("localhost");
        cfg.setCacheConfiguration(cacheConfiguration);
        Ignite ignite = Ignition.start(cfg);

        IgniteCache<String, String> cache = ignite.cache("cache");
        TimeUnit.SECONDS.sleep(2);

        IgniteTransactions transactions = ignite.transactions();
        try (Transaction tx = transactions.txStart()) {
            cache.put("key1", "value1");
            cache.put("key2", "value2");
             throw new RuntimeException("");
//            tx.commit();
        }catch (Exception e){
            System.out.println("Catch exception : " + e.getMessage());
        }

        System.out.println("key1 ========> " + cache.get("key1"));
        System.out.println("key2 ========> " + cache.get("key2"));

        TimeUnit.SECONDS.sleep(2);
        ignite.close();
    }

//    @Test
//    public void client(){
//        TcpDiscoverySpi tcpDiscoverySpi = BaseConfigUtil.getTcpDiscoverySpi("127.0.0.1");
//        IgniteConfiguration cfg = new IgniteConfiguration();
//        cfg.setClientMode(true);
//        cfg.setDiscoverySpi(tcpDiscoverySpi);
//        Ignite ignite = Ignition.start(cfg);
//        IgniteCache<String, String> cache = ignite.cache("cache");
//        System.out.println(cache.get("key1"));
//        System.out.println(cache.get("key2"));
//        ignite.close();
//    }
}