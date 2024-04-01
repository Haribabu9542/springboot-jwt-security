package com.java.springsecuritydemo.conifg;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@EnableCassandraRepositories
public class CassandraConfig extends AbstractCassandraConfiguration {
//public class CassandraConfig {

        @Value("${contactpoint}")
    private String contactpoint;
    @Value("${keyspace}")
    private String keyspace;
    @Value("${port}")
    private int port;
    @Override
    protected String getKeyspaceName() {
        return keyspace;
    }
    @Override
    public String getContactPoints() {
        return contactpoint;
    }
    @Override
    protected int getPort() {
        return port;
    }
//    private final CqlSession cqlSession;

//    @Autowired
//    public CassandraConfig(CqlSession cqlSession) {
//        this.cqlSession = cqlSession;
//    }



}
