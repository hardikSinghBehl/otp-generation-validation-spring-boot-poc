package com.hardik.hadraniel.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hardik.hadraniel.constant.ApiConstants;
import com.hardik.hadraniel.dto.UserCreationRequestDto;
import com.hardik.hadraniel.dto.UserDto;
import com.hardik.hadraniel.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping(value = ApiConstants.USER_BASE_PATH)
@AllArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	@Operation(summary = ApiConstants.USER_ACCOUNT_CREATION_SUMMARY)
	public ResponseEntity<?> useAccountCreationHandler(
			@RequestBody(required = true) final UserCreationRequestDto userCreationRequest) {
		return userService.createUser(userCreationRequest);
	}

	@GetMapping(value = ApiConstants.USER_ID_PATH_VARIABLE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	@Operation(summary = ApiConstants.USER_DETAILS_RETREIVAL_SUMMARY)
	public ResponseEntity<UserDto> userDetailsRetreivalHandler(
			@PathVariable(name = "userId", required = true) final UUID userId) {
		return ResponseEntity.ok(userService.retreiveDetails(userId));
	}

}
