package com.example.stresstest.repo;

import com.example.stresstest.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface UserRepository extends MongoRepository<User, String> {
}
