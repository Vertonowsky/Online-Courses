package com.example.hello_world.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {

    public static Connection conn;


    public static synchronized void openConnection(){
        if (!isConnected()) {

            String user = "notatest";
            String password = "Karaoke321.";
            String database = "spring";

            try {

                conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + database + "?useUnicode=yes&characterEncoding=UTF-8", user, password);

                System.out.println("Polaczono!");

            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public static synchronized void closeConnection() {
        if (isConnected()) {
            try {

                conn.close();

            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public static boolean isConnected() {
        if (conn == null) return false;

        try {

            if(conn == null) return false;
            if(conn.isClosed()) return false;

        } catch(SQLException e) {
            e.printStackTrace();
        }
        return true;
    }


    public static void verifyDatabase() {
        try {

            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "notatest", "Karaoke321.");
            con.createStatement().executeUpdate("CREATE DATABASE IF NOT EXISTS spring");
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
