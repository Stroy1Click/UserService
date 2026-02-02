package ru.stroy1click.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserServiceUpdatePasswordRequest {

    @NotBlank(message = "{validation.user_service_update_password_request.new_password.not_blank}")
    @Size(min = 8, max = 50, message = "{validation.user_service_update_password_request.new_password.length}")
    private String newPassword;

    @NotBlank(message = "{validation.user_service_update_password_request.email.not_blank}")
    @Length(min = 8, max = 50, message = "{validation.user_service_update_password_request.email.length}")
    private String email;

}
