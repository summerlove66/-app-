package com.hui.crawler;

import java.sql.*;

public class MysqlUtil {
	public  Connection getConnect() {
		String url1 = "jdbc:mysql://localhost:3306/test?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false";
		String user = "root";
		String password = "smile789";
		Connection conn = null;
		
        try {
            // connect way #1
            
            conn = DriverManager.getConnection(url1, user, password);
            if (conn != null) {
                System.out.println("Connected to the database test1");
              
            }
            
        } catch (SQLException ex) {
            System.out.println("An error occurred. Maybe user/password is invalid");
            ex.printStackTrace();
        }
		return conn;
    }
	 public void closeConnection( Statement statement, Connection con) {
	 try {
	                if (statement != null) {
	                    statement.close();
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	            } finally {
	                try {
	                    if (con != null) {
	                        con.close();
	                    }
	                } catch (SQLException e) {
	                    e.printStackTrace();
	                }
	            }
	        }

	
 }

