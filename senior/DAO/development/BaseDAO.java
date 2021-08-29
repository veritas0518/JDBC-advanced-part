package senior.DAO.development;

import primary.util.JDBCUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: BaseDAO
 * @Description: 封装了针对于数据表的通用的操作
 * @Author: TianXing.Xue
 * @Date: 2021/8/23 14:25
 *
 *   DAO：data(base) access object --- 数据库访问对象
 **/

public abstract class BaseDAO<T> {

    private Class<T> clazz = null;

    {
        //获取当前BaseDAO的子类继承的父类中的泛型
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;

        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();//获取了父类的泛型参数
        clazz = (Class<T>) actualTypeArguments[0];//泛型的第一个参数
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

    //通用的查询操作，用于返回数据表中的一条操作（version 2.0，考虑上事务）
    public T getInstance(Connection connection, String sql, Object... args) {

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
//                  String columnName = rsmd.getColumnName(i + 1);
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

    public List<T> getForList(Connection connection, String sql, Object... args) {
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
            JDBCUtils.closeResource(connection, ps, rs);
        }
        return null;
    }

    //用于查询特殊值的通用的方法
    public <E> E getValue(Connection connection, String sql, Object... args) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);

            }
            rs = ps.executeQuery();

            if (rs.next()) {
                return (E) rs.getObject(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtils.closeResource(null, ps, rs);
        }
        return null;
    }
}
