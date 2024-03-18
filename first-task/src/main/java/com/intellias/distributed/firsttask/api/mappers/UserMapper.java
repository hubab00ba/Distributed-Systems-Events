package com.intellias.distributed.firsttask.api.mappers;

import com.intellias.distributed.firsttask.api.dto.UserDto;
import com.intellias.distributed.firsttask.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User source);

    User toDocument(UserDto source);
}
