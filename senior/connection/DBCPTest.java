package senior.connection;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.junit.Test;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @ClassName: DBCPTest
 * @Description: 测试DBCP的数据库连接池技术
 * @Author: TianXing.Xue
 * @Date: 2021/8/24 11:11
 **/

public class DBCPTest {

    //方式一：不推荐
    @Test
    public void testGetConnection() throws SQLException {
        //创建了DBCP的数据库连接池
        BasicDataSource source = new BasicDataSource();

        //设置基本信息
        source.setDriverClassName("com.mysql.cj.jdbc.Driver");
        source.setUrl("jdbc:mysql:///test");
        source.setUsername("root");
        source.setPassword("111111");

        //还可以设置其他涉及数据库连接池管理的相关属性
        source.setInitialSize(10);
        source.setMaxTotal(10);

        Connection connection = source.getConnection();
        System.out.println(connection);

    }

    //方式二：使用配置文件
    @Test
    public void tetGetConnection1() throws Exception {
        Properties pros = new Properties();
        //方式1：类的加载器
//        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("dbcp.properties");

        //方式2：
        FileInputStream is = new FileInputStream("src/dbcp.properties");

        pros.load(is);
        BasicDataSource dataSource = BasicDataSourceFactory.createDataSource(pros);

        Connection connection = dataSource.getConnection();
        System.out.println(connection);
    }
}
