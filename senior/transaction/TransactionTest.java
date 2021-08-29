package senior.transaction;

import org.junit.Test;
import primary.util.JDBCUtils;
import senior.transaction.User;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: TransactionTest
 * @Description:
 * @Author: TianXing.Xue
 * @Date: 2021/8/23 11:03
 *
 *  1.什么叫数据库事务
 *  事务：一组逻辑单元，使数据从一种状态变换到另一种状态
 *        >一组逻辑单元：一个或多个DML操作
 *
 *  2.事务处理的原则：（1）保证事务都作为一个工作单元来执行，即使出现了故障，都不能改变这种执行方式。
 *                 （2）当一个事务中执行多个操作时，要么所有的事务都执行，那么这些修改就永久保存下来；
 *                     要么都不执行，整个事务回滚到最初状态
 *
 *  3.数据一旦提交，就不可以回滚
 *
 *  4.哪些操作会导致数据的自动提交？
 *      >DDL操作一旦执行，就会自动提交
 *          >set auto_commit = false 对DDL操作失效
 *      >DML默认情况下，一旦执行，就会自动提交
 *          >set auto_commit = false的方式取消DML操作的自动提交
 *      >默认在关闭连接时，也就是按X退出后，会自动提交数据
 *
 **/

public class TransactionTest {

    //*****************未考虑数据库事务情况下的转账操作*********************
    /*
     针对于数据表user_table来说：
     AA用户给BB用户转账100

     update user_table set balance = balance - 100 where user = 'AA'
     update user_table set balance = balance + 100 where user = 'BB'

     */

    @Test
    public void testUpdate() {
        String sql1 = "update user_table set balance = balance - 100 where user = ?";
        update(sql1, "AA");

        //模拟网络异常
        System.out.println(10 / 0);

        String sql2 = "update user_table set balance = balance + 100 where user = ?";
        update(sql2, "BB");

        System.out.println("转账成功");
    }

    //通用的增删改操作 --- version1.0
    public int update(String sql, Object... args) { //sql中占位符的个数与可变形参的长度相同
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            //1.获取数据库的连接
            connection = JDBCUtils.getConnection();
            //2.预编译sql语句，返回PreparedStatement的实例
            ps = connection.prepareStatement(sql);
            //3.填充占位符
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]); //小心参数声明错误
            }
            //4.执行
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            //5.关闭
            JDBCUtils.closeResource(connection, ps);
        }
        return 0;
    }

    //*****************考虑数据库事务情况下的转账操作*********************

    @Test
    public void testUpdateWithTx() {
        Connection connection = null;
        try {
            connection = JDBCUtils.getConnection();
            System.out.println(connection.getAutoCommit()); //测试默认情况下数据库是否是自动提交

            //1.取消数据的自动提交功能
            connection.setAutoCommit(false);

            String sql1 = "update user_table set balance = balance - 100 where user = ?";
            update(connection, sql1, "AA");

            //模拟网络异常
            System.out.println(10 / 0);

            String sql2 = "update user_table set balance = balance + 100 where user = ?";
            update(connection, sql2, "BB");

            System.out.println("转账成功");

            //2.提交数据
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();

            //3.回滚数据
            try {
                connection.rollback();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } finally {

            //修改为自动提交数据，为了别人拿到的时候依然保持 默认的状态
            //主要针对于使用数据库连接池的使用
            try {
                connection.setAutoCommit(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            JDBCUtils.closeResource(connection, null);
        }
    }

    //通用的增删改操作 --- version2.0(考虑上事务)
    public int update(Connection connection, String sql, Object... args) { //sql中占位符的个数与可变形参的长度相同
        PreparedStatement ps = null;
        try {
            //1.预编译sql语句，返回PreparedStatement的实例
            ps = connection.prepareStatement(sql);
            //2.填充占位符
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]); //小心参数声明错误
            }
            //3.执行
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            //4.关闭
            JDBCUtils.closeResource(null, ps);
        }
        return 0;
    }

    @Test
    public void testTransactionSelect() throws Exception {
        Connection connection = JDBCUtils.getConnection();

        //设置数据库的隔离级别
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

        //取消自动提交数据
        connection.setAutoCommit(false);

        String sql = "select user,password,balance from user_table where user = ?";
        User user = getInstance(connection, User.class, sql, "CC");

        System.out.println(user);
    }

    @Test
    public void testTransactionUpdate() throws Exception {
        Connection connection = JDBCUtils.getConnection();
        //获取当前的隔离级别
        System.out.println(connection.getTransactionIsolation());

        //取消自动提交数据
        connection.setAutoCommit(false);

        String sql = "update user_table set balance = ? where user = ?";
        update(connection, sql, 6000, "CC");
        Thread.sleep(15000);
        System.out.println("修改结束");
    }

    //通用的查询操作，用于返回数据表中的一条操作（version 2.0，考虑上事务）
    public <T> T getInstance(Connection connection, Class<T> clazz, String sql, Object... args) {

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(sql);

            //填充占位符
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            rs = ps.executeQuery();
            //获取结果集的元数据ResultSetMetaData
            ResultSetMetaData rsmd = rs.getMetaData();
            //通过结果集的元数据(ResultSetMetaData)来获取结果集当中的列数
            int columnCount = rsmd.getColumnCount();
            if (rs.next()) {
                T t = clazz.getDeclaredConstructor().newInstance();
                //处理结果集一行数据中的每一个列
                for (int i = 0; i < columnCount; i++) {
                    //sql里是从1开始，获取列支
                    Object columnValue = rs.getObject(i + 1);

                    //获取每个列的列名
//                    String columnName = rsmd.getColumnName(i + 1);
                    String columnLabel = rsmd.getColumnLabel(i + 1);

                    //给cust对象指定的columnName属性，赋值为columnValue,通过反射
                    //这里注意是clazz，即当前的T类
                    Field field = clazz.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(t, columnValue);
                }
                return t;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(null, ps, rs);
        }
        return null;
    }

    //通用的查询操作，用于返回数据表中的多条记录构成的集合（version 2.0，考虑上事务）
    public <T> List<T> getForList(Connection connection,Class<T> clazz, String sql, Object... args) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(sql);

            //填充占位符
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            rs = ps.executeQuery();
            //获取结果集的元数据ResultSetMetaData
            ResultSetMetaData rsmd = rs.getMetaData();
            //通过结果集的元数据(ResultSetMetaData)来获取结果集当中的列数
            int columnCount = rsmd.getColumnCount();
            //创建集合对象
            ArrayList<T> list = new ArrayList<>();

            while (rs.next()) {
                T t = clazz.getDeclaredConstructor().newInstance();
                //处理结果集一行数据中的每一个列：给t对象指定的属性赋值
                for (int i = 0; i < columnCount; i++) {
                    //sql里是从1开始，获取列支
                    Object columnValue = rs.getObject(i + 1);

                    //获取每个列的列名
//                    String columnName = rsmd.getColumnName(i + 1);
                    String columnLabel = rsmd.getColumnLabel(i + 1);

                    //给cust对象指定的columnName属性，赋值为columnValue,通过反射
                    //这里注意是clazz，即当前的T类
                    Field field = clazz.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(t, columnValue);
                }
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(null, ps, rs);
        }
        return null;
    }


}
