package com.backend.tasks.mapper;

import com.backend.tasks.dto.UserCreateDto;
import com.backend.tasks.dto.UserReadDto;
import com.backend.tasks.dto.UserUpdateDto;
import com.backend.tasks.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User userCreateDtoToUser(UserCreateDto userDto);

    User userUpdateDtoToUser(UserUpdateDto userUpdateDto);

    UserReadDto userToUserReadDto(User user);

}
