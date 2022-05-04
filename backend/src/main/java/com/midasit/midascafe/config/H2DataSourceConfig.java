package com.midasit.midascafe.config;

import lombok.RequiredArgsConstructor;
import org.h2.Driver;
import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@RequiredArgsConstructor
public class H2DataSourceConfig {

    private final ConfigurableEnvironment environment;


    @PostConstruct
    public void start() {
        try {
            Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092").start();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Bean
    public DataSource dataSource() {
        return getDataSource();
    }

    private DriverManagerDataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Driver.class.getName());
        dataSource.setUrl(environment.getProperty("jdbc.h2.datasource.url"));
        dataSource.setUsername(environment.getProperty("jdbc.h2.datasource.username"));
        dataSource.setPassword(environment.getProperty("jdbc.h2.datasource.password"));

        return dataSource;
    }
}