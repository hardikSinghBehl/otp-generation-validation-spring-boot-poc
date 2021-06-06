package com.hardik.hadraniel.controller;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hardik.hadraniel.constant.ApiConstants;
import com.hardik.hadraniel.dto.ForgotPasswordChangeRequestDto;
import com.hardik.hadraniel.dto.UserLoginRequestDto;
import com.hardik.hadraniel.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping(value = ApiConstants.AUTHENTICATION)
@AllArgsConstructor
public class AuthenticationController {

	private final UserService userService;

	@PostMapping(value = ApiConstants.LOGIN)
	@ResponseStatus(value = HttpStatus.OK)
	@Operation(summary = ApiConstants.LOGIN_SUMMARY)
	public UUID userLoginHandler(@Valid @RequestBody(required = true) final UserLoginRequestDto userLoginRequest) {
		return userService.login(userLoginRequest);
	}

	@GetMapping(value = ApiConstants.FORGOT_PASSWORD + ApiConstants.EMAIL_ID_PATH_VARIABLE)
	@ResponseStatus(value = HttpStatus.OK)
	@Operation(summary = ApiConstants.FORGOT_PASSWORD_SUMMARY)
	public Integer forgotPasswordHandler(@PathVariable(name = "emailId", required = true) final String emailId)
			throws ExecutionException {
		return userService.generateOtp(emailId);
	}

	@PutMapping(value = ApiConstants.CHANGE_PASSWORD)
	@ResponseStatus(value = HttpStatus.OK)
	@Operation(summary = ApiConstants.CHANGE_PASSWORD_SUMMARY)
	public ResponseEntity<?> boom(
			@Valid @RequestBody(required = true) final ForgotPasswordChangeRequestDto forgotPasswordChangeRequest)
			throws ExecutionException {
		return userService.changePassword(forgotPasswordChangeRequest);
	}

}
