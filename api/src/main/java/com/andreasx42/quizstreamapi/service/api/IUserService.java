package com.andreasx42.quizstreamapi.service.api;

import com.andreasx42.quizstreamapi.dto.UserDto;
import com.andreasx42.quizstreamapi.entity.User;

public interface IUserService extends IService<User, UserDto> {

    UserDto create(UserDto userDto);

    User getByName(String username);

}
