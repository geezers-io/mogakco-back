package io.geezers.mogakco.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.geezers.mogakco.api.common.Endpoint;
import io.geezers.mogakco.api.user.UserApiController;
import io.geezers.mogakco.domain.dto.user.UserLoginRequestDto;
import io.geezers.mogakco.domain.dto.user.UserSignupRequestDto;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
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
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation)
                               .operationPreprocessors()
                               .withRequestDefaults(prettyPrint(), removeHeaders("Host"))
                               .withResponseDefaults(prettyPrint()))
                .apply(springSecurity())
                .build();
    }

    @DisplayName("?????? ?????? ?????? ??????")
    @Test
    void testAuthenticationVerifyingSuccess() throws Exception {
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockMvc
                .perform(get(Endpoint.Api.AUTH).session(mockHttpSession))
                .andExpect(status().isOk());
    }

    @DisplayName("?????? ?????? ?????? ??????")
    @Test
    void testAuthenticationVerifyingFailed() throws Exception {
        mockMvc
                .perform(get(Endpoint.Api.AUTH))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("???????????? ??????")
    @Test
    void testSignupIsSuccess() throws Exception {
        UserSignupRequestDto signupRequestDto = getUserSignupRequestDto(getValidEmail(), getValidPassword());

        mockMvc
                .perform(post(Endpoint.Api.USER)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(objectMapper.writeValueAsString(signupRequestDto)))
                .andExpect(status().isCreated())
                .andDo(document("signup", requestFields(
                        attributes(key("title").value("Data Params"), key("url").value(Endpoint.Api.USER),
                                   key("method").value("POST")), fieldWithPath("email")
                                .description("")
                                .attributes(key("required").value("O"))
                                .attributes(key("constraints").value("@Email ????????? ???????????? ???")), fieldWithPath("password")
                                .description("")
                                .attributes(key("required").value("O"))
                                .attributes(key("constraints").value("Null ??????, ??? ????????? ??????")))));
    }

    private UserSignupRequestDto getUserSignupRequestDto(String email, String password) {
        return UserSignupRequestDto
                .builder()
                .email(email)
                .password(password)
                .build();
    }

    private String getValidPassword() {
        return "password";
    }

    private String getValidEmail() {
        return "adam@gmail.com";
    }

    @DisplayName("????????? ?????? ????????? ?????? ???????????? ??????")
    @Test
    void testSignupIsFailedByEmailDuplicate() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        UserSignupRequestDto signupRequestDto = getUserSignupRequestDto(getValidEmail(), getValidPassword());
        userApiController.signup(signupRequestDto, request);

        mockMvc
                .perform(post(Endpoint.Api.USER)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(objectMapper.writeValueAsString(signupRequestDto)))
                .andExpect(status().isConflict());
    }

    @DisplayName("????????? ?????? ????????? ?????? ???????????? ??????")
    @ParameterizedTest
    @ValueSource(strings = {"adam", "123adam", "adam123"})
    void testSignupIsFailedByEmailFormat(String invalidEmail) throws Exception {
        UserSignupRequestDto signupRequestDto = getUserSignupRequestDto(invalidEmail, getValidPassword());

        mockMvc
                .perform(post(Endpoint.Api.USER)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(objectMapper.writeValueAsString(signupRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("????????? ????????? ????????? ?????? ???????????? ??????")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void testSignupIsFailedByEmptyEmail(String emptyEmail) throws Exception {
        UserSignupRequestDto signupRequestDto = getUserSignupRequestDto(emptyEmail, getValidPassword());

        mockMvc
                .perform(post(Endpoint.Api.USER)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(objectMapper.writeValueAsString(signupRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("???????????? ????????? ????????? ?????? ???????????? ??????")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void testSignupIsFailedByEmptyPassword(String emptyPassword) throws Exception {
        UserSignupRequestDto signupRequestDto = getUserSignupRequestDto(getValidEmail(), emptyPassword);

        mockMvc
                .perform(post(Endpoint.Api.USER)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(objectMapper.writeValueAsString(signupRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("????????? ??????")
    @Test
    void testLoginIsSuccess() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        // ????????? ?????? ????????????
        UserSignupRequestDto signupRequestDto = getUserSignupRequestDto(getValidEmail(), getValidPassword());
        userApiController.signup(signupRequestDto, request);

        UserLoginRequestDto loginRequestDto = getLoginRequestDto(getValidEmail(), getValidPassword());

        mockMvc
                .perform(post(Endpoint.Api.LOGIN)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk())
                .andDo(document("login", requestFields(
                        attributes(key("title").value("Data Params"), key("url").value(Endpoint.Api.LOGIN),
                                   key("method").value("POST")), fieldWithPath("email")
                                .description("")
                                .attributes(key("required").value("O"))
                                .attributes(key("constraints").value("@Email ????????? ???????????? ???")), fieldWithPath("password")
                                .description("")
                                .attributes(key("required").value("O"))
                                .attributes(key("constraints").value("Null ??????, ??? ????????? ??????")))));
    }

    private UserLoginRequestDto getLoginRequestDto(String email, String password) {
        return UserLoginRequestDto
                .builder()
                .email(email)
                .password(password)
                .build();
    }

    @DisplayName("???????????? ?????? ?????? ???????????? ????????? ????????? ?????? ????????? ??????")
    @Test
    void testLoginIsFailedByNotExistEmail() throws Exception {
        UserLoginRequestDto loginRequestDto = getLoginRequestDto(getNotExistEmail(), getValidPassword());

        mockMvc
                .perform(post(Endpoint.Api.LOGIN)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isUnauthorized());
    }

    private String getNotExistEmail() {
        return "iamnotsignup@gmail.com";
    }

    @DisplayName("????????? ?????? ????????? ?????? ????????? ??????")
    @ParameterizedTest
    @ValueSource(strings = {"adam", "123adam", "adam123"})
    void testLoginIsFailedByEmailFormat(String invalidEmail) throws Exception {
        UserLoginRequestDto loginRequestDto = getLoginRequestDto(invalidEmail, getValidPassword());

        mockMvc
                .perform(post(Endpoint.Api.LOGIN)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("????????? ????????? ????????? ?????? ????????? ??????")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void testLoginIsFailedByEmptyEmail(String emptyEmail) throws Exception {
        UserLoginRequestDto loginRequestDto = getLoginRequestDto(emptyEmail, getValidPassword());

        mockMvc
                .perform(post(Endpoint.Api.LOGIN)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("???????????? ????????? ????????? ?????? ????????? ??????")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void testLoginIsFailedByEmptyPassword(String emptyPassword) throws Exception {
        UserLoginRequestDto loginRequestDto = getLoginRequestDto(getValidEmail(), emptyPassword);

        mockMvc
                .perform(post(Endpoint.Api.LOGIN)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("???????????? ??????")
    @WithMockUser
    @Test
    void testLogoutIsSuccess() throws Exception {
        mockMvc
                .perform(post(Endpoint.Api.LOGOUT))
                .andExpect(status().isOk())
                .andDo(document("logout"));
    }
}