package com.supermarket;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCUtils {

    static ComboPooledDataSource pool = new ComboPooledDataSource();

    /**
     * 工具类，私有化构造函数
     */
    private JDBCUtils() {
    }

    /**
     * 关闭一个对象
     * @param obj 要关闭的对象，要求该对象具有close()方法
     * */
    private static <T extends AutoCloseable> void close(T obj) {
        if (obj != null) {
            try {
                obj.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                obj = null;
            }
        }
    }

    /**
     * 按照顺序关闭一群对象
     * @param obj 要关闭的一组对象，要求对象具有close()方法
     * */
    @SafeVarargs
    public static <T extends AutoCloseable> void close(T... obj) {
        for (T t : obj) {
            JDBCUtils.close(t);
        }
    }

    /**
     * 查询个数，执行语句：select count(1) as count from table where filed = value
     * @param table 表名
     * @param field 列名
     * @param value 查询数值
     * @return 查询个数
     */
    public static int count(String table, String field, Object value) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int result =  0;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement(String.format("select count(1) as count from %s where %s = ?", table, field));
            if (value.getClass() == String.class)
                ps.setString(1, (String) value);
            else if (value.getClass() == Integer.class)
                ps.setInt(1, (Integer) value);
            else
                ps.setObject(1, value);
            rs = ps.executeQuery();
            if (rs.next())
                result = rs.getInt("count");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            JDBCUtils.close(rs, ps, conn);
        }
        return result;
    }

    /**
     * 向表格user中插入数据，执行语句：insert into user values(null, username, password, nickname, email)
     * @param username 用户名
     * @param password 密码
     * @param nickname 昵称
     * @param email 邮箱
     * */
    public static void insertUser(String username, String password, String nickname, String email){
        Connection conn = null;
        PreparedStatement ps = null;
        try{
            conn = pool.getConnection();
            ps = conn.prepareStatement("insert into user values(null, ?, ?, ?, ?)");
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, nickname);
            ps.setString(4, email);
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }finally{
            JDBCUtils.close(ps, conn);
        }
    }
}
