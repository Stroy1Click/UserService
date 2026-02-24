package ru.stroy1click.user.service;


import ru.stroy1click.user.dto.UserDto;

public interface UserService {

    UserDto get(Long id);

    UserDto create(UserDto userDto);

    void update(Long id, UserDto userDto);

    void delete(Long id);

    UserDto getByEmail(String email);

    Boolean existsUserByEmail(String email);

    void updateEmailConfirmedStatus(String email);

    void updatePassword(String newPassword, String email);
}
