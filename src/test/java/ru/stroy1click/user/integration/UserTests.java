package ru.stroy1click.user.integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import ru.stroy1click.user.dto.UserDto;
import ru.stroy1click.user.model.Role;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserTests {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void createUser_WithValidData_ReturnsCreatedUser(){
        UserDto userDto = new UserDto(null, "firstName", "lastName", "email@gmail.com", "password", false, Role.ROLE_USER);
        HttpEntity<UserDto> createEntity = new HttpEntity<>(userDto);

        ResponseEntity<UserDto> responseEntity = this.testRestTemplate.exchange(
                "/api/v1/users",
                HttpMethod.POST,
                createEntity,
                UserDto.class
        );

        Assertions.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        Assertions.assertNotNull(responseEntity.getHeaders().getLocation());

        UserDto createUser = this.testRestTemplate.exchange(
                responseEntity.getHeaders().getLocation(),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                UserDto.class
        ).getBody();

        Assertions.assertNotNull(createUser.getId());
        Assertions.assertEquals(userDto.getFirstName(),createUser.getFirstName());
    }

    @Test
    public void updateUser_WithValidData_ReturnsSuccessMessage() {
        UserDto userDto = new UserDto(null, "newFirstName", "lastName", "email@gmail.com", "password", false, Role.ROLE_USER);
        HttpEntity<UserDto> updatedEntity = new HttpEntity<>(userDto);

        ResponseEntity<String> responseEntity = this.testRestTemplate.exchange(
                "/api/v1/users/2",
                HttpMethod.PATCH,
                updatedEntity,
                String.class
        );

        Assertions.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        Assertions.assertEquals("Пользователь обновлён", responseEntity.getBody());

        UserDto updatedUserDto = this.testRestTemplate.exchange(
                "/api/v1/users/2",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                UserDto.class
        ).getBody();

        Assertions.assertEquals(updatedUserDto.getFirstName(), userDto.getFirstName());
    }

    @Test
    public void getUser_ById_ReturnsUserDto() {
        ResponseEntity<UserDto> responseEntity = this.testRestTemplate.exchange(
                "/api/v1/users/1",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                UserDto.class
        );

        Assertions.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        Assertions.assertNotNull(responseEntity.getBody().getFirstName());
    }

    @Test
    public void getUser_ByEmail_ReturnsUserDto() {
        String email = "mike_thompson@gmail.com";
        ResponseEntity<UserDto> responseEntity = this.testRestTemplate.exchange(
                "/api/v1/users/email?email=" + email,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                UserDto.class
        );

        Assertions.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        Assertions.assertEquals("Mike", responseEntity.getBody().getFirstName());
    }

    @Test
    public void getUserByEmail_WhenEmailNotFound_ReturnsNotFound() {
        String email = "notfound@gmail.com";
        ResponseEntity<ProblemDetail> responseEntity = this.testRestTemplate.exchange(
                "/api/v1/users/email?email=" + email,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                ProblemDetail.class
        );

        Assertions.assertTrue(responseEntity.getStatusCode().is4xxClientError());
        Assertions.assertEquals("Пользователь с электронной почтой %s не найден".formatted(email),
                responseEntity.getBody().getDetail());
    }

    @Test
    public void deleteUser_WithValidId_ReturnsSuccessMessage() {
        ResponseEntity<String> responseEntity = this.testRestTemplate.exchange(
                "/api/v1/users/3",
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                String.class
        );

        Assertions.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        Assertions.assertEquals("Пользователь удалён", responseEntity.getBody());

        ResponseEntity<ProblemDetail> deletedUser = this.testRestTemplate.exchange(
                "/api/v1/users/3",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                ProblemDetail.class
        );

        Assertions.assertTrue(deletedUser.getStatusCode().is4xxClientError());
        Assertions.assertEquals("Не найдено", deletedUser.getBody().getTitle());
    }

    @Test
    public void updateUser_WithEmptyFirstName_ReturnsValidationError() {
        UserDto userDtoWithEmptyFirstName = new UserDto(null, "", "lastName", "email@gmail.com", "password", false, Role.ROLE_USER);
        HttpEntity<UserDto> entityWithEmptyFirstName = new HttpEntity<>(userDtoWithEmptyFirstName);

        ResponseEntity<ProblemDetail> responseWithEmptyFirstName = this.testRestTemplate.exchange(
                "/api/v1/users/2",
                HttpMethod.PATCH,
                entityWithEmptyFirstName,
                ProblemDetail.class
        );

        Assertions.assertTrue(responseWithEmptyFirstName.getStatusCode().is4xxClientError());
        Assertions.assertEquals("Ошибка валидации", responseWithEmptyFirstName.getBody().getTitle());
    }

    @Test
    public void updateUser_WithTooShortFirstName_ReturnsValidationError() {
        UserDto userDtoWithShortFirstName = new UserDto(null, "A", "lastName", "email@gmail.com", "password", false, Role.ROLE_USER);
        HttpEntity<UserDto> entityWithShortFirstName = new HttpEntity<>(userDtoWithShortFirstName);

        ResponseEntity<ProblemDetail> responseWithShortFirstName = this.testRestTemplate.exchange(
                "/api/v1/users/2",
                HttpMethod.PATCH,
                entityWithShortFirstName,
                ProblemDetail.class
        );

        Assertions.assertTrue(responseWithShortFirstName.getStatusCode().is4xxClientError());
        Assertions.assertEquals("Ошибка валидации", responseWithShortFirstName.getBody().getTitle());
        Assertions.assertTrue(responseWithShortFirstName.getBody().getDetail().contains("Минимальная длина имени составляет 2 символа"));
    }

    @Test
    public void updateUser_WithEmptyEmail_ReturnsValidationError() {
        UserDto userDtoWithEmptyEmail = new UserDto(null, "firstName", "lastName", "", "password", false, Role.ROLE_USER);
        HttpEntity<UserDto> entityWithEmptyEmail = new HttpEntity<>(userDtoWithEmptyEmail);

        ResponseEntity<ProblemDetail> responseWithEmptyEmail = this.testRestTemplate.exchange(
                "/api/v1/users/2",
                HttpMethod.PATCH,
                entityWithEmptyEmail,
                ProblemDetail.class
        );


        Assertions.assertTrue(responseWithEmptyEmail.getStatusCode().is4xxClientError());
        Assertions.assertEquals("Ошибка валидации", responseWithEmptyEmail.getBody().getTitle());
    }

    @Test
    public void updateUser_WithInvalidEmailFormat_ReturnsValidationError() {
        UserDto userDtoWithInvalidEmail = new UserDto(null, "firstName", "lastName", "invalid-email", "password", false, Role.ROLE_USER);
        HttpEntity<UserDto> entityWithInvalidEmail = new HttpEntity<>(userDtoWithInvalidEmail);

        ResponseEntity<ProblemDetail> responseWithInvalidEmail = this.testRestTemplate.exchange(
                "/api/v1/users/2",
                HttpMethod.PATCH,
                entityWithInvalidEmail,
                ProblemDetail.class
        );

        Assertions.assertTrue(responseWithInvalidEmail.getStatusCode().is4xxClientError());
        Assertions.assertEquals("Ошибка валидации", responseWithInvalidEmail.getBody().getTitle());
        Assertions.assertEquals("Электронная почта должна быть валидной(иметь @)", responseWithInvalidEmail.getBody().getDetail());
    }

    @Test
    public void updateUser_WithNullRole_ReturnsValidationError() {
        UserDto userDtoWithNullRole = new UserDto(null, "firstName", "lastName", "email@gmail.com", "password", false, null);
        HttpEntity<UserDto> entityWithNullRole = new HttpEntity<>(userDtoWithNullRole);

        ResponseEntity<ProblemDetail> responseWithNullRole = this.testRestTemplate.exchange(
                "/api/v1/users/2",
                HttpMethod.PATCH,
                entityWithNullRole,
                ProblemDetail.class
        );

        Assertions.assertTrue(responseWithNullRole.getStatusCode().is4xxClientError());
        Assertions.assertEquals("Ошибка валидации", responseWithNullRole.getBody().getTitle());
        Assertions.assertEquals("Роль не может быть пустой", responseWithNullRole.getBody().getDetail());
    }
}
