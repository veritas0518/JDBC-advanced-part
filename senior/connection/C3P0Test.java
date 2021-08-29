package senior.connection;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @ClassName: C3P0Test
 * @Description:
 * @Author: TianXing.Xue
 * @Date: 2021/8/24 9:50
 **/

public class C3P0Test {

    //方式一：不推荐
    @Test
    public void testGetConnection() throws Exception {
        //获取C3P0数据库连接池
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        cpds.setDriverClass( "com.mysql.cj.jdbc.Driver" ); //loads the jdbc driver
        cpds.setJdbcUrl( "jdbc:mysql://localhost:3306/test" );
        cpds.setUser("root");
        cpds.setPassword("111111");

        //通过设置相关的参数，对数据库连接池进行管理
        //设置初始时数据库连接池中的连接数
        cpds.setInitialPoolSize(10);

        Connection connection = cpds.getConnection();
        System.out.println(connection);

        //销毁C3P0数据库连接池
//        DataSources.destroy(cpds);
    }

    //方式二：使用配置文件（推荐）
    @Test
    public void testGetConnectionFinal() throws SQLException {
        ComboPooledDataSource cpds = new ComboPooledDataSource("helloC3p0");
        Connection connection = cpds.getConnection();
        System.out.println(connection);
    }
}
