package senior.util;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.apache.commons.dbutils.DbUtils;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @ClassName: JDBCUtils
 * @Description: 使用c3p0的数据库连接池技术
 * @Author: TianXing.Xue
 * @Date: 2021/8/24 10:44
 **/

public class JDBCUtils {

    /*方法描述
     * @author: TianXing.Xue
     * @Description: 获取数据库的连接
     * @param:
     * @return:
     * @date: 2021/8/16 11:10
     */
    public static Connection getConnection() throws Exception {
        //1.读取配置文件中的四个基本信息
        //第一行是为了获得一个类的加载器
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("jdbc.properties");
        Properties properties = new Properties();
        properties.load(is);

        String user = properties.getProperty("user");
        String password = properties.getProperty("password");
        String url = properties.getProperty("url");
        String driverClass = properties.getProperty("driverClass");

        //2.加载驱动
        Class.forName(driverClass);

        //3.获取连接
        Connection connection = DriverManager.getConnection(url, user, password);
        return connection;
    }

    //数据库连接池只需提供一个即可。
    private static ComboPooledDataSource cpds = new ComboPooledDataSource("helloC3p0");

    public static Connection getConnection1() throws SQLException {

        Connection connection = cpds.getConnection();

        return connection;
    }

    /*方法描述
     * @author: TianXing.Xue
     * @Description: 使用DBCP数据库连接池技术获取数据库连接
     * @param:
     * @return:
     * @date: 2021/8/24 13:25
     */
    private static DataSource source;

    static {
        try {
            Properties pros = new Properties();

            FileInputStream is = new FileInputStream("src/dbcp.properties");
            pros.load(is);
            //创建一个DBCP数据库连接池
            source = BasicDataSourceFactory.createDataSource(pros);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection2() throws Exception {
        //拿着唯一的一个source去获取连接
        Connection connection = source.getConnection();
        return connection;
    }

    /*方法描述
     * @author: TianXing.Xue
     * @Description: 使用Druid数据库连接池技术(重要)
     * @param:
     * @return:
     * @date: 2021/8/24 14:09
     */
    private static DataSource source1;

    static {
        try {
            Properties properties = new Properties();
            FileInputStream is = new FileInputStream("src/druid.properties");

            properties.load(is);
            source1 = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection3() throws SQLException {

        Connection connection = source1.getConnection();
        return connection;
    }

    /*方法描述
     * @author: TianXing.Xue
     * @Description: 关闭连接和Statement的操作
     * @param:
     * @return:
     * @date: 2021/8/16 11:13
     */
    public static void closeResource(Connection connection, Statement ps) {
        if (ps != null) {
            //7.资源关闭
            try {
                ps.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    /*方法描述
     * @author: TianXing.Xue
     * @Description:关闭资源的操作
     * @param:
     * @return:
     * @date: 2021/8/21 18:31
     */
    public static void closeResource(Connection connection, Statement ps, ResultSet rs) {
        if (ps != null) {
            //7.资源关闭
            try {
                ps.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    /*方法描述
    * @author: TianXing.Xue
    * @Description: 使用dbutils.jar包中提供的DbUtils工具类，实现资源的关闭
    * @param:
    * @return:
    * @date: 2021/8/24 17:05
    */
    public static void closeResource1(Connection connection, Statement ps, ResultSet rs) {
//        try {
//            DbUtils.close(connection);
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//        try {
//            DbUtils.close(ps);
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//        try {
//            DbUtils.close(rs);
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }

        //悄悄的关闭（lol）
        DbUtils.closeQuietly(connection, ps, rs);

    }
}
