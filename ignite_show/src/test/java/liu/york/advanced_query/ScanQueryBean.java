package liu.york.advanced_query;

import liu.york.BaseConfigUtil;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.junit.Test;

import javax.cache.Cache;
import java.util.List;

public class ScanQueryBean {
    @Test
    public void scanQuery(){
        IgniteCache<Integer, UserBean> cache = getCache();

        /*
         * 如果提前预知返回集非常大，可以设置分页大小，query.setPageSize(100),默认为 1024
         * 如果使用 QueryCursor.Iterator 迭代器，那么当迭代最后一个时，会自动请求下一页，但是必须要主动关闭 result.close()
         */

        ScanQuery<Integer, UserBean> query = new ScanQuery<>((k, v) -> v.getUserId() > 9000 && v.getUserId() < 9010);
        QueryCursor<Cache.Entry<Integer, UserBean>> result = cache.query(query);
        List<Cache.Entry<Integer, UserBean>> all = result.getAll();
        all.forEach(entry -> System.out.println(entry.getKey() + " ---> " + entry.getValue()));
        result.close();

    }

    @Test
    public void scanField(){
        IgniteCache<Integer, UserBean> cache = getCache();

        ScanQuery<Integer, UserBean> query = new ScanQuery<>((k, v) -> v.getUserId() < 15);
        /*
         * 只获取 userBean 的 userName 属性
         */
        QueryCursor<String> result = cache.query(query, (e) -> e.getValue().getUserName());

        result.forEach(name -> System.out.println(name));
    }


    public static IgniteCache<Integer, UserBean> getCache(){
        TcpDiscoverySpi tcpDiscoverySpi = BaseConfigUtil.getTcpDiscoverySpi("127.0.0.1");
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setLocalHost("127.0.0.1");
        cfg.setDiscoverySpi(tcpDiscoverySpi);
        Ignite ignite = Ignition.start(cfg);

        IgniteCache<Integer, UserBean> cache = ignite.getOrCreateCache("cache");

        StringBuilder sb = new StringBuilder();
        for (int i = 0;i < 10000;i++){
            sb.setLength(0);
            cache.put(i,new UserBean(i,sb.append("userName-").append(i).toString(),"上海浦东张江"));
        }

        return cache;
    }
}