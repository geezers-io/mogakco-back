package io.geezers.mogakco.domain.dto.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class UserLoginResponseDto {

    private final Me me;

    private UserLoginResponseDto(Me me) {
        this.me = me;
    }

    public static UserLoginResponseDto of(Me me) {
        return new UserLoginResponseDto(me);
    }
}
