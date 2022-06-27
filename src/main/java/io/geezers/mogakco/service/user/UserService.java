package io.geezers.mogakco.service.user;

import io.geezers.mogakco.domain.dto.user.UserSignupRequestDto;
import io.geezers.mogakco.domain.entity.User;
import io.geezers.mogakco.exception.AlreadyUserExistingException;
import io.geezers.mogakco.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public User signup(UserSignupRequestDto signupRequestDto) {
        existUser(signupRequestDto);

        signupRequestDto.setPassword(passwordEncoder.encode(signupRequestDto.getPassword()));
        User newUser = User.of(signupRequestDto);

        return userRepository.save(newUser);
    }

    private void existUser(UserSignupRequestDto signupRequestDto) {
        User user = userRepository.findByEmail(signupRequestDto.getEmail());
        if (user != null) {
            throw new AlreadyUserExistingException("This email is already exist.");
        }
    }
}
