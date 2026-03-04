package ru.stroy1click.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.stroy1click.user.dto.UserDto;
import ru.stroy1click.user.entity.Role;
import ru.stroy1click.user.service.UserService;
import ru.stroy1click.user.validator.UserCreateValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserCreateValidator userCreateValidator;

    @Test
    public void create_WhenUserDtoFirstNameIsEmpty_ThrowValidationException() throws Exception {
        //Arrange
        UserDto dto = UserDto.builder()
                .lastName("LastName")
                .email("email@gmail.com")
                .isEmailConfirmed(false)
                .role(Role.ROLE_USER)
                .password("{nope}password")
                .build();
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto));

        //Act
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();
        String string = result.getResponse().getContentAsString();
        ProblemDetail problemDetail = new ObjectMapper().readValue(string, ProblemDetail.class);
        int status = result.getResponse().getStatus();

        //Assert
        assertEquals(400, status);
        assertEquals("Ошибка валидации", problemDetail.getTitle());
    }

    @Test
    public void create_WhenUserDtoPasswordIsEmpty_ThrowValidationException() throws Exception {
        //Arrange
        UserDto dto = UserDto.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .email("email@gmail.com")
                .isEmailConfirmed(false)
                .role(Role.ROLE_USER)
                .build();
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto));

        //Act
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();
        String string = result.getResponse().getContentAsString();
        ProblemDetail problemDetail = new ObjectMapper().readValue(string, ProblemDetail.class);
        int status = result.getResponse().getStatus();

        //Assert
        assertEquals(400, status);
        assertEquals("Ошибка валидации", problemDetail.getTitle());
        assertEquals("Пароль не может быть пустым", problemDetail.getDetail());
    }

    @Test
    public void create_WhenUserDtoFirstNameIsShort_ThrowValidationException() throws Exception {
        //Arrange
        UserDto dto = UserDto.builder()
                .firstName("И")
                .lastName("LastName")
                .email("email@gmail.com")
                .isEmailConfirmed(false)
                .role(Role.ROLE_USER)
                .password("{nope}password")
                .build();
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto));

        //Act
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();
        String string = result.getResponse().getContentAsString();
        ProblemDetail problemDetail = new ObjectMapper().readValue(string, ProblemDetail.class);
        int status = result.getResponse().getStatus();

        //Assert
        assertEquals(400, status);
        assertEquals("Ошибка валидации", problemDetail.getTitle());
        assertEquals("Минимальная длина имени составляет 2 символа, максимальная - 30 символов", problemDetail.getDetail());
    }

    @Test
    public void create_WhenUserDtoEmailIsEmpty_ThrowValidationException() throws Exception {
        //Arrange
        UserDto dto = UserDto.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .isEmailConfirmed(false)
                .role(Role.ROLE_USER)
                .password("{nope}password")
                .build();
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto));

        //Act
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();
        String string = result.getResponse().getContentAsString();
        ProblemDetail problemDetail = new ObjectMapper().readValue(string, ProblemDetail.class);
        int status = result.getResponse().getStatus();

        //Assert
        assertEquals(400, status);
        assertEquals("Ошибка валидации", problemDetail.getTitle());
    }

    @Test
    public void create_WithInvalidEmailFormat_ThrowValidationException() throws Exception {
        //Arrange
        UserDto dto = UserDto.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .email("invalidemailgmail.com")
                .isEmailConfirmed(false)
                .role(Role.ROLE_USER)
                .password("{nope}password")
                .build();
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto));

        //Act
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();
        String string = result.getResponse().getContentAsString();
        ProblemDetail problemDetail = new ObjectMapper().readValue(string, ProblemDetail.class);
        int status = result.getResponse().getStatus();

        //Assert
        assertEquals(400, status);
        assertEquals("Ошибка валидации", problemDetail.getTitle());
        assertEquals("Электронная почта должна быть валидной(иметь @)", problemDetail.getDetail());
    }

    @Test
    public void create_WithNullRole_ThrowValidationException() throws Exception {
        //Arrange
        UserDto dto = UserDto.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .email("email@gmail.com")
                .isEmailConfirmed(false)
                .password("{nope}password")
                .build();
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto));

        //Act
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();
        String string = result.getResponse().getContentAsString();
        ProblemDetail problemDetail = new ObjectMapper().readValue(string, ProblemDetail.class);
        int status = result.getResponse().getStatus();

        //Assert
        assertEquals(400, status);
        assertEquals("Ошибка валидации", problemDetail.getTitle());
        assertEquals("Роль не может быть пустой", problemDetail.getDetail());
    }

    @Test
    public void update_WhenUserDtoFirstNameIsEmpty_ThrowValidationException() throws Exception {
        //Arrange
        UserDto dto = UserDto.builder()
                .lastName("LastName")
                .email("email@gmail.com")
                .isEmailConfirmed(false)
                .role(Role.ROLE_USER)
                .password("{nope}password")
                .build();
        RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto));

        //Act
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();
        String string = result.getResponse().getContentAsString();
        ProblemDetail problemDetail = new ObjectMapper().readValue(string, ProblemDetail.class);
        int status = result.getResponse().getStatus();

        //Assert
        assertEquals(400, status);
        assertEquals("Ошибка валидации", problemDetail.getTitle());}

    @Test
    public void update_WhenUserDtoFirstNameIsShort_ThrowValidationException() throws Exception {
        //Arrange
        UserDto dto = UserDto.builder()
                .firstName("И")
                .lastName("LastName")
                .email("email@gmail.com")
                .isEmailConfirmed(false)
                .role(Role.ROLE_USER)
                .password("{nope}password")
                .build();
        RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto));

        //Act
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();
        String string = result.getResponse().getContentAsString();
        ProblemDetail problemDetail = new ObjectMapper().readValue(string, ProblemDetail.class);
        int status = result.getResponse().getStatus();

        //Assert
        assertEquals(400, status);
        assertEquals("Ошибка валидации", problemDetail.getTitle());
        assertEquals("Минимальная длина имени составляет 2 символа, максимальная - 30 символов", problemDetail.getDetail());
    }

    @Test
    public void update_WhenUserDtoEmailIsEmpty_ThrowValidationException() throws Exception {
        //Arrange
        UserDto dto = UserDto.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .isEmailConfirmed(false)
                .role(Role.ROLE_USER)
                .password("{nope}password")
                .build();
        RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto));

        //Act
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();
        String string = result.getResponse().getContentAsString();
        ProblemDetail problemDetail = new ObjectMapper().readValue(string, ProblemDetail.class);
        int status = result.getResponse().getStatus();

        //Assert
        assertEquals(400, status);
        assertEquals("Ошибка валидации", problemDetail.getTitle());
    }

    @Test
    public void update_WithInvalidEmailFormat_ThrowValidationException() throws Exception {
        //Arrange
        UserDto dto = UserDto.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .email("invalidemailgmail.com")
                .isEmailConfirmed(false)
                .role(Role.ROLE_USER)
                .password("{nope}password")
                .build();
        RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto));

        //Act
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();
        String string = result.getResponse().getContentAsString();
        ProblemDetail problemDetail = new ObjectMapper().readValue(string, ProblemDetail.class);
        int status = result.getResponse().getStatus();

        //Assert
        assertEquals(400, status);
        assertEquals("Ошибка валидации", problemDetail.getTitle());
        assertEquals("Электронная почта должна быть валидной(иметь @)", problemDetail.getDetail());
    }

    @Test
    public void update_WithNullRole_ThrowValidationException() throws Exception {
        //Arrange
        UserDto dto = UserDto.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .email("email@gmail.com")
                .isEmailConfirmed(false)
                .password("{nope}password")
                .build();
        RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto));

        //Act
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();
        String string = result.getResponse().getContentAsString();
        ProblemDetail problemDetail = new ObjectMapper().readValue(string, ProblemDetail.class);
        int status = result.getResponse().getStatus();

        //Assert
        assertEquals(400, status);
        assertEquals("Ошибка валидации", problemDetail.getTitle());
        assertEquals("Роль не может быть пустой", problemDetail.getDetail());
    }
}
