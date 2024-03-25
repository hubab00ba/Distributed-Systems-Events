package com.intellias.distributed.secondtask.repository;

import com.intellias.distributed.secondtask.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
}
