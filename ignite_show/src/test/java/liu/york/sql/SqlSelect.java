package liu.york.sql;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;

public class SqlSelect {
    private static final String IP = "192.168.159.135";
    final String selectStu = "select * from student limit 1,20"; // where studentId = '93'
    final String selectTea = "select * from teacher "; // where teacherId = '5'
    final String selectStuTea = "select t.teacherId,t.userName,s.studentId,s.userName " +
            "from teacher t, student s where t.teacherId = s.teacherId"; // and t.teacherId = '9'

    private ResultSet rs;
    private Statement stat;
    private Connection conn;

    @Test
    public void selectStu() throws Exception {
        rs = stat.executeQuery(selectStu);
        while (rs.next())
            System.out.println(
                    rs.getString(1) + "  " +
                    rs.getString(2) + "   " +
                    rs.getString(3) + "   " +
                    rs.getString(4));
//        while (rs.next())
//            System.out.println(rs.getString(1));
    }

    @Test
    public void selectTea() throws Exception {
        rs = stat.executeQuery(selectTea);
        while (rs.next())
            System.out.println(
                    rs.getString(1) + "  " +
                    rs.getString(2) + "   " +
                    rs.getString(3));
    }

    @Test
    public void selectStuTea() throws Exception {
        rs = stat.executeQuery(selectStuTea);
        while (rs.next())System.out.println(
                rs.getString(1) + "  " +
                rs.getString(2) + "   " +
                rs.getString(3) + "   " +
                rs.getString(4));
    }

    @Before
    public void init() throws Exception {
        Class.forName("org.apache.ignite.IgniteJdbcThinDriver");
        conn = DriverManager.getConnection("jdbc:ignite:thin://"+IP+"/");
        stat = conn.createStatement();
    }

    @After
    public void destroy() throws SQLException {
        if(rs != null && !rs.isClosed()) rs.close();
        if(stat != null && !stat.isClosed()) stat.close();
        if(conn != null && !conn.isClosed()) conn.close();
    }
}