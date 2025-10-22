package com.main.ioteacher.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MariaDBDriverConfig {
    @PostConstruct
    public void loadDriver() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            System.out.println("✅ MariaDB JDBC Driver manually loaded.");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MariaDB JDBC Driver not found.");
        }
    }
}
