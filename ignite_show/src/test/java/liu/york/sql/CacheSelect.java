package liu.york.sql;

import com.alibaba.fastjson.JSON;
import liu.york.BaseConfigUtil;
import liu.york.sql.model.Student;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.FieldsQueryCursor;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.junit.Test;

import javax.cache.Cache;
import java.util.Iterator;
import java.util.List;

public class CacheSelect {
    private static final String IP = "192.168.159.135";
    @Test
    public void cacheSelect(){
        TcpDiscoverySpi tcpDiscoverySpi = BaseConfigUtil.getTcpDiscoverySpi(IP);
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setClientMode(true);
        cfg.setDiscoverySpi(tcpDiscoverySpi);
        Ignite ignite = Ignition.start(cfg);

        /*
         * 1 新建的表默认都会生成一个与之对应的cache，前缀 SQL_PUBLIC_ + 表名(字母大写)
         * 2 访问不能像普通缓存，而是需要 sql-query 查询
         * 3 普通缓存是不能跨缓存访问，但是 sql 是可以跨缓存查询
         * 4 返回格式数组 [] ，和 select * 对应列名对应
         */
        IgniteCache<Integer, Student> studentCache =
                ignite.cache("SQL_PUBLIC_STUDENT");
//        IgniteCache<Integer, Teacher> teacherCache = ignite.cache("SQL_PUBLIC_TEACHER");

        SqlFieldsQuery sqlFieldsQuery = new SqlFieldsQuery(
                "select * from student s, teacher t where t.teacherId = s.teacherId limit 10");
        FieldsQueryCursor<List<?>> query = studentCache.query(sqlFieldsQuery);
        Iterator<List<?>> iterator = query.iterator();
        while (iterator.hasNext()){
            List<?> next = iterator.next();
            System.out.println(JSON.toJSONString(next,true));
        }
        ignite.close();
    }

    @Test
    public void beanSelect(){
        TcpDiscoverySpi tcpDiscoverySpi = BaseConfigUtil.getTcpDiscoverySpi(IP);
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setClientMode(true);
        cfg.setDiscoverySpi(tcpDiscoverySpi);
        Ignite ignite = Ignition.start(cfg);

        IgniteCache<Integer, Student> studentCache = ignite.cache("student");
        SqlQuery<Integer, Student> query = new SqlQuery<>(Student.class, " studentId = ? ");
        QueryCursor<Cache.Entry<Integer, Student>> result = studentCache.query(query.setAlias("100"));

        System.out.println(result.getAll().get(0).getValue().toString());

    }
}