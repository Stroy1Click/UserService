package ru.stroy1click.user.validator.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import ru.stroy1click.common.exception.AlreadyExistsException;
import ru.stroy1click.user.service.UserService;
import ru.stroy1click.user.validator.UserCreateValidator;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class UserCreateValidatorImpl implements UserCreateValidator {

    private final UserService userService;

    private final MessageSource messageSource;

    @Override
    public void validate(String email){
        if(this.userService.existsUserByEmail(email)){
            throw new AlreadyExistsException(
                    this.messageSource.getMessage(
                            "error.details.already_exist",
                            new Object[]{email},
                            Locale.getDefault()
                    )
            );
        }
    }
}
