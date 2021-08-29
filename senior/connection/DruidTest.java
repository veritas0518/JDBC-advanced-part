package senior.connection;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.sql.Connection;
import java.util.Properties;


/**
 * @ClassName: DruidTest
 * @Description: 使用druid进行连接测试
 * @Author: TianXing.Xue
 * @Date: 2021/8/24 13:34
 **/

public class DruidTest {

    @Test
    public void getConnection() throws Exception {

        Properties properties = new Properties();
        FileInputStream is = new FileInputStream("src/druid.properties");

        properties.load(is);
        DataSource source = DruidDataSourceFactory.createDataSource(properties);
        Connection connection = source.getConnection();
        System.out.println(connection);


    }
}
