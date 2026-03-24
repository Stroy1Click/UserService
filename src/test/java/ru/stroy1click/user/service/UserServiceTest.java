package ru.stroy1click.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.stroy1click.user.cache.CacheClear;
import ru.stroy1click.user.dto.UserDto;
import ru.stroy1click.common.exception.NotFoundException;
import ru.stroy1click.user.mapper.UserMapper;
import ru.stroy1click.user.entity.Role;
import ru.stroy1click.user.entity.User;
import ru.stroy1click.user.repository.UserRepository;
import ru.stroy1click.user.service.impl.UserServiceImpl;

import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
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
        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("test@mail.com")
                .password("encoded")
                .isEmailConfirmed(false)
                .role(Role.ROLE_USER)
                .build();

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("test@mail.com");
        userDto.setPassword("password");
    }

    @Test
    void get_WhenUserExists_ShouldReturnUserDto() {
        //Arrange
        when(this.userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(this.userMapper.toDto(user))
                .thenReturn(userDto);

        //Act
        UserDto result = this.userService.get(1L);

        //Assert
        assertNotNull(result);
        assertEquals(userDto, result);
        verify(this.userRepository).findById(1L);
    }

    @Test
    void get_WhenUserDoesNotExist_ShouldThrowNotFoundException() {
        //Arrange
        when(this.userRepository.findById(1L))
                .thenReturn(Optional.empty());

        //Act & Assert
        Assertions.assertThrows(
                NotFoundException.class,
                () -> this.userService.get(1L)
        );
    }

    @Test
    void create_WhenDataIsValid_ShouldReturnUserDto() {
        //Arrange
        when(this.passwordEncoder.encode("password")).thenReturn("encoded");
        when(this.userMapper.toEntity(userDto)).thenReturn(user);
        when(this.userRepository.save(any(User.class))).thenReturn(user);
        when(this.userMapper.toDto(any(User.class))).thenReturn(userDto);

        //Act
        UserDto createdUser = this.userService.create(userDto);

        //Assert
        verify(this.passwordEncoder).encode("password");
        verify(this.userRepository).save(user);
        assertNotNull(createdUser.getId());
    }

    @Test
    void update_WhenUserExists_ShouldUpdateUser() {
        //Arrange
        when(this.userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        UserDto dto = UserDto.builder()
                .id(1L)
                .firstName("New John")
                .lastName("New Doe")
                .email("test@mail.com")
                .password("encoded")
                .isEmailConfirmed(false)
                .role(Role.ROLE_USER)
                .build();

        //Act
        this.userService.update(1L, dto);

        //Assert
        verify(this.cacheClear).clearEmail(userDto.getEmail());
        assertEquals("New John", user.getFirstName());
        assertEquals("New Doe", user.getLastName());
    }

    @Test
    void update_WhenUserDoesNotExist_ShouldThrowNotFoundException() {
        //Arrange
        when(this.userRepository.findById(1L))
                .thenReturn(Optional.empty());
        when(this.messageSource.getMessage(
                eq("error.user.not_found_id"),
                any(),
                any(Locale.class)
        )).thenReturn("User not found");

        //Act & Assert
        Assertions.assertThrows(
                NotFoundException.class,
                () -> this.userService.update(1L, userDto)
        );
    }

    @Test
    void delete_WhenUserExists_ShouldDeleteUserAndClearCache() {
        //Arrange
        when(this.userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        //Act
        this.userService.delete(1L);

        //Assert
        verify(this.userRepository).deleteById(1L);
        verify(this.cacheClear).clearEmail(user.getEmail());
    }

    @Test
    void delete_WhenUserDoesNotExist_ShouldThrowNotFoundException() {
        //Arrange
        when(this.userRepository.findById(1L))
                .thenReturn(Optional.empty());

        //Act & Assert
        Assertions.assertThrows(
                NotFoundException.class,
                () -> this.userService.delete(1L)
        );
    }

    @Test
    void getByEmail_WhenUserExists_ShouldReturnUserDto() {
        //Arrange
        when(this.userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));
        when(this.userMapper.toDto(user))
                .thenReturn(userDto);

        //Act & Assert
        UserDto result = this.userService.getByEmail("test@mail.com");

        //Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(userDto, result);
        verify(this.userRepository).findByEmail("test@mail.com");
    }

    @Test
    void getByEmail_WhenUserDoesNotExist_ShouldThrowNotFoundException() {
        //Arrange
        when(this.userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.empty());

        //Act & Assert
        Assertions.assertThrows(
                NotFoundException.class,
                () -> this.userService.getByEmail("test@mail.com")
        );
    }

    @Test
    void existsUserByEmail_WhenUserExists_ShouldReturnTrue() {
        //Arrange
        when(this.userRepository.existsUserByEmail("test@mail.com"))
                .thenReturn(true);

        //Act
        Boolean result = this.userService.existsUserByEmail("test@mail.com");

        //Assert
        Assertions.assertTrue(result);
        verify(this.userRepository).existsUserByEmail("test@mail.com");
    }

    @Test
    void updateEmailConfirmedStatus_WhenUserExists_ShouldSetEmailConfirmedTrue() {
        //Arrange
        when(this.userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        //Act
        this.userService.updateEmailConfirmedStatus("test@mail.com");

        //Assert
        Assertions.assertTrue(user.getIsEmailConfirmed());
        verify(this.cacheClear).clearUserById(user.getId());
        verify(this.cacheClear).clearEmail("test@mail.com");
    }

    @Test
    void updateEmailConfirmedStatus_WhenUserDoesNotExists_ShouldThrowNotFoundException() {
        //Arrange
        when(this.userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.empty());

        //Act & Assert
        Assertions.assertThrows(
                NotFoundException.class,
                () -> this.userService.updateEmailConfirmedStatus("test@mail.com")
        );
    }

    @Test
    void updatePassword_WhenUserExists_ShouldEncodeAndUpdatePassword() {
        //Arrange
        when(this.userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        //Act
        this.userService.updatePassword( "encodedNewPass", "test@mail.com");

        //Assert
        assertEquals("encodedNewPass", user.getPassword());
        verify(this.cacheClear).clearUserById(user.getId());
        verify(this.cacheClear).clearEmail("test@mail.com");
    }

    @Test
    void updatePassword_WhenUserDoesNotExists_ShouldThrowNotFoundException() {
        //Arrange
        when(this.userRepository.findByEmail("test@mail.com")).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(NotFoundException.class,
                () -> this.userService.updatePassword("newPass", "test@mail.com")
        );
    }
}