package ru.stroy1click.user.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import ru.stroy1click.user.config.TestcontainersConfiguration;
import ru.stroy1click.user.dto.UserDto;
import ru.stroy1click.user.entity.Role;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void create_WhenValidDataProvided_ShouldReturnCreatedUser(){
        //Arrange
        UserDto userDto = new UserDto(null, "firstName", "lastName", "email@gmail.com", "password", false, Role.ROLE_USER);
        HttpEntity<UserDto> createEntity = new HttpEntity<>(userDto);

        //Act
        ResponseEntity<UserDto> responseEntity = this.testRestTemplate.exchange(
                "/api/v1/users",
                HttpMethod.POST,
                createEntity,
                UserDto.class
        );

        //Assert
        Assertions.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        Assertions.assertNotNull(responseEntity.getHeaders().getLocation());
        UserDto createUser = this.testRestTemplate.exchange(
                responseEntity.getHeaders().getLocation(),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                UserDto.class
        ).getBody();
        Assertions.assertNotNull(createUser);
        Assertions.assertNotNull(createUser.getId());
        Assertions.assertEquals(userDto.getFirstName(),createUser.getFirstName());
    }

    @Test
    public void update_WhenValidDataProvidedAndUserExists_ShouldReturnSuccessMessage() {
        //Arrange
        UserDto userDto = new UserDto(null, "newFirstName", "lastName", "email@gmail.com", "password", false, Role.ROLE_USER);
        HttpEntity<UserDto> updatedEntity = new HttpEntity<>(userDto);

        //Act
        ResponseEntity<String> responseEntity = this.testRestTemplate.exchange(
                "/api/v1/users/2",
                HttpMethod.PATCH,
                updatedEntity,
                String.class
        );

        //Assert
        Assertions.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        Assertions.assertEquals("Пользователь обновлён", responseEntity.getBody());
        UserDto updatedUserDto = this.testRestTemplate.exchange(
                "/api/v1/users/2",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                UserDto.class
        ).getBody();
        Assertions.assertNotNull(updatedUserDto);
        Assertions.assertEquals(updatedUserDto.getFirstName(), userDto.getFirstName());
    }

    @Test
    public void get_WhenUserExists_ShouldReturnUserDto() {
        //Act
        ResponseEntity<UserDto> responseEntity = this.testRestTemplate.exchange(
                "/api/v1/users/1",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                UserDto.class
        );

        //Assert
        Assertions.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertNotNull(responseEntity.getBody().getFirstName());
    }

    @Test
    public void getByEmail_WhenUserExists_ShouldReturnUserDto() {
        //Arrange
        String email = "mike_thompson@gmail.com";

        //Act
        ResponseEntity<UserDto> responseEntity = this.testRestTemplate.exchange(
                "/api/v1/users?email=" + email,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                UserDto.class
        );

        //Assert
        Assertions.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals("Mike", responseEntity.getBody().getFirstName());
    }

    @Test
    public void getByEmail_WhenUserDoesNotExists_ShouldThrowNotFoundException() {
        //Arrange
        String email = "notfound@gmail.com";

        //Act
        ResponseEntity<ProblemDetail> responseEntity = this.testRestTemplate.exchange(
                "/api/v1/users?email=" + email,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                ProblemDetail.class
        );

        //Assert
        Assertions.assertTrue(responseEntity.getStatusCode().is4xxClientError());
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals("Пользователь с электронной почтой %s не найден".formatted(email),
                responseEntity.getBody().getDetail());
    }

    @Test
    public void delete_WhenUserExists_ShouldReturnSuccessMessage() {
        //Act
        ResponseEntity<String> responseEntity = this.testRestTemplate.exchange(
                "/api/v1/users/3",
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                String.class
        );

        //Assert
        Assertions.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        Assertions.assertEquals("Пользователь удалён", responseEntity.getBody());

        ResponseEntity<ProblemDetail> deletedUser = this.testRestTemplate.exchange(
                "/api/v1/users/3",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                ProblemDetail.class
        );

        Assertions.assertTrue(deletedUser.getStatusCode().is4xxClientError());
        Assertions.assertNotNull(deletedUser.getBody());
        Assertions.assertEquals("Не найдено", deletedUser.getBody().getTitle());
    }
}
