package com.hardik.hadraniel.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JacksonStdImpl
public class UserCreationRequestDto {

	@NotBlank(message = "Name must not be empty")
	@Size(max = 50, message = "FullName must not exceed more than 50 characters")
	private final String fullName;

	@NotBlank(message = "Email-id must not be empty")
	@Email(message = "Email-id must be of valid format")
	@Size(max = 50, message = "Email-id must not exceed more than 50 characters")
	private final String emailId;

	@NotBlank(message = "Pasword must not be empty")
	private final String password;

}
