package com.hardik.hadraniel.exception.handler;

import java.time.LocalDateTime;

import org.json.JSONObject;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.hardik.hadraniel.constant.ApiConstants;
import com.hardik.hadraniel.exception.InvalidUserIdException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class InvalidUserIdExceptionHandler {

	@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
	@ResponseBody
	@ExceptionHandler(InvalidUserIdException.class)
	public ResponseEntity<?> invalidUserIdExceptionHandler(InvalidUserIdException exception) {

		final var response = new JSONObject();
		response.put(ApiConstants.MESSAGE, ApiConstants.INVALID_USER_ID);
		response.put(ApiConstants.TIMESTAMP, LocalDateTime.now().toString());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response.toString());
	}

}
