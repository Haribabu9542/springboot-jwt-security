package com.java.springsecuritydemo;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.DriverTimeoutException;
import com.java.springsecuritydemo.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.cassandra.core.CassandraAdminOperations;

@SpringBootApplication
public class SpringSecurityDemoApplication {

    @Autowired
    private CassandraAdminOperations adminTemplate;

   @PostConstruct
   public void createTable() {
       adminTemplate.createTable(true, CqlIdentifier.fromCql("user"), User.class, null);
   }

    public static void main(String[] args) {
//       try {
           SpringApplication.run(SpringSecurityDemoApplication.class, args);
//       }catch (DriverTimeoutException e){
//           for (int i = 0; i < 4; i++) {
//               SpringApplication.run(SpringSecurityDemoApplication.class, args);
//           }
//       }
    }

}
