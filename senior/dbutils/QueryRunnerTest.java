package senior.dbutils;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.*;
import org.junit.Test;
import senior.bean.Customer;
import senior.util.JDBCUtils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: QueryRunnerTest
 * @Description: commons-dbutils是Apache组织提供的一个开源的JDBC的工具类库。
 *               它是对JDBC的简单封装,封装了对数据库的增删改查操作。
 * @Author: TianXing.Xue
 * @Date: 2021/8/24 14:28
 **/

public class QueryRunnerTest {

    @Test
    public void testInsert() {
        Connection connection3 = null;
        try {
            QueryRunner runner = new QueryRunner();
            connection3 = JDBCUtils.getConnection3();
            String sql = "insert into customers(name,email,birth)values(?,?,?)";
            int insertCount = runner.update(connection3, sql, "Jackie", "jackie@qq.com", "1999-9-9");
            System.out.println("添加了" + insertCount + "条记录");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtils.closeResource(connection3, null);
        }
    }

    /*
     *  测试查询
     *
     *  BeanHandler：是ResultSetHandler接口的实现类，用于封装表中的一条记录。
     *
     * */
    @Test
    public void testQuery1() {
        Connection connection3 = null;
        try {
            QueryRunner runner = new QueryRunner();
            connection3 = JDBCUtils.getConnection3();
            String sql = "select id,name,email,birth from customers where id = ?";
            BeanHandler<Customer> handler = new BeanHandler<Customer>(Customer.class);
            Customer customer = runner.query(connection3, sql, handler, 23);
            System.out.println(customer);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            //不关闭连接会导致内存泄漏问题
            JDBCUtils.closeResource(connection3, null);
        }
    }

    /*
     *  BeanHandler：是ResultSetHandler接口的实现类，用于封装表中多条记录构成的集合。
     *
     * */
    @Test
    public void testQuery2() {
        Connection connection3 = null;
        try {
            QueryRunner runner = new QueryRunner();
            connection3 = JDBCUtils.getConnection3();
            String sql = "select id,name,email,birth from customers where id < ?";
            BeanListHandler<Customer> handler = new BeanListHandler<Customer>(Customer.class);
            List<Customer> list = runner.query(connection3, sql, handler, 23);
            list.forEach(System.out::println);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtils.closeResource(connection3, null);
        }
    }

    /*
     *  测试查询
     *
     *  MapHandler：是ResultSetHandler接口的实现类，用于封装表中的一条记录。
     *  将结果及相应字段的值作为map中的key和value
     * */
    @Test
    public void testQuery3() {
        Connection connection3 = null;
        try {
            QueryRunner runner = new QueryRunner();
            connection3 = JDBCUtils.getConnection3();
            String sql = "select id,name,email,birth from customers where id = ?";
            MapHandler handler = new MapHandler();
            Map<String, Object> map = runner.query(connection3, sql, handler, 22);
            System.out.println(map);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            //不关闭连接会导致内存泄漏问题
            JDBCUtils.closeResource(connection3, null);
        }
    }

    /*
     *  测试查询
     *
     *  MapListHandler：是ResultSetHandler接口的实现类，用于封装表中的多条记录。
     *  将结果及相应字段的值作为map中的key和value。将这些map添加到list中
     * */
    @Test
    public void testQuery4() {
        Connection connection3 = null;
        try {
            QueryRunner runner = new QueryRunner();
            connection3 = JDBCUtils.getConnection3();
            String sql = "select id,name,email,birth from customers where id < ?";
            MapListHandler handler = new MapListHandler();
            List<Map<String, Object>> list = runner.query(connection3, sql, handler, 21);
            list.forEach(System.out::println);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            //不关闭连接会导致内存泄漏问题
            JDBCUtils.closeResource(connection3, null);
        }
    }

    /*
     *  ScalarHandler：用于查询特殊值
     * */
    @Test
    public void testQuery5() {
        Connection connection3 = null;
        try {
            QueryRunner runner = new QueryRunner();
            connection3 = JDBCUtils.getConnection3();

            String sql = "select count(*) from customers";

            ScalarHandler handler = new ScalarHandler();
            Long count =(Long) runner.query(connection3, sql, handler);
            System.out.println(count);
        } catch (SQLException throwables) {

            throwables.printStackTrace();
        } finally {
            //不关闭连接会导致内存泄漏问题
            JDBCUtils.closeResource(connection3, null);
        }
    }

    @Test
    public void testQuery6() {
        Connection connection3 = null;
        try {
            QueryRunner runner = new QueryRunner();
            connection3 = JDBCUtils.getConnection3();

            String sql = "select MAX(birth) from customers";

            ScalarHandler handler = new ScalarHandler();
            Date maxBirth = (Date) runner.query(connection3, sql, handler);
            System.out.println(maxBirth);
        } catch (SQLException throwables) {

            throwables.printStackTrace();
        } finally {
            //不关闭连接会导致内存泄漏问题
            JDBCUtils.closeResource(connection3, null);
        }
    }

    /*
    *  自定义ResultSetHandler的实现类
    *
    * */
    @Test
    public void testQuery7() {
        Connection connection3 = null;
        try {
            QueryRunner runner = new QueryRunner();
            connection3 = JDBCUtils.getConnection3();

            String sql = "select id,name,email,birth from customers where id =?";

            ResultSetHandler<Customer> handler = new ResultSetHandler<>() {
                @Override
                public Customer handle(ResultSet rs) throws SQLException {
//                    System.out.println("这个方法被执行过");
//                    return null;

//                    return new Customer(12,"成龙","JackieChen@qq.com",new Date(5322443432L));

                    if (rs.next()){
                        int id = rs.getInt("id");
                        String name = rs.getString("name");
                        String email = rs.getString("email");
                        Date birth = rs.getDate("birth");

                        Customer customer = new Customer(id, name, email, birth);
                        return customer;
                    }
                    return null;

                }
            };
            Customer customer = runner.query(connection3, sql, handler, 23);
            System.out.println(customer);

        } catch (SQLException throwables) {

            throwables.printStackTrace();
        } finally {
            //不关闭连接会导致内存泄漏问题
            JDBCUtils.closeResource(connection3, null);
        }
    }
}
