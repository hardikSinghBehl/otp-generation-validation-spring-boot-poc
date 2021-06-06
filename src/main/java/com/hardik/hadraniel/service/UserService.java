package com.hardik.hadraniel.service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.common.cache.LoadingCache;
import com.hardik.hadraniel.constant.ApiConstants;
import com.hardik.hadraniel.dto.ForgotPasswordChangeRequestDto;
import com.hardik.hadraniel.dto.UserCreationRequestDto;
import com.hardik.hadraniel.dto.UserDto;
import com.hardik.hadraniel.dto.UserLoginRequestDto;
import com.hardik.hadraniel.entity.User;
import com.hardik.hadraniel.exception.DuplicateEmailIdException;
import com.hardik.hadraniel.exception.InvalidPasswordException;
import com.hardik.hadraniel.exception.InvalidUserIdException;
import com.hardik.hadraniel.exception.OneTimePasswordValidationFailureException;
import com.hardik.hadraniel.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {

	private final LoadingCache<UUID, Integer> oneTimePasswordCache;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	private boolean exists(final String emailId) {
		return userRepository.existsByEmailId(emailId);
	}

	public ResponseEntity<?> createUser(final UserCreationRequestDto userCreationRequest) {
		final var user = new User();
		final var response = new JSONObject();

		if (exists(userCreationRequest.getEmailId()))
			throw new DuplicateEmailIdException();

		user.setEmailId(userCreationRequest.getEmailId());
		user.setFullName(userCreationRequest.getFullName());
		user.setPassword(passwordEncoder.encode(userCreationRequest.getPassword()));

		userRepository.save(user);

		response.put(ApiConstants.MESSAGE, ApiConstants.ACCOUNT_CREATION_SUCCESS);
		response.put(ApiConstants.TIMESTAMP, LocalDateTime.now().toString());
		return ResponseEntity.ok(response.toString());
	}

	public UUID login(final UserLoginRequestDto userLoginRequest) {
		final var user = userRepository.findByEmailId(userLoginRequest.getEmailId())
				.orElseThrow(() -> new InvalidUserIdException());

		if (passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword()))
			return user.getId();
		throw new InvalidPasswordException();
	}

	public UserDto retreiveDetails(final UUID id) {
		final var user = userRepository.findById(id).orElseThrow(() -> new InvalidUserIdException());

		return UserDto.builder().createdAt(user.getCreatedAt()).emailId(user.getEmailId()).fullName(user.getFullName())
				.id(user.getId()).build();
	}

	public Integer generateOtp(final String emailId) throws ExecutionException {
		final var user = userRepository.findByEmailId(emailId).orElseThrow(() -> new InvalidUserIdException());

		if (oneTimePasswordCache.get(user.getId()) != null)
			oneTimePasswordCache.invalidate(user.getId());

		final var otp = new Random().ints(1, 100000, 999999).sum();
		oneTimePasswordCache.put(user.getId(), otp);

		return otp;
	}

	private boolean validateOtp(final User user, final Integer otp) throws ExecutionException {
		return oneTimePasswordCache.get(user.getId()).equals(otp);
	}

	public ResponseEntity<?> changePassword(final ForgotPasswordChangeRequestDto forgotPasswordChangeRequest)
			throws ExecutionException {
		final var user = userRepository.findByEmailId(forgotPasswordChangeRequest.getEmailId())
				.orElseThrow(() -> new InvalidUserIdException());
		final var response = new JSONObject();

		if (validateOtp(user, forgotPasswordChangeRequest.getOtp())) {
			user.setPassword(passwordEncoder.encode(forgotPasswordChangeRequest.getNewPassword()));
			userRepository.save(user);

			oneTimePasswordCache.invalidate(user.getId());

			response.put(ApiConstants.MESSAGE, ApiConstants.PASSWORD_CHANGE_SUCCESS);
			response.put(ApiConstants.TIMESTAMP, LocalDateTime.now().toString());
			return ResponseEntity.ok(response.toString());
		} else
			throw new OneTimePasswordValidationFailureException();
	}

}
