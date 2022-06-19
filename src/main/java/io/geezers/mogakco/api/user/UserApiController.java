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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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

    @GetMapping("/authentication")
    public ResponseEntity<Void> authenticate(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
        }
        return ResponseEntity.status(HttpServletResponse.SC_OK).build();
    }
}
