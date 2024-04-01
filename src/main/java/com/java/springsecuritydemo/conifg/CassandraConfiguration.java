package com.java.springsecuritydemo.conifg;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CassandraConfiguration {
    @Bean
    public CassandraConfig cluster() {
        CassandraConfig cluster = new CassandraConfig();
        cluster.getContactPoints();
        cluster.getPort();
        cluster.getKeyspaceName();
        return cluster;
    }
}
