package com.example.hello_world.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySQL {

    public static Connection conn;

    private static String user = "notatest";
    private static String password = "Karaoke321.";
    private static String database = "spring";



    public static synchronized void openConnection(){
        if (!isConnected()) {

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

                System.out.println("Rozlaczono!");

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

            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/", user, password);
            con.createStatement().executeUpdate("CREATE DATABASE IF NOT EXISTS spring");
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static List<String> getSingleFilter(String query) {
        openConnection();
        List<String> collection = new ArrayList<>();

        try {

            ResultSet result = conn.createStatement().executeQuery(query);

            while (result.next())
                collection.add(result.getString(1));


        } catch (SQLException e) {
            e.printStackTrace();
        }

        closeConnection();
        return collection;
    }

}
