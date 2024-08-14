package com.andreasx42.quizstreamapi.util.mapper;

import com.andreasx42.quizstreamapi.dto.user.UserOutboundDto;
import com.andreasx42.quizstreamapi.dto.user.UserRegisterDto;
import com.andreasx42.quizstreamapi.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserMapper {


    public UserOutboundDto mapFromEntityOutbound(User user) {
        return new UserOutboundDto(user.getId(), user.getUsername(), user.getEmail());
    }


    public User mapToEntity(UserRegisterDto userDto) {
        return new User(userDto.username(), userDto.email(), userDto.password());
    }

}
