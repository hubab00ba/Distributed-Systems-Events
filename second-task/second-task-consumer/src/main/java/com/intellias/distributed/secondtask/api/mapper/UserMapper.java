package com.intellias.distributed.secondtask.api.mapper;

import com.intellias.distributed.secondtask.api.dto.UserDto;
import com.intellias.distributed.secondtask.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User source);

    User toDocument(UserDto source);
}
