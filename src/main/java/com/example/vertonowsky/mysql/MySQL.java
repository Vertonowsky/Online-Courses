package com.example.vertonowsky.mysql;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
@PropertySource("classpath:application.properties")
public class MySQL {

    private Connection conn;

    @Value("${spring.datasource.username}")
    private String user;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${database.name}")
    private String database;

    private final String prefix = "[MySQL Configuration]";



    @PostConstruct
    public void verifyDatabase() {
        try {

            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/", user, password);
            con.createStatement().executeUpdate("CREATE DATABASE IF NOT EXISTS spring");
            con.close();
            System.out.printf("%s Verified database existance! %n", prefix);

            openConnection();
            closeConnection();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[ ============================================== ]");
            System.out.println("[ There was a problem with database connection!  ]");
            System.out.println("[ Make sure that provided credentials in         ]");
            System.out.println("[ appilcation.properties are valid and MySQL     ]");
            System.out.println("[ server is running properly!!                   ]");
            System.out.println("[ ============================================== ]");
        }
    }



    private synchronized void openConnection() {
        if (!isConnected()) {

            try {

                conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + database + "?useUnicode=yes&characterEncoding=UTF-8", user, password);
                System.out.printf("%s Successfully connected! %n", prefix);

            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public synchronized void closeConnection() {

        if (isConnected()) {

            try {
                conn.close();
                System.out.printf("%s Successfully disconnected! %n", prefix);

            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }


    private boolean isConnected() {
        if (conn == null) return false;

        try {
            if(conn.isClosed()) return false;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

}
