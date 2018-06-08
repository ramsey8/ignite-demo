package liu.york.cache_select;

import liu.york.BaseConfigUtil;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.cache.query.TextQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.lang.IgniteBiPredicate;
import org.apache.ignite.lang.IgniteClosure;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.junit.Test;

import javax.cache.Cache;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CacheSelect {
    private static final String CACHE_NAME = "cache_select_001";
    @Test
    public void server() throws InterruptedException {
        TcpDiscoverySpi tcpDiscoverySpi = BaseConfigUtil.getTcpDiscoverySpi("127.0.0.1");
        CacheConfiguration<String, User> cacheConfiguration = new CacheConfiguration<>(CACHE_NAME);
        cacheConfiguration.setIndexedTypes(String.class, User.class);
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setDiscoverySpi(tcpDiscoverySpi);
        cfg.setCacheConfiguration(cacheConfiguration);
        Ignite ignite = Ignition.start(cfg);

        IgniteCache<String, User> cache = ignite.getOrCreateCache(CACHE_NAME);
        for(int i=0;i<10;i++){
            cache.put("type1_key" + i,new User("type1_name",i));
            cache.put("type2_key" + i,new User("type2_name",i));
            cache.put("type3_key" + i,new User("type3_name",i));
        }
        TimeUnit.DAYS.sleep(1);
    }

    /**
     * ignite 支持对缓存进行查询，可以对key 和 value 实现动态过滤
     * @throws InterruptedException
     */
    @Test
    public void query() throws InterruptedException {
        TcpDiscoverySpi tcpDiscoverySpi = BaseConfigUtil.getTcpDiscoverySpi("127.0.0.1");
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setClientMode(true);
        cfg.setDiscoverySpi(tcpDiscoverySpi);
        Ignite ignite = Ignition.start(cfg);

        IgniteCache<String, User> cache = ignite.cache(CACHE_NAME);

        // 查询key包含 type1 的缓存
        try (QueryCursor cursor = cache.query(new ScanQuery<String,User>((k,v) -> k.contains("type1")))) {

            cursor.forEach(value -> System.out.println(value));
        }
        ignite.close();
    }

    /**
     * 减小返回结果的体，最小化网络数据传输量
     * @throws InterruptedException
     */
    @Test
    public void partitionQuery() throws InterruptedException {
        TcpDiscoverySpi tcpDiscoverySpi = BaseConfigUtil.getTcpDiscoverySpi("127.0.0.1");
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setClientMode(true);
        cfg.setDiscoverySpi(tcpDiscoverySpi);
        Ignite ignite = Ignition.start(cfg);

        IgniteCache<String, User> cache = ignite.cache(CACHE_NAME);
        QueryCursor<String> result = cache.query(new ScanQuery<String, User>((k, v) -> k.contains("type1")), o -> o.getValue().getName());
        result.forEach(v -> System.out.println(v));
        ignite.close();
    }

    /**
     * 文本字段查询，但是必须要设置文本索引
     */
    @Test
    public void luceneQuery(){
        TcpDiscoverySpi tcpDiscoverySpi = BaseConfigUtil.getTcpDiscoverySpi("127.0.0.1");
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setClientMode(true);
        cfg.setDiscoverySpi(tcpDiscoverySpi);
        Ignite ignite = Ignition.start(cfg);

        IgniteCache<String, User> cache = ignite.cache(CACHE_NAME);
        // Note: 这个地方必须要提前设置 User 的 TextField 索引
        TextQuery txt = new TextQuery(User.class, "type1_name");
        try (QueryCursor<Cache.Entry<Long, User>> masters = cache.query(txt)) {
            for (Cache.Entry<Long, User> e : masters)
                System.out.println("===========> " + e.getValue().getName());
        }
        ignite.close();
    }
}