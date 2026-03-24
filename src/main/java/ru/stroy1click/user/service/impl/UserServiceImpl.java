package ru.stroy1click.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.stroy1click.common.exception.NotFoundException;
import ru.stroy1click.common.util.ExceptionUtils;
import ru.stroy1click.user.cache.CacheClear;
import ru.stroy1click.user.dto.UserDto;
import ru.stroy1click.user.mapper.UserMapper;
import ru.stroy1click.user.entity.User;
import ru.stroy1click.user.repository.UserRepository;
import ru.stroy1click.user.service.UserService;

import java.util.Locale;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final CacheClear cacheClear;

    private final PasswordEncoder passwordEncoder;

    private final MessageSource messageSource;

    @Override
    @Cacheable(value = "user", key = "#id")
    public UserDto get(Long id) {
        log.info("get {}", id);

        User user = this.userRepository.findById(id).orElseThrow(
                () -> ExceptionUtils.notFound("error.user.not_found_id",id));

        return this.userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        log.info("create {}", userDto);

        userDto.setPassword(this.passwordEncoder.encode(userDto.getPassword()));

        User createdUser = this.userRepository.save(
                this.userMapper.toEntity(userDto)
        );

        return this.userMapper.toDto(createdUser);
    }

    @Override
    @Transactional
    @CacheEvict(value = "user", key = "#id")
    public void update(Long id, UserDto userDto) {
        log.info("update {}, {}", id, userDto);

        this.userRepository.findById(id).ifPresentOrElse(user -> {
            user.setFirstName(userDto.getFirstName());
            user.setLastName(userDto.getLastName());
        }, () -> {
            throw new NotFoundException(
                    this.messageSource.getMessage(
                            "error.user.not_found_id",
                            new Object[]{id},
                            Locale.getDefault()
                    )
            );
        });

        this.cacheClear.clearEmail(userDto.getEmail());
    }

    @Override
    @Transactional
    @CacheEvict(value = "user", key = "#id")
    public void delete(Long id) {
        log.info("delete {}", id);

        User user = this.userRepository.findById(id).orElseThrow(
                () -> ExceptionUtils.notFound("error.user.not_found_id",id));

        this.userRepository.deleteById(id);
        this.cacheClear.clearEmail(user.getEmail());
    }

    @Override
    @Cacheable(value = "email", key = "#email")
    public UserDto getByEmail(String email) {
        log.info("getByEmail {}", email);

        User user = this.userRepository.findByEmail(email).orElseThrow(
                () -> ExceptionUtils.notFound("error.user.not_found_email",email));

        return this.userMapper.toDto(user);
    }

    @Override
    public Boolean existsUserByEmail(String email) {
        log.info("existsUserByEmail {}", email);

        return this.userRepository.existsUserByEmail(email);
    }

    @Override
    @Transactional
    public void updateEmailConfirmedStatus(String email) {
        log.info("updateEmailConfirmedStatus {}", email);

        User user = this.userRepository.findByEmail(email).orElseThrow(
                () -> ExceptionUtils.notFound("error.user.not_found_email",email));

        user.setIsEmailConfirmed(true);

        this.cacheClear.clearUserById(user.getId());
        this.cacheClear.clearEmail(email);
    }

    @Override
    @Transactional
    public void updatePassword(String newPassword, String email) {
        log.info("updatePassword {}", email);

        User user = this.userRepository.findByEmail(email).orElseThrow(
                () -> ExceptionUtils.notFound("error.user.not_found_email",email));

        user.setPassword(newPassword); //новый пароль предоставляется в уже хэшированном виде

        this.cacheClear.clearUserById(user.getId());
        this.cacheClear.clearEmail(email);
    }
}

