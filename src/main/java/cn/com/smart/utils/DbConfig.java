package cn.com.smart.utils;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;

import com.alibaba.druid.pool.DruidDataSource;


public class DbConfig {
	private static DruidDataSource dataSource = new DruidDataSource();
	private static DruidDataSource dataSource1 = new DruidDataSource();
	
	public static ThreadLocal<Connection> threadLocal = new ThreadLocal<Connection>();
	
	static{
		InputStream in = DbConfig.class.getResourceAsStream("/jdbc.properties");
		Properties properties = new Properties();
		try {
			properties.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		dataSource.setUrl(properties.getProperty("jdbc.url"));
        dataSource.setUsername(properties.getProperty("jdbc.username"));//用户名
        dataSource.setPassword(properties.getProperty("jdbc.password"));//密码
//		dataSource.setUrl("jdbc:mysql://172.16.2.84:3306/cmdb");
//		dataSource.setUsername("cmdb");//用户名
//		dataSource.setPassword("cmdb");//密码
        dataSource.setInitialSize(2);
        dataSource.setMaxActive(20);
        dataSource.setMinIdle(0);
        dataSource.setMaxWait(60000);
        //dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestOnBorrow(false);
        dataSource.setTestWhileIdle(true);
        dataSource.setPoolPreparedStatements(false);
        
//        dataSource1.setUrl(properties.getProperty("jdbc.url1"));
//        dataSource1.setUsername(properties.getProperty("jdbc.username1"));//用户名
//        dataSource1.setPassword(properties.getProperty("jdbc.password1"));//密码
//        dataSource1.setInitialSize(2);
//        dataSource1.setMaxActive(20);
//        dataSource1.setMinIdle(0);
//        dataSource1.setMaxWait(60000);
//        //dataSource.setValidationQuery("SELECT 1");
//        dataSource1.setTestOnBorrow(false);
//        dataSource1.setTestWhileIdle(true);
//        dataSource1.setPoolPreparedStatements(false);
     
	}
	
	public static Connection getConnection(){
		Connection connection = null;
//		DruidConfig datasource = new DruidConfig();
		try{
			connection = dataSource.getConnection();
		}catch(SQLException e){
			e.printStackTrace();
		}
		return connection;
	}
	
	public static Connection getConnection1(){
		Connection connection = null;
		try{
			connection = dataSource1.getConnection();
		}catch(SQLException e){
			e.printStackTrace();
		}
		return connection;
	}
	
	public static void close(Connection con){
		try {
			if(con != null){
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void rollback(Connection con){
		try {
			con.rollback();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	public static void commit(Connection con){
		try {
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
