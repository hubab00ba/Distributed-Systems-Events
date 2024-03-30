package com.example.service;

import com.example.api.message.UserDataRecord;
import com.example.model.UserData;
import com.example.repo.UserDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RabbitListener(queues = "user-created-payload")
public class UserReplicationService {

  private static final Logger log = LoggerFactory.getLogger(UserReplicationService.class);
  private final UserDataRepository repository;

  public UserReplicationService(UserDataRepository repository) {
    this.repository = repository;
  }

  @RabbitHandler
  public void retrieveAndSaveUserData(UserDataRecord userDataRecord) {
    UserData userData = repository.save(createUserDataEntry(userDataRecord));
    log.info("Data for userId={} is successfully saved in Replication Service", userData.getId());
  }

  private UserData createUserDataEntry(UserDataRecord userDataRecord) {
    return new UserData(userDataRecord.id(), userDataRecord.username(), userDataRecord.email(), userDataRecord.registeredAt());
  }

}
