package com.hardik.hadraniel.dto;

import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JacksonStdImpl
public class UserLoginRequestDto {

	private final String emailId;
	private final String password;

}
