package app.quizstream.util.mapper;

import org.springframework.stereotype.Component;

import app.quizstream.dto.user.UserOutboundDto;
import app.quizstream.dto.user.UserRegisterDto;
import app.quizstream.entity.User;

@Component
public class UserMapper {


    public UserOutboundDto mapFromEntityOutbound(User user) {
        return new UserOutboundDto(user.getId(), user.getUsername(), user.getEmail(), user.getRole()
                .name());
    }


    public User mapToEntity(UserRegisterDto userDto) {
        return new User(userDto.username(), userDto.email(), userDto.password());
    }

}
