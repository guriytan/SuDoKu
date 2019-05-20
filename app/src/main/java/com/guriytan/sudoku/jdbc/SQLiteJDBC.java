package com.guriytan.sudoku.jdbc;

import java.sql.*;
import java.util.List;

public class SQLiteJDBC {
    private static Connection connection = null;
    private static Statement statement = null;

    public static void setConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:C:/Users/GU/sudoku.db");
            statement = connection.createStatement();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("连接数据库成功");
    }

    /**
     * @describe: 创建表
     * @params: tableName: 要创建的表的名称 className：项目中Pojo的类名(需要注意的是该类名需要加上包名 如 com.xxx.xxx.pojo.xxx)
     */
    public synchronized static void create(String tableName) {
        String sb = "CREATE TABLE " + tableName + " " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "DATA            CHAR(100));";
        try {
            statement.executeUpdate("DROP TABLE IF EXISTS " + tableName + ";");
            statement.executeUpdate(sb);
            System.out.println("建表成功");
        } catch (Exception e) {
            throw new RuntimeException("建表失败，表名称：" + tableName);
        }
    }

    /**
     * @describe: 表中插入数据
     * @params: tableName：表名 list:待插入的对象集合 需要注意的是插入的对象要跟表名对应
     */
    public synchronized static void insert(String tableName, int[][] data) {
        StringBuilder dataSql = new StringBuilder();
        dataSql.append("'");
        for (int[] row : data) {
            for (int col : row) {
                dataSql.append(col);
            }
            dataSql.append(";");
        }
        dataSql.append("'");
        String retSQL = "INSERT INTO " + tableName + " (ID, DATA)"
                + " VALUES (NULL, " + dataSql.toString() + ");";
        System.out.println(retSQL);
        try {
            statement.executeUpdate(retSQL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @describe: 关闭链接
     */
    public static void endConnection() {
        try {
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}