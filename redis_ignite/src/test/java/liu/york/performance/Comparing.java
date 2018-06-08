package liu.york.performance;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import javax.cache.Cache;
import java.util.Arrays;
import java.util.Iterator;

public class Comparing {
    private static final String IP = "192.168.159.136";//192.168.159.135
    private int NUM = 1_000_000;

    @Test
    public void redisSingle(){
        NUM = 1_00_000;
        Jedis jedis = new Jedis(IP);
        // 25299
        System.out.println("start insert ...");
        long startTime = System.currentTimeMillis();
        for(int i=0;i<NUM;i++)
        {
            jedis.set(String.valueOf(i),"redis_value");
            if(i%10000 == 0) System.out.println("save -> " + i);
        }
        System.out.println("redis single spend =======> " + (System.currentTimeMillis() - startTime));
        jedis.close();
    }

    @Test
    public void igniteSingle(){
        NUM = 1_00_000;
        Ignite ignite = getIgniteCli(false);

        // server：24811     client: 43617
        IgniteCache<String, String> cache = ignite.getOrCreateCache("cache");
        System.out.println("start insert ...");
        long startTime = System.currentTimeMillis();
        for(int i=0;i<NUM;i++)
        {
            cache.put(String.valueOf(i),"ignite_value");
            if(i%10000 == 0) System.out.println("save -> " + i);
        }
        System.out.println("ignite single spend =======> " + (System.currentTimeMillis() - startTime));
        cache.removeAll();
        cache.close();
        cache.close();
    }

    @Test
    public void redisBatch(){
        NUM = 1_000_000;
        Jedis jedis = new Jedis(IP);
        Pipeline pipeline = jedis.pipelined();
        // 1722
        System.out.println("start insert ...");
        long startTime = System.currentTimeMillis();
        for(int i=0;i<NUM;i++)
        {
            pipeline.set(String.valueOf(i),"redis_value");
            if(i%10000 == 0) System.out.println("save -> " + i);
        }
        pipeline.sync();
        System.out.println("redis batch spend =======> " + (System.currentTimeMillis() - startTime));
        jedis.close();
    }

    @Test
    public void igniteBatch(){
        NUM = 1_000_000;
        Ignite ignite = getIgniteCli(false);
        // 4324
        IgniteCache<String, String> cache = ignite.getOrCreateCache("cache");
        IgniteDataStreamer<String, String> dataStreamer = ignite.dataStreamer("cache");

        System.out.println("start insert ...");
        long startTime = System.currentTimeMillis();
        for(int i=0;i<NUM;i++)
        {
            dataStreamer.addData(String.valueOf(i),"ignite_value");
            if(i%10000 == 0) System.out.println("save -> " + i);
        }
        dataStreamer.flush();
        System.out.println("ignite batch spend =======> " + (System.currentTimeMillis() - startTime));
        cache.removeAll();
        dataStreamer.close();
        cache.close();
    }

    @Test
    public void queryRedis(){
        NUM = 1_00_000;
        Jedis jedis = new Jedis(IP);
        Pipeline pipelined = jedis.pipelined();
        insertBatchRedis(pipelined);

        jedis.close();
        jedis = new Jedis(IP);

        int queryInt = 0;
        // 23448
        System.out.println("start query ...");
        long queryStart = System.currentTimeMillis();
        for(int i=0;i<NUM;i++)
        {
            String value = jedis.get(String.valueOf(i));
            if(i%10000 == 0) System.out.println("redis query -> " + i);
            if(value != null) queryInt++;
        }
        System.out.println("spend ======> " + (System.currentTimeMillis() - queryStart));
        System.out.println("find size ====> " + queryInt);
        jedis.close();
    }

    @Test
    public void queryIgnite(){
        NUM = 1_00_000;
        Ignite ignite = getIgniteCli(false);
        IgniteCache<String, String> cache = ignite.getOrCreateCache("cache");
        IgniteDataStreamer<String, String> dataStreamer = ignite.dataStreamer("cache");
        insertBatchIgnite(dataStreamer);

        int queryInt = 0;
        // server:23010     client: 44270
        System.out.println("start query ...");
        long queryStart = System.currentTimeMillis();
        for(int i=0;i<NUM;i++)
        {
            String s = cache.get(String.valueOf(i));
            if(i%10000 == 0) System.out.println("ignite query -> " + i);
            if(s != null) queryInt++;
        }
        System.out.println("spend ======> " + (System.currentTimeMillis() - queryStart));
        System.out.println("find size ====> " + queryInt);
        cache.removeAll();
        dataStreamer.close();
        ignite.close();
    }


    private void insertBatchRedis(Pipeline pipelined){
        for(int i=0;i<NUM;i++)
        {
            pipelined.set(String.valueOf(i),"redis_value");
        }
        pipelined.sync();
    }
    private void insertBatchIgnite(IgniteDataStreamer<String, String> dataStreamer){
        for(int i=0;i<NUM;i++)
        {
            dataStreamer.addData(String.valueOf(i),"redis_value");
        }
        dataStreamer.flush();
    }

    private Ignite getIgniteCli(boolean isClient){
        TcpDiscoverySpi spi = new TcpDiscoverySpi();
        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
        ipFinder.setAddresses(Arrays.asList(IP));
        spi.setIpFinder(ipFinder);
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setDiscoverySpi(spi);
        cfg.setClientMode(isClient);
        return Ignition.start(cfg);
    }

//    @Test
//    public void clearIgnite(){
//        TcpDiscoverySpi spi = new TcpDiscoverySpi();
//        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
//        ipFinder.setAddresses(Arrays.asList(IP));
//        spi.setIpFinder(ipFinder);
//
//        IgniteConfiguration cfg = new IgniteConfiguration();
//        cfg.setDiscoverySpi(spi);
//        Ignite ignite = Ignition.start(cfg);
//        IgniteCache<String, String> cache = ignite.cache("cache");
//        cache.removeAll();
//        System.out.println("delete all done.");
//        ignite.close();
//    }

}