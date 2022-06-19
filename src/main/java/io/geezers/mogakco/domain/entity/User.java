package io.geezers.mogakco.domain.entity;

import io.geezers.mogakco.domain.dto.user.UserSignupRequestDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static User of(UserSignupRequestDto signupRequestDto) {
        return new User(signupRequestDto.getEmail(), signupRequestDto.getPassword());
    }
}
