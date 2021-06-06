package unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.common.cache.LoadingCache;
import com.hardik.hadraniel.constant.ApiConstants;
import com.hardik.hadraniel.dto.ForgotPasswordChangeRequestDto;
import com.hardik.hadraniel.dto.UserCreationRequestDto;
import com.hardik.hadraniel.dto.UserLoginRequestDto;
import com.hardik.hadraniel.entity.User;
import com.hardik.hadraniel.exception.DuplicateEmailIdException;
import com.hardik.hadraniel.exception.InvalidPasswordException;
import com.hardik.hadraniel.exception.InvalidUserIdException;
import com.hardik.hadraniel.exception.OneTimePasswordValidationFailureException;
import com.hardik.hadraniel.repository.UserRepository;
import com.hardik.hadraniel.service.UserService;

public class UserServiceTest {

	private static final UUID USER_ID = UUID.fromString("caaa7b82-5656-48ba-a953-a08201598a3c");
	private static final String EMAIL_ID = "demo@gmail.com";
	private static final String FULL_NAME = "Demo";
	private static final String PASSWORD = "Demo123";
	private static final String ENCRPYTED_PASSWORD = PASSWORD + "ENCRPYTED";

	private UserService userService;
	private LoadingCache<UUID, Integer> oneTimePasswordCache;
	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void setUp() {
		this.oneTimePasswordCache = mock(LoadingCache.class);
		this.userRepository = mock(UserRepository.class);
		this.passwordEncoder = mock(PasswordEncoder.class);
		this.userService = new UserService(oneTimePasswordCache, userRepository, passwordEncoder);
	}

	@Test
	@DisplayName("User Creation: Success")
	void createUser_success() {
		// PREPARE
		final var userCreationRequest = mock(UserCreationRequestDto.class);
		when(userCreationRequest.getEmailId()).thenReturn(EMAIL_ID);
		when(userCreationRequest.getFullName()).thenReturn(FULL_NAME);
		when(userCreationRequest.getPassword()).thenReturn(PASSWORD);
		when(userRepository.existsByEmailId(EMAIL_ID)).thenReturn(false);
		when(userRepository.save(Mockito.any(User.class))).thenReturn(mock(User.class));

		// CALL
		final var response = userService.createUser(userCreationRequest);

		// VERIFY
		assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
		assertThat(response.toString()).contains(ApiConstants.ACCOUNT_CREATION_SUCCESS);
		verify(userRepository).save(Mockito.any(User.class));
		verify(userRepository).existsByEmailId(EMAIL_ID);
		verify(userCreationRequest, times(2)).getEmailId();
		verify(userCreationRequest).getFullName();
		verify(userCreationRequest).getPassword();
	}

	@Test
	@DisplayName("User Creation: Failure Duplicate Email-Id")
	void createUser_duplicateEmailId_failure() {
		// PREPARE
		final var userCreationRequest = mock(UserCreationRequestDto.class);
		when(userCreationRequest.getEmailId()).thenReturn(EMAIL_ID);
		when(userRepository.existsByEmailId(EMAIL_ID)).thenReturn(true);

		// CALL
		assertThrows(DuplicateEmailIdException.class, () -> userService.createUser(userCreationRequest));

		// VERIFY
		verify(userRepository, times(0)).save(Mockito.any(User.class));
		verify(userRepository).existsByEmailId(EMAIL_ID);
		verify(userCreationRequest).getEmailId();
		verify(userCreationRequest, times(0)).getFullName();
		verify(userCreationRequest, times(0)).getPassword();
	}

	@Test
	@DisplayName("User Login: Successs")
	void login_success() {
		// PREPARE
		final var userLoginRequest = mock(UserLoginRequestDto.class);
		final var user = mock(User.class);
		when(user.getPassword()).thenReturn(ENCRPYTED_PASSWORD);
		when(user.getId()).thenReturn(USER_ID);
		when(userRepository.findByEmailId(EMAIL_ID)).thenReturn(Optional.of(user));
		when(userLoginRequest.getEmailId()).thenReturn(EMAIL_ID);
		when(userLoginRequest.getPassword()).thenReturn(PASSWORD);
		when(passwordEncoder.matches(PASSWORD, ENCRPYTED_PASSWORD)).thenReturn(true);

		// CALL
		final var userId = userService.login(userLoginRequest);

		// VERIFY
		assertEquals(USER_ID, userId);
		verify(userRepository).findByEmailId(EMAIL_ID);
		verify(userLoginRequest).getEmailId();
		verify(userLoginRequest).getPassword();
	}

	@Test
	@DisplayName("User Login: Failure Invalid EmailId")
	void login_failure_invalidEmailId() {
		// PREPARE
		final var userLoginRequest = mock(UserLoginRequestDto.class);
		when(userLoginRequest.getEmailId()).thenReturn(EMAIL_ID);
		when(userRepository.findByEmailId(EMAIL_ID)).thenReturn(Optional.empty());

		// CALL
		assertThrows(InvalidUserIdException.class, () -> userService.login(userLoginRequest));

		// VERIFY
		verify(userRepository).findByEmailId(EMAIL_ID);
		verify(userLoginRequest).getEmailId();
		verify(userLoginRequest, times(0)).getPassword();
	}

	@Test
	@DisplayName("User Login: Failure Wrong Password")
	void login_failure_wrongPassword() {
		// PREPARE
		final var userLoginRequest = mock(UserLoginRequestDto.class);
		final var user = mock(User.class);
		when(user.getPassword()).thenReturn(ENCRPYTED_PASSWORD);
		when(user.getId()).thenReturn(USER_ID);
		when(userRepository.findByEmailId(EMAIL_ID)).thenReturn(Optional.of(user));
		when(userLoginRequest.getEmailId()).thenReturn(EMAIL_ID);
		when(userLoginRequest.getPassword()).thenReturn(PASSWORD);
		when(passwordEncoder.matches(PASSWORD, ENCRPYTED_PASSWORD)).thenReturn(false);

		// CALL
		assertThrows(InvalidPasswordException.class, () -> userService.login(userLoginRequest));

		// VERIFY
		verify(userRepository).findByEmailId(EMAIL_ID);
		verify(userLoginRequest).getEmailId();
		verify(userLoginRequest).getPassword();
	}

	@Test
	@DisplayName("Generate OTP: Failure Invalid Email-id")
	void generateOtp_failure_invalidEmailId() {
		// PREPARE
		when(userRepository.findByEmailId(EMAIL_ID)).thenReturn(Optional.empty());

		// CALL
		assertThrows(InvalidUserIdException.class, () -> userService.generateOtp(EMAIL_ID));
	}

	@Test
	@DisplayName("Generate OTP: Success")
	void generateOtp_success() throws ExecutionException {
		// PREPARE
		final var user = mock(User.class);
		when(user.getId()).thenReturn(USER_ID);
		when(userRepository.findByEmailId(EMAIL_ID)).thenReturn(Optional.of(user));
		when(oneTimePasswordCache.get(USER_ID)).thenReturn(null);

		// CALL
		final var response = userService.generateOtp(EMAIL_ID);

		// VERIFY
		assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
		assertThat(response.getBody().toString()).contains(ApiConstants.OTP_GENERATION_SUCCESS);
		verify(oneTimePasswordCache).get(USER_ID);
		verify(oneTimePasswordCache, times(0)).invalidate(USER_ID);
		verify(oneTimePasswordCache).put(Mockito.eq(USER_ID), Mockito.anyInt());
	}

	@Test
	@DisplayName("Change Password: Success")
	void changePassword_success() throws ExecutionException {
		// PREPARE
		final var passwordChangeRequest = mock(ForgotPasswordChangeRequestDto.class);
		final var user = mock(User.class);
		when(user.getId()).thenReturn(USER_ID);
		when(passwordChangeRequest.getEmailId()).thenReturn(EMAIL_ID);
		when(passwordChangeRequest.getNewPassword()).thenReturn(PASSWORD);
		when(passwordChangeRequest.getOtp()).thenReturn(121999);
		when(userRepository.findByEmailId(EMAIL_ID)).thenReturn(Optional.of(user));
		when(oneTimePasswordCache.get(USER_ID)).thenReturn(121999);
		when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCRPYTED_PASSWORD);

		// CALL
		final var response = userService.changePassword(passwordChangeRequest);

		// VERIFY
		assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
		assertThat(response.getBody().toString()).contains(ApiConstants.PASSWORD_CHANGE_SUCCESS);
		verify(passwordChangeRequest).getEmailId();
		verify(passwordChangeRequest).getOtp();
		verify(passwordChangeRequest).getNewPassword();
		verify(user).setPassword(ENCRPYTED_PASSWORD);
		verify(passwordEncoder).encode(PASSWORD);
		verify(userRepository).save(user);
		verify(oneTimePasswordCache).invalidate(USER_ID);
	}

	@Test
	@DisplayName("Change Password: Failure Invalid OTP")
	void changePassword_failure_invalidOtp() throws ExecutionException {
		// PREPARE
		final var passwordChangeRequest = mock(ForgotPasswordChangeRequestDto.class);
		final var user = mock(User.class);
		when(user.getId()).thenReturn(USER_ID);
		when(passwordChangeRequest.getEmailId()).thenReturn(EMAIL_ID);
		when(passwordChangeRequest.getNewPassword()).thenReturn(PASSWORD);
		when(passwordChangeRequest.getOtp()).thenReturn(052001);
		when(userRepository.findByEmailId(EMAIL_ID)).thenReturn(Optional.of(user));
		when(oneTimePasswordCache.get(USER_ID)).thenReturn(121999);

		// CALL
		assertThrows(OneTimePasswordValidationFailureException.class,
				() -> userService.changePassword(passwordChangeRequest));

		// VERIFY
		verify(passwordChangeRequest).getEmailId();
		verify(passwordChangeRequest).getOtp();
		verify(passwordChangeRequest, times(0)).getNewPassword();
		verify(user, times(0)).setPassword(ENCRPYTED_PASSWORD);
		verify(passwordEncoder, times(0)).encode(PASSWORD);
		verify(userRepository, times(0)).save(user);
	}
}
