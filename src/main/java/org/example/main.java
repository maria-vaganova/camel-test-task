package org.example;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import javax.sql.DataSource;
import java.util.Properties;

public class main {
    private static final Logger LOGGER = LogManager.getLogger(main.class);

    public static void main(String args[]) throws Exception {
        SQLManager.createDatabase();
        SQLManager.fillDatabase();

        Properties props = SQLManager.getProps();
        String fullURL = props.getProperty("url") + props.getProperty("created_name") + "?user=" + props.getProperty("user") + "&password=" + props.getProperty("password");
        LOGGER.info(fullURL);

        DefaultCamelContext camel = new DefaultCamelContext();
        DataSource dataSource = new DriverManagerDataSource(fullURL);
        camel.getRegistry().bind("test", dataSource);

        camel.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("timer:base?period=5000")
                        .routeId("JDBC route")
                        .setBody(simple("select id, p_number from prime_numbers"))
                        .to("jdbc:test")
                        .log(">>> ${body}");
            }
        });

        camel.start();

        Thread.sleep(15_000);

        camel.stop();
    }
}
