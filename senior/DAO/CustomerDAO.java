package senior.DAO;

import primary.bean.Customer;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

/*
 *  此接口用来规范对于customer表的常用操作
 *
 * */
public interface CustomerDAO {

    //将customer对象添加到数据库中
    void insert(Connection connection, Customer customer);

    //针对指定的id，删除表中的一条记录
    void deleteByID(Connection connection, int id);

    //针对于内存中的customer对象，去修改数据表中的指定的记录
    void update(Connection connection, Customer customer);

    //根据指定的id查询对应的Customer对象
    Customer getCustomerByID(Connection connection, int id);

    //查询表中的所有记录构成的集合
    List<Customer> getAll(Connection connection);

    //返回数据表中数据的条目数
    Long getCount(Connection connection);

    //返回数据表中最大的生日
    Date getMaxBirth(Connection connection);
}
