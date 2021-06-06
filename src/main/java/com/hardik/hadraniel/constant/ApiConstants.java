package com.hardik.hadraniel.constant;

public class ApiConstants {

	public static final String MESSAGE = "message";
	public static final String TIMESTAMP = "timestamp";
	public static final String INVALID_USER_ID = "No User Exists With Provided Id/Email-id";
	public static final String OTP_VALIDATION_FAILURE = "Failed To Validate Provided OTP, Either Wrong OTP Entered Or OTP Expired";
	public static final String INVALID_PASSWORD = "The Password You Entered Was Wrong";
	public static final String ACCOUNT_CREATION_SUCCESS = "Account Created Successfully, You Can Use Your Credentials To Login";
	public static final String PASSWORD_CHANGE_SUCCESS = "Password Changed Successfully";

	public static final String USER_BASE_PATH = "/user";
	public static final String USER_ID_PATH_VARIABLE = "/{userId}";
	public static final String USER_DETAILS_RETREIVAL_SUMMARY = "Returns User Details Of User";
	public static final String USER_ACCOUNT_CREATION_SUMMARY = "Creates User Account In The System";
	public static final String AUTHENTICATION = "/authentication";
	public static final String LOGIN = "/login";
	public static final String LOGIN_SUMMARY = "Logs User Into the System And Returns User's Unique Account Id";
	public static final String FORGOT_PASSWORD = "/forgot-password";
	public static final String FORGOT_PASSWORD_SUMMARY = "Returns A Six Digit OTP That Can Be Used To Change The Password";
	public static final String CHANGE_PASSWORD = "/change-password";
	public static final String CHANGE_PASSWORD_SUMMARY = "Changes Users Password In The System If OTP is Validated";
	public static final String EMAIL_ID_PATH_VARIABLE = "/{emailId}";
	public static final String DUPLICATE_EMAIL = "Account Already Exists With Entered Email-id";
	public static final String OTP = "One Time Password";
	public static final String OTP_GENERATION_SUCCESS = "You Can Use This OTP to change your password by hitting the change-password endpoint, The generated OTP will expire in 5 minutes";

}
