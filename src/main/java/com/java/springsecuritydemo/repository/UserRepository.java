package com.java.springsecuritydemo.repository;

import com.java.springsecuritydemo.model.User;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CassandraRepository<User,String> {
    Optional<User> findByEmail(String email);
}
