package liu.york.advanced_query;

import liu.york.BaseConfigUtil;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.junit.Test;

import javax.cache.Cache;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ScanQueryKeyValue {

    private final int Num = 1_000_000;
    final StringBuilder sb = new StringBuilder();

    @Test
    public void scanQuery() throws InterruptedException {
        /*
         * local node test
         * put  4500-4800
         * query 6-9
         */
        scanQuery0("127.0.0.1");


        /*
         * cluster node test
         * put 7587
         * query 78
         */
//        scanQuery0("192.168.159.135");
    }

    public void scanQuery0(String ip) throws InterruptedException {
        TcpDiscoverySpi tcpDiscoverySpi = BaseConfigUtil.getTcpDiscoverySpi(ip);

        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setDiscoverySpi(tcpDiscoverySpi);

        Ignite ignite = Ignition.start(cfg);
        IgniteCache<String, String> cache = ignite.getOrCreateCache("cache");

        // 预热 、 休息两秒
        warmUp(cache,1000);

        List<String> valueList = Arrays.asList("VALUE0","VALUE1","VALUE2","VALUE3",
                "VALUE4","VALUE5","VALUE6","VALUE7","VALUE8","VALUE9");
        System.out.println("Start put values ....");
        long putStart = System.currentTimeMillis();
        for(int i = 0;i < Num;i++)
        {
            cache.put(String.valueOf(i),valueList.get(i%10));
        }
        System.out.println("End put values,spend time is ===> " + (System.currentTimeMillis() - putStart));

        try (QueryCursor<Cache.Entry<String, String>> query =
                     cache.query(new ScanQuery<String, String>((k, v) -> StringUtils.equals("VALUE1",v)))){
            int i = 0;
            Iterator<Cache.Entry<String, String>> iterator = query.iterator();
            while (iterator.hasNext()) {
                iterator.next();i++;
            }
            System.out.println("query size ====> " + i);
        }
        TimeUnit.SECONDS.sleep(5);
        ignite.close();
    }

    /**
     * 预热
     * @param cache
     * @param times
     */
    private void warmUp(IgniteCache<String, String> cache,int times) throws InterruptedException {
        final String keyName = "warm-up-key";
        for (int i = 0;i < times; i++)
        {
            sb.setLength(0);
            cache.put(sb.append(keyName).append(i).toString(), "value");
        }
        for (int i = 0;i < times; i++)
        {
            sb.setLength(0);
            cache.remove(sb.append(keyName).append(i).toString());
        }
        TimeUnit.SECONDS.sleep(2);
    }
}