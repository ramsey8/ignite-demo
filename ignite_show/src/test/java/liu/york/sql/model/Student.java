package liu.york.sql.model;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

public class Student {
    @QuerySqlField
    private int studentId;
    @QuerySqlField
    private String userName;
    @QuerySqlField
    private String address;
    @QuerySqlField
    private int teacherId;

    public Student(int studentId, String userName, String address, int teacherId) {
        this.studentId = studentId;
        this.userName = userName;
        this.address = address;
        this.teacherId = teacherId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }
}