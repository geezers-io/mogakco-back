package io.geezers.mogakco.domain.dto.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class UserSignupResponseDto {

    private final Me me;

    private UserSignupResponseDto(Me me) {
        this.me = me;
    }

    public static UserSignupResponseDto of(Me me) {
        return new UserSignupResponseDto(me);
    }
}
