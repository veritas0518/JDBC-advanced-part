package senior.DAO.development.junit;

import org.junit.jupiter.api.Test;
import primary.bean.Customer;
import senior.util.JDBCUtils;
import senior.DAO.development.CustomerDAOImpl;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

class CustomerDAOImplTest {
    private CustomerDAOImpl dao = new CustomerDAOImpl();

    @Test
    void testInsert() {
        Connection connection = null;
        try {
            connection = JDBCUtils.getConnection();
            Customer customer = new Customer(1, "小飞", "xiaofei@qq.com", new Date(3213131312132L));
            dao.insert(connection, customer);
            System.out.println("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(connection, null);
        }
    }

    @Test
    void testDeleteByID() {
        Connection connection = null;
        try {
            connection = JDBCUtils.getConnection();
            dao.deleteByID(connection, 13);
            System.out.println("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(connection, null);
        }
    }

    @Test
    void testUpdate() {
        Connection connection = null;
        try {
            connection = JDBCUtils.getConnection();
            Customer customer = new Customer(18, "贝多芬", "beiduofeng@qq.com", new Date(3232242433L));
            dao.update(connection, customer);
            System.out.println("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(connection, null);
        }
    }

    @Test
    void testGetCustomerByID() {
        Connection connection = null;
        try {
            connection = JDBCUtils.getConnection3();
            Customer customerByID = dao.getCustomerByID(connection, 19);
            System.out.println(customerByID);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(connection, null);
        }
    }

    @Test
    void testGetAll() {
        Connection connection = null;
        try {
            connection = JDBCUtils.getConnection();
            List<Customer> list = dao.getAll(connection);
            list.forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(connection, null);
        }
    }

    @Test
    void testGetCount() {
        Connection connection = null;
        try {
            connection = JDBCUtils.getConnection();
            Long count = dao.getCount(connection);
            System.out.println("表中的记录数为：" + count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(connection, null);
        }
    }

    @Test
    void testGetMaxBirth() {
        Connection connection = null;
        try {
            connection = JDBCUtils.getConnection();
            java.util.Date maxBirth = dao.getMaxBirth(connection);
            System.out.println("最大的生日为" + maxBirth);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(connection, null);
        }
    }
}