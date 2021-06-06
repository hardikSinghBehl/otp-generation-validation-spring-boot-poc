package com.hardik.hadraniel.bean;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hardik.hadraniel.configuration.properties.OneTimePasswordConfigurationProperties;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
@EnableConfigurationProperties(OneTimePasswordConfigurationProperties.class)
public class OtpCacheBean {

	private final OneTimePasswordConfigurationProperties oneTimePasswordConfigurationProperties;

	@Bean
	public LoadingCache<UUID, Integer> loadingCache() {
		final var expirationMinutes = oneTimePasswordConfigurationProperties.getOtp().getExpirationMinutes();
		return CacheBuilder.newBuilder().expireAfterWrite(expirationMinutes, TimeUnit.MINUTES)
				.build(new CacheLoader<>() {
					public Integer load(UUID key) {
						return 0;
					}
				});
	}

}
