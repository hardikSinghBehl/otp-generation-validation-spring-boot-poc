package com.hardik.hadraniel.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JacksonStdImpl
public class UserDto {

	private final UUID id;
	private final String fullName;
	private final String emailId;
	private final LocalDateTime createdAt;

}
