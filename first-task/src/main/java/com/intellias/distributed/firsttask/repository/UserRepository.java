package com.intellias.distributed.firsttask.repository;

import com.intellias.distributed.firsttask.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
}
