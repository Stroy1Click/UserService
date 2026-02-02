package ru.stroy1click.user.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.stroy1click.user.cache.CacheClear;
import ru.stroy1click.user.dto.UserDto;
import ru.stroy1click.user.exception.NotFoundException;
import ru.stroy1click.user.mapper.UserMapper;
import ru.stroy1click.user.entity.Role;
import ru.stroy1click.user.entity.User;
import ru.stroy1click.user.repository.UserRepository;
import ru.stroy1click.user.service.impl.UserServiceImpl;

import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CacheClear cacheClear;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        this.user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("test@mail.com")
                .password("encoded")
                .emailConfirmed(false)
                .role(Role.ROLE_USER)
                .build();

        this.userDto = new UserDto();
        this.userDto.setId(1L);
        this.userDto.setFirstName("John");
        this.userDto.setLastName("Doe");
        this.userDto.setEmail("test@mail.com");
        this.userDto.setPassword("password");
    }

    @Test
    void get_ShouldReturnUserDto_WhenUserExists() {
        when(this.userRepository.findById(1L))
                .thenReturn(Optional.of(this.user));
        when(this.userMapper.toDto(this.user))
                .thenReturn(this.userDto);

        UserDto result = this.userService.get(1L);

        assertNotNull(result);
        assertEquals(this.userDto, result);
        verify(this.userRepository).findById(1L);
    }

    @Test
    void get_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(this.userRepository.findById(1L))
                .thenReturn(Optional.empty());
        when(this.messageSource.getMessage(
                eq("error.user.not_found_id"),
                any(),
                any(Locale.class)
        )).thenReturn("User not found");

        Assertions.assertThrows(
                NotFoundException.class,
                () -> this.userService.get(1L)
        );
    }

    @Test
    void create_ShouldEncodePasswordAndSaveUser() {
        when(this.passwordEncoder.encode("password"))
                .thenReturn("encoded");
        when(this.userMapper.toEntity(this.userDto))
                .thenReturn(this.user);

        this.userService.create(this.userDto);

        verify(this.passwordEncoder).encode("password");
        verify(this.userRepository).save(this.user);
    }

    @Test
    void update_ShouldUpdateUser_WhenUserExists() {
        when(this.userRepository.findById(1L))
                .thenReturn(Optional.of(this.user));

        this.userService.update(1L, this.userDto);

        verify(this.userRepository).save(any(User.class));
        verify(this.cacheClear).clearEmail(this.userDto.getEmail());
    }

    @Test
    void update_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(this.userRepository.findById(1L))
                .thenReturn(Optional.empty());
        when(this.messageSource.getMessage(
                eq("error.user.not_found_id"),
                any(),
                any(Locale.class)
        )).thenReturn("User not found");

        Assertions.assertThrows(
                NotFoundException.class,
                () -> this.userService.update(1L, this.userDto)
        );
    }

    @Test
    void delete_ShouldDeleteUserAndClearCache_WhenUserExists() {
        when(this.userRepository.findById(1L))
                .thenReturn(Optional.of(this.user));

        this.userService.delete(1L);

        verify(this.userRepository).deleteById(1L);
        verify(this.cacheClear).clearEmail(this.user.getEmail());
    }

    @Test
    void delete_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(this.userRepository.findById(1L))
                .thenReturn(Optional.empty());
        when(this.messageSource.getMessage(
                eq("error.user.not_found_id"),
                any(),
                any(Locale.class)
        )).thenReturn("User not found");

        Assertions.assertThrows(
                NotFoundException.class,
                () -> this.userService.delete(1L)
        );
    }

    @Test
    void getByEmail_ShouldReturnUserDto_WhenUserExists() {
        when(this.userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(this.user));
        when(this.userMapper.toDto(this.user))
                .thenReturn(this.userDto);

        UserDto result = this.userService.getByEmail("test@mail.com");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(this.userDto, result);
        verify(this.userRepository).findByEmail("test@mail.com");
    }

    @Test
    void getByEmail_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(this.userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.empty());
        when(this.messageSource.getMessage(
                eq("error.user.not_found_email"),
                any(),
                any(Locale.class)
        )).thenReturn("User not found");

        Assertions.assertThrows(
                NotFoundException.class,
                () -> this.userService.getByEmail("test@mail.com")
        );
    }

    @Test
    void existsUserByEmail_ShouldReturnTrue_WhenUserExists() {
        when(this.userRepository.existsUserByEmail("test@mail.com"))
                .thenReturn(true);

        Boolean result = this.userService.existsUserByEmail("test@mail.com");

        Assertions.assertTrue(result);
        verify(this.userRepository).existsUserByEmail("test@mail.com");
    }

    @Test
    void updateEmailConfirmedStatus_ShouldSetEmailConfirmedTrue() {
        when(this.userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(this.user));

        this.userService.updateEmailConfirmedStatus("test@mail.com");

        Assertions.assertTrue(this.user.getEmailConfirmed());
        verify(this.cacheClear).clearUserById(this.user.getId());
        verify(this.cacheClear).clearEmail("test@mail.com");
    }

    @Test
    void updateEmailConfirmedStatus_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(this.userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.empty());
        when(this.messageSource.getMessage(
                eq("error.user.not_found_email"),
                any(),
                any(Locale.class)
        )).thenReturn("User not found");

        Assertions.assertThrows(
                NotFoundException.class,
                () -> this.userService.updateEmailConfirmedStatus("test@mail.com")
        );
    }

    @Test
    void updatePassword_ShouldEncodeAndUpdatePassword() {
        when(this.userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(this.user));
        when(this.passwordEncoder.encode("newPass"))
                .thenReturn("encodedNewPass");

        this.userService.updatePassword("test@mail.com", "newPass");
        assertEquals("encodedNewPass", this.user.getPassword());
        verify(this.cacheClear).clearUserById(this.user.getId());
        verify(this.cacheClear).clearEmail("test@mail.com");
    }

    @Test
    void updatePassword_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(this.userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.empty());
        when(this.messageSource.getMessage(
                eq("error.user.not_found_id"),
                any(),
                any(Locale.class)
        )).thenReturn("User not found");

        assertThrows(
                NotFoundException.class,
                () -> this.userService.updatePassword("test@mail.com", "newPass")
        );
    }
}