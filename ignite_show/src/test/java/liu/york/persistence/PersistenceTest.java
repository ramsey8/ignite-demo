package liu.york.persistence;

import liu.york.BaseConfigUtil;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class PersistenceTest {
    @Test
    public void enable() throws InterruptedException {
        TcpDiscoverySpi tcpDiscoverySpi = BaseConfigUtil.getTcpDiscoverySpi("127.0.0.1");
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setDiscoverySpi(tcpDiscoverySpi);
        cfg.setLocalHost("localhost");

        DataStorageConfiguration storageCfg = new DataStorageConfiguration();
        DataRegionConfiguration defaultDataRegionConfiguration = storageCfg.getDefaultDataRegionConfiguration();
//        defaultDataRegionConfiguration.setInitialSize(100L * 1024 * 1024); // 设置初始化内存大小
//        defaultDataRegionConfiguration.setMaxSize(500L * 1024 * 1024);     // 设置最大值内存大小
//        defaultDataRegionConfiguration.setPersistenceEnabled(true);
        // 默认路径 C:\Users\18721\AppData\Local\Temp\ignite\work
//        storageCfg.setStoragePath("F:\\dev\\ignite\\filePath");            // 自定义持久化路径
//        storageCfg.setWalPath("F:\\dev\\ignite\\filePath");                // 自定义持久化路径
        cfg.setDataStorageConfiguration(storageCfg);

        Ignite ignite = Ignition.start(cfg);
//        ignite.active(true);


        IgniteCache<String, String> cache = ignite.getOrCreateCache("cache");
        cache.put("name","value2");

        System.out.println(cache.get("name"));


        TimeUnit.SECONDS.sleep(2);
        ignite.close();
    }
}