package com.hardik.hadraniel.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "com.hardik.hadraniel")
public class OneTimePasswordConfigurationProperties {

	private OTP otp = new OTP();

	@Data
	public class OTP {
		private Integer expirationMinutes;
	}

}
