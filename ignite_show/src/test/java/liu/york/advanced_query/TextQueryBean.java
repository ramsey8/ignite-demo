package liu.york.advanced_query;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.TextQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.junit.Test;

import javax.cache.Cache;
import java.util.List;

public class TextQueryBean {
    @Test
    public void textQuery(){

        CacheConfiguration<Integer, UserBean> cacheCfg = new CacheConfiguration<>("cache");
        cacheCfg.setIndexedTypes(Integer.class,UserBean.class);

        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setCacheConfiguration(cacheCfg);
        cfg.setLocalHost("127.0.0.1");
        Ignite ignite = Ignition.start(cfg);
        IgniteCache<Integer, UserBean> cache = ignite.cache("cache");

        StringBuilder sb = new StringBuilder();
        for (int i = 0;i < 10000;i++){
            sb.setLength(0);
            cache.put(i,new UserBean(i,sb.append("userName-").append(i).toString(),"上海浦东张江"));
        }

        TextQuery<Integer, UserBean> query = new TextQuery<>(UserBean.class, "userName-5555");
        QueryCursor<Cache.Entry<Integer, UserBean>> result = cache.query(query);
        List<Cache.Entry<Integer, UserBean>> list = result.getAll();


        System.out.println("query size ===> " + list.size());
        System.out.println("key:" + list.get(0).getKey());
        System.out.println("value:" + list.get(0).getValue());
    }
}