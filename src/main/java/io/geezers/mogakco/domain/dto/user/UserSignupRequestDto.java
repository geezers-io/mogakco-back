package io.geezers.mogakco.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserSignupRequestDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
