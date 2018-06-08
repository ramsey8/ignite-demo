package liu.york.sql;

import liu.york.BaseConfigUtil;
import liu.york.sql.model.Student;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.junit.Test;

import javax.cache.Cache;

public class BeanSelect {

    @Test
    public void select(){
        TcpDiscoverySpi tcpDiscoverySpi = BaseConfigUtil.getTcpDiscoverySpi("127.0.0.1");
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setLocalHost("localhost");
        cfg.setDiscoverySpi(tcpDiscoverySpi);
        Ignite ignite = Ignition.start(cfg);

        IgniteCache<Integer, Student> studentCache = ignite.getOrCreateCache("studentCache");

        for(int i=0;i<100;i++)
        {
            studentCache.put(i,new Student(i,"userName" + i,"上海浦东新区",i%10));
        }

        SqlQuery<Integer, Student> query = new SqlQuery<>(Student.class, "studentId = ?");

        QueryCursor<Cache.Entry<Integer, Student>> result = studentCache.query(query.setArgs(99));
        System.out.println(result.getAll().get(0).getValue());


    }
}