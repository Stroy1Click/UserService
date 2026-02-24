package ru.stroy1click.user.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.stroy1click.common.exception.ValidationException;
import ru.stroy1click.user.dto.UserDto;
import ru.stroy1click.user.service.UserService;
import ru.stroy1click.common.util.ValidationErrorUtils;
import ru.stroy1click.user.validator.UserCreateValidator;

import java.net.URI;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "Interaction with user")
@RateLimiter(name = "userLimiter")
public class UserController {

    private final UserService userService;

    private final UserCreateValidator userCreateValidator;

    private final MessageSource messageSource;

    @GetMapping("/{id}")
    @Operation(summary = "Получение пользователя.")
    public UserDto get(@PathVariable("id") Long id){
        return this.userService.get(id);
    }

    @GetMapping(params = "email")
    @Operation(summary = "Получение пользователя по электронной почте.")
    public UserDto getByEmail(@RequestParam("email") String email){
        return this.userService.getByEmail(email);
    }

    @PostMapping
    @Operation(summary = "Создание пользователя.")
    public ResponseEntity<UserDto> create(@RequestBody @Valid UserDto userDto, BindingResult bindingResult){
        if(bindingResult.hasFieldErrors()) throw new ValidationException(ValidationErrorUtils.collectErrorsToString(
                bindingResult.getFieldErrors()
        ));

        this.userCreateValidator.validate(userDto.getEmail());

        UserDto createdUser = this.userService.create(userDto);

        return ResponseEntity
                .created(URI.create("/api/v1/users/" + createdUser.getId()))
                .body(createdUser);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновление пользователя.")
    public ResponseEntity<String> update(@PathVariable("id") Long id, @RequestBody @Valid UserDto userDto, BindingResult bindingResult){
        if(bindingResult.hasFieldErrors()) throw new ValidationException(ValidationErrorUtils.collectErrorsToString(
                bindingResult.getFieldErrors()
        ));

        this.userService.update(id, userDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                this.messageSource.getMessage(
                        "info.user.updated",
                        null,
                        Locale.getDefault()
                )
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление пользователя.")
    public ResponseEntity<String> delete(@PathVariable("id") Long id){
        this.userService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                this.messageSource.getMessage(
                        "info.user.deleted",
                        null,
                        Locale.getDefault()
                )
        );
    }
}
