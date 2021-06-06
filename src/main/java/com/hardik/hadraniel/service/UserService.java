package com.hardik.hadraniel.service;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.google.common.cache.LoadingCache;
import com.hardik.hadraniel.dto.ForgotPasswordChangeRequestDto;
import com.hardik.hadraniel.dto.UserCreationRequestDto;
import com.hardik.hadraniel.dto.UserDto;
import com.hardik.hadraniel.dto.UserLoginRequestDto;
import com.hardik.hadraniel.entity.User;
import com.hardik.hadraniel.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {

	private final LoadingCache<UUID, Integer> oneTimePasswordCache;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public void createUser(final UserCreationRequestDto userCreationRequest) {
		final var user = new User();

		user.setEmailId(userCreationRequest.getEmailId());
		user.setFullName(userCreationRequest.getFullName());
		user.setPassword(passwordEncoder.encode(userCreationRequest.getPassword()));

		userRepository.save(user);
	}

	public UUID login(final UserLoginRequestDto userLoginRequest) {
		final var user = userRepository.findByEmailId(userLoginRequest.getEmailId()).get();

		if (passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword()))
			return user.getId();
		throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
	}

	public UserDto retreiveDetails(final UUID id) {
		final var user = userRepository.findById(id).get();

		return UserDto.builder().createdAt(user.getCreatedAt()).emailId(user.getEmailId()).fullName(user.getFullName())
				.id(user.getId()).build();
	}

	public Integer generateOtp(final String emailId) throws ExecutionException {
		final var user = userRepository.findByEmailId(emailId).get();

		if (oneTimePasswordCache.get(user.getId()) != null)
			oneTimePasswordCache.invalidate(user.getId());

		final var otp = new Random().ints(1, 100000, 999999).sum();
		oneTimePasswordCache.put(user.getId(), otp);

		return otp;
	}

	private boolean validateOtp(final String emailId, final Integer otp) throws ExecutionException {
		final var user = userRepository.findByEmailId(emailId).get();
		return oneTimePasswordCache.get(user.getId()).equals(otp);
	}

	public void changePassword(final ForgotPasswordChangeRequestDto forgotPasswordChangeRequest)
			throws ExecutionException {
		if (validateOtp(forgotPasswordChangeRequest.getEmailId(), forgotPasswordChangeRequest.getOtp())) {
			final var user = userRepository.findByEmailId(forgotPasswordChangeRequest.getEmailId()).get();
			user.setPassword(passwordEncoder.encode(forgotPasswordChangeRequest.getNewPassword()));
			userRepository.save(user);
		} else
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
	}

}
