package liu.york.fix_memory;

import liu.york.BaseConfigUtil;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class FixMemoryTest {
    @Test
    public void start() throws InterruptedException {
        TcpDiscoverySpi tcpDiscoverySpi = BaseConfigUtil.getTcpDiscoverySpi("127.0.0.1");//192.168.159.135

        DataStorageConfiguration storageCfg = new DataStorageConfiguration();
        // 新建一个region
        DataRegionConfiguration myRegionCfg = new DataRegionConfiguration();
        myRegionCfg.setPersistenceEnabled(true);
        myRegionCfg.setInitialSize(20L * 1024 * 1024);
        myRegionCfg.setMaxSize(50L * 1024 * 1024);
        myRegionCfg.setName("myFixMemory");
        // 默认路径 C:\Users\18721\AppData\Local\Temp\ignite\work

        // 新建一个默认 region
//        DataRegionConfiguration defaultRegionCfg = storageCfg.getDefaultDataRegionConfiguration();
//        defaultRegionCfg.setPersistenceEnabled(false);
//        defaultRegionCfg.setInitialSize(20L * 1024 * 1024);
//        defaultRegionCfg.setMaxSize(50L * 1024 * 1024);


        // 新建一个cache 和 fixMemory 绑定
        CacheConfiguration<String,String> myCacheCfg = new CacheConfiguration<>("myCache");
        myCacheCfg.setDataRegionName("myFixMemory");// 必须要绑定

        //新建一个cache 和 默认的 region 绑定
        CacheConfiguration<String,String> defaultCacheCfg = new CacheConfiguration<>("defaultCache");
//        defaultCacheCfg.setDataRegionName(defaultRegionCfg.getName());

        storageCfg.setDataRegionConfigurations(myRegionCfg);

        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setDiscoverySpi(tcpDiscoverySpi);
        cfg.setDataStorageConfiguration(storageCfg);
        cfg.setCacheConfiguration(myCacheCfg,defaultCacheCfg);

        Ignite ignite = Ignition.start(cfg);
        ignite.active(true);

        IgniteCache<Object, Object> myCache = ignite.cache("myCache");
        IgniteCache<Object, Object> defaultCache = ignite.cache("defaultCache");

        if(myCache != null && defaultCache != null)
        {
//            myCache.put("myName","myValue");
//            defaultCache.put("defaultName","defaultValue");

            System.out.println("get myCache ====>" + myCache.get("myName"));
            System.out.println("get defaultName ====>" + defaultCache.get("defaultName"));
        }
        else
        {
            System.out.println("not find cache");
            TimeUnit.SECONDS.sleep(2);
            ignite.close();
        }

        TimeUnit.SECONDS.sleep(2);
        ignite.close();
    }
}