package liu.york.sql;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SqlInit {
    private static final String IP = "192.168.159.135";
    @Test
    public void init() throws Exception {
        Class.forName("org.apache.ignite.IgniteJdbcThinDriver");
        Connection conn = DriverManager.getConnection("jdbc:ignite:thin://"+IP+"/");

        try (Statement stmt = conn.createStatement()) {
            // 创建表
            createTable(stmt);
            // 新建数据
            insert(getStudent(),stmt);
            insert(getTeacher(),stmt);
        }
        conn.close();
    }

    private void createTable(Statement stmt) throws Exception {
        stmt.execute("CREATE TABLE student (" +
                "studentId INT PRIMARY KEY," +
                "userName VARCHAR," +
                "address VARCHAR(20)," +
                "teacherId VARCHAR(100)" +
                ")WITH \"template=replicated\""); // WITH template=replicated

        stmt.execute("CREATE TABLE teacher (" +
                "teacherId INT PRIMARY KEY ," +
                "userName VARCHAR(20)," +
                "address VARCHAR(100)" +
                ")WITH \"template=replicated\""); // backups=1, affinityKey=city_id
    }

    private List<String> getStudent(){
        List<String> sqlList = new ArrayList<>();
        String insertTemplate = "insert into student(studentId,userName,address,teacherId) values('%s','%s','%s','%s')";
        for(int i = 0; i < 100; i++)
        {
            sqlList.add(String.format(insertTemplate, i,"userName-" + i,"上海浦东新区",(i%10)));
        }
        return sqlList;
    }

    private List<String> getTeacher(){
        List<String> sqlList = new ArrayList<>();
        String insertTemplate = "insert into teacher(teacherId,userName,address) values('%s','%s','%s')";
        for(int i = 0; i < 10; i++)
        {
            sqlList.add(String.format(insertTemplate, i,"userName-" + i,"上海浦东旧区"));
        }
        return sqlList;
    }

    private void insert(List<String> sqlList,Statement stmt){
        sqlList.forEach(sql -> {
            try {
                stmt.execute(sql);
            } catch (SQLException e) {
                // TODO: 2018/5/25  cache exception
            }
        });
    }

}