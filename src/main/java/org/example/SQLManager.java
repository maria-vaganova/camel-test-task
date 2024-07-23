package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SQLManager {
    private static final Logger LOGGER = LogManager.getLogger(SQLManager.class);
    private static Properties props;

    static {
        props = new Properties();
        try {
            InputStream input = SQLManager.class.getResourceAsStream("database.properties");
            props.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createDatabase() {
        LOGGER.info("createDatabase()");
        try {
            Class.forName(props.getProperty("driver"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Connection connection = DriverManager.getConnection(props.getProperty("url") + props.getProperty("service_name"), props.getProperty("user"), props.getProperty("password"));
            Statement statement = connection.createStatement();
            statement.execute("DROP DATABASE IF EXISTS " + props.getProperty("created_name"));
            statement.execute("CREATE DATABASE " + props.getProperty("created_name"));
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void fillDatabase() {
        LOGGER.info("fillDatabase()");
        try {
            Connection connection = DriverManager.getConnection(props.getProperty("url") + props.getProperty("created_name"), props.getProperty("user"), props.getProperty("password"));
            Statement statement = connection.createStatement();

            statement.execute("CREATE TABLE prime_numbers(id INTEGER, p_number INTEGER)");

            int number = 0;
            for (int i = 1; i <= 10; ) {
                if (isPrime(number)) {
                    statement.execute("INSERT INTO prime_numbers(id, p_number) VALUES (" + i + ", " + number + ")");
                    i++;
                }
                number++;
            }

            ResultSet rs = statement.executeQuery("SELECT * FROM prime_numbers");
            while(rs.next()){
                LOGGER.info("id:" + rs.getInt("id") + ", p_number:" + rs.getString("p_number"));
            }

            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Properties getProps() {
        LOGGER.info("getProps()");
        return props;
    }

    private static boolean isPrime(int number) {
        if (number <= 1) {
            return false;
        }
        for (int i = 2; i * i <= number; i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }
}
