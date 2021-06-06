# OTP Generation And Validation Using Spring Boot
#### Proof Of Concept showing OTP generation and validation technique using Java Spring-boot and LoadingCache Bean from google guava library to handle user's forgot-password flow

[Running Application](https://spring-boot-otp-validator.herokuapp.com/swagger-ui.html)
---

```
@Bean
public LoadingCache<UUID, Integer> loadingCache() {
 return CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES)
		.build(new CacheLoader<>() {
			public Integer load(UUID key) {
				return 0;
			}
	});
}
```
The Above bean is used to store the generated OTP corresponding to the user's unique id (UUID)

The expiration minute(s) for OTP can be set in the application.properties file after which the OTP will be automatically removed from the cache

```
com.hardik.hadraniel.otp.expiration-minutes=5
```

In a real implementation, the generated OTP will be sent to the user's entered email-id/phone number rather than a response to an API

All the logic can be found at UserService.class

---
## Local Setup

* Install Java 15
* Install Maven

Recommended way is to use [sdkman](https://sdkman.io/) for installing both maven and java

Run the below commands in the core

```
mvn clean install
```

```
mvn spring-boot:run
```

server port is configured to 8090 and expiration minute(s) for OTP is configured to 5 minutes (can be changed in application.properties file)

Go to the below url to view swagger-ui (API docs)

```
http://localhost:8090/swagger-ui.html
```
