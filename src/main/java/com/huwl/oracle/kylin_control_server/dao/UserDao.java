package com.huwl.oracle.kylin_control_server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Test;

import com.huwl.oracle.kylinremotecontrol.beans.User;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class UserDao {
	private static DataSource dataSource = null;
    public static void releaseConnection(Connection connection){
           try {
                  if(connection != null ) {
                         connection.close();
                  }
           }catch (Exception e) {
                  e.printStackTrace();
           }
    }
    
    static{
           //dataSource资源只能初始化一次
           dataSource= new ComboPooledDataSource("kylinC3p0");
    }
    /**
     * 获取连接
     * @return
     * @throws SQLException
     */
    public static Connection getConnection() {
    	Connection conn=null;
       try {
    	   conn=dataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
       return conn;
    }
    
    public User register(User user){
    	Connection conn=getConnection();
    	PreparedStatement ps=null;
    	boolean hasSame=false;
    	try {
			ps=conn.prepareStatement("select count(*)>0 from USERS WHERE USER_ID=?");
			ps.setString(1, user.getUserId());
			ResultSet rs=ps.executeQuery();
			rs.next();
			hasSame=rs.getBoolean(1);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
    	if(hasSame)return null;
    	int flag=0;
    	try {
    		ps=conn.prepareStatement("insert into USERS VALUES(?,?)");
    		ps.setString(1,user.getUserId());
    		ps.setString(2,user.getPassword());
    		flag=ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return flag==1?user:null;
    }
    
    
    @Test
    public void test(){
    	try {
			System.out.println(getConnection().getAutoCommit());
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

	public boolean login(User user) {
		Connection conn=getConnection();
    	PreparedStatement ps=null;
    	ResultSet rs=null;
    	try {
			ps=conn.prepareStatement("select count(*)>0 from USERS where USER_ID=? and PASSWORD=?");
			ps.setString(1, user.getUserId());
			ps.setString(2, user.getPassword());
			rs=ps.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	boolean result=false;
		try {
			rs.next();
			result = rs.getBoolean(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return result;
	}

}
