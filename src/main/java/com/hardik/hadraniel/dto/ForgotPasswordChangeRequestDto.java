package com.hardik.hadraniel.dto;

import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JacksonStdImpl
public class ForgotPasswordChangeRequestDto {

	private final String emailId;
	private final Integer otp;
	private final String newPassword;

}
