package io.geezers.mogakco.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.geezers.mogakco.api.common.Endpoint;
import io.geezers.mogakco.api.user.UserApiController;
import io.geezers.mogakco.domain.dto.user.UserLoginRequestDto;
import io.geezers.mogakco.domain.dto.user.UserSignupRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@Transactional
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest
class UserApiControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private UserApiController userApiController;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint(), removeHeaders("Host"))
                        .withResponseDefaults(prettyPrint()))
                .apply(springSecurity()).build();
    }

    @DisplayName("인증 상태 검증 성공")
    @WithMockUser
    @Test
    void testAuthenticationVerifyingSuccess() throws Exception {
        mockMvc.perform(get(Endpoint.Api.AUTH))
                .andExpect(status().isOk());
    }

    @DisplayName("인증 상태 검증 실패")
    @Test
    void testAuthenticationVerifyingFailed() throws Exception {
        mockMvc.perform(get(Endpoint.Api.AUTH))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("회원가입 성공")
    @Test
    void testSignupIsSuccess() throws Exception {
        UserSignupRequestDto signupRequestDto = getUserSignupRequestDto(getValidEmail(), getValidPassword());

        mockMvc.perform(post(Endpoint.Api.USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequestDto)))
                .andExpect(status().isCreated())
                .andDo(document("signup",
                        requestFields(
                                attributes(
                                        key("title").value("Data Params"), key("url").value(Endpoint.Api.USER),
                                        key("method").value("POST")),
                                fieldWithPath("email").description("").
                                        attributes(key("required").value("O"))
                                        .attributes(key("constraints").value("@Email 형식을 맞추어야 함")),
                                fieldWithPath("password").description("")
                                        .attributes(key("required").value("O"))
                                        .attributes(key("constraints").value("Null 불가, 빈 문자열 불가")))));
    }

    private UserSignupRequestDto getUserSignupRequestDto(String email, String password) {
        return UserSignupRequestDto.builder().email(email).password(password).build();
    }

    private String getValidPassword() {
        return "password";
    }

    private String getValidEmail() {
        return "adam@gmail.com";
    }

    @DisplayName("이메일 중복 오류로 인한 회원가입 실패")
    @Test
    void testSignupIsFailedByEmailDuplicate() throws Exception {
        UserSignupRequestDto signupRequestDto = getUserSignupRequestDto(getValidEmail(), getValidPassword());
        userApiController.signup(signupRequestDto);

        mockMvc.perform(post(Endpoint.Api.USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequestDto)))
                .andExpect(status().isConflict());
    }

    @DisplayName("이메일 형식 오류로 인한 회원가입 실패")
    @ParameterizedTest
    @ValueSource(strings = {"adam", "123adam", "adam123"})
    void testSignupIsFailedByEmailFormat(String invalidEmail) throws Exception {
        UserSignupRequestDto signupRequestDto = getUserSignupRequestDto(invalidEmail, getValidPassword());

        mockMvc.perform(post(Endpoint.Api.USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("이메일 미입력 오류로 인한 회원가입 실패")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void testSignupIsFailedByEmptyEmail(String emptyEmail) throws Exception {
        UserSignupRequestDto signupRequestDto = getUserSignupRequestDto(emptyEmail, getValidPassword());

        mockMvc.perform(post(Endpoint.Api.USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("비밀번호 미입력 오류로 인한 회원가입 실패")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void testSignupIsFailedByEmptyPassword(String emptyPassword) throws Exception {
        UserSignupRequestDto signupRequestDto = getUserSignupRequestDto(getValidEmail(), emptyPassword);

        mockMvc.perform(post(Endpoint.Api.USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("로그인 성공")
    @Test
    void testLoginIsSuccess() throws Exception {
        // 테스트 유저 회원가입
        UserSignupRequestDto signupRequestDto = getUserSignupRequestDto(getValidEmail(), getValidPassword());
        userApiController.signup(signupRequestDto);

        UserLoginRequestDto loginRequestDto = getLoginRequestDto(getValidEmail(), getValidPassword());

        mockMvc.perform(post(Endpoint.Api.LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk())
                .andDo(document("login",
                        requestFields(
                                attributes(
                                        key("title").value("Data Params"), key("url").value(Endpoint.Api.LOGIN),
                                        key("method").value("POST")),
                                fieldWithPath("email").description("").
                                        attributes(key("required").value("O"))
                                        .attributes(key("constraints").value("@Email 형식을 맞추어야 함")),
                                fieldWithPath("password").description("")
                                        .attributes(key("required").value("O"))
                                        .attributes(key("constraints").value("Null 불가, 빈 문자열 불가")))));
    }

    private UserLoginRequestDto getLoginRequestDto(String email, String password) {
        return UserLoginRequestDto.builder().email(email).password(password).build();
    }

    @DisplayName("회원가입 하지 않은 이메일로 로그인 시도로 인한 로그인 실패")
    @Test
    void testLoginIsFailedByNotExistEmail() throws Exception {
        UserLoginRequestDto loginRequestDto = getLoginRequestDto(getNotExistEmail(), getValidPassword());

        mockMvc.perform(post(Endpoint.Api.LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isUnauthorized());
    }

    private String getNotExistEmail() {
        return "iamnotsignup@gmail.com";
    }

    @DisplayName("이메일 형식 오류로 인한 로그인 실패")
    @ParameterizedTest
    @ValueSource(strings = {"adam", "123adam", "adam123"})
    void testLoginIsFailedByEmailFormat(String invalidEmail) throws Exception {
        UserLoginRequestDto loginRequestDto = getLoginRequestDto(invalidEmail, getValidPassword());

        mockMvc.perform(post(Endpoint.Api.LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("이메일 미입력 오류로 인한 로그인 실패")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void testLoginIsFailedByEmptyEmail(String emptyEmail) throws Exception {
        UserLoginRequestDto loginRequestDto = getLoginRequestDto(emptyEmail, getValidPassword());

        mockMvc.perform(post(Endpoint.Api.LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("비밀번호 미입력 오류로 인한 로그인 실패")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void testLoginIsFailedByEmptyPassword(String emptyPassword) throws Exception {
        UserLoginRequestDto loginRequestDto = getLoginRequestDto(getValidEmail(), emptyPassword);

        mockMvc.perform(post(Endpoint.Api.LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("로그아웃 성공")
    @WithMockUser
    @Test
    void testLogoutIsSuccess() throws Exception {
        mockMvc.perform(post(Endpoint.Api.LOGOUT))
                .andExpect(status().isOk())
                .andDo(document("logout"));
    }
}