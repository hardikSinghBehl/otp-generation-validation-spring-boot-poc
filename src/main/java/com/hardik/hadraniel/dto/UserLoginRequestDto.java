package com.hardik.hadraniel.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JacksonStdImpl
public class UserLoginRequestDto {

	@NotBlank(message = "Email-id must not be empty")
	@Email(message = "Email-id must be of valid format")
	private final String emailId;

	@NotBlank(message = "Password must not be empty")
	private final String password;

}
