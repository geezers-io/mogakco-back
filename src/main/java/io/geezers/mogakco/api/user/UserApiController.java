package io.geezers.mogakco.api.user;

import io.geezers.mogakco.api.common.Endpoint;
import io.geezers.mogakco.domain.dto.user.Me;
import io.geezers.mogakco.domain.dto.user.UserSignupRequestDto;
import io.geezers.mogakco.domain.dto.user.UserSignupResponseDto;
import io.geezers.mogakco.domain.entity.User;
import io.geezers.mogakco.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping(value = Endpoint.Api.USER, produces = {MediaType.APPLICATION_JSON_VALUE})
@RestController
public class UserApiController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserSignupResponseDto> signup(@Valid @RequestBody UserSignupRequestDto signupRequestDto) {
        User user = userService.signup(signupRequestDto);

        Me me = Me.builder()
                .id(user.getId())
                .email(user.getEmail())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(UserSignupResponseDto.of(me));
    }
}
