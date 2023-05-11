package org.nentangso.core.web.rest.errors;

import java.net.URI;

public final class NtsErrorConstants {

    public static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";
    public static final String ERR_VALIDATION = "error.validation";
    public static final String PROBLEM_BASE_URL = "https://www.jhipster.tech/problem";
    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");
    public static final URI CONSTRAINT_VIOLATION_TYPE = URI.create(PROBLEM_BASE_URL + "/constraint-violation");
    public static final URI INVALID_PASSWORD_TYPE = URI.create(PROBLEM_BASE_URL + "/invalid-password");
    public static final URI EMAIL_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/email-already-used");
    public static final URI LOGIN_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/login-already-used");
    public static final String KEY_ERRORS = "errors";
    public static final String KEY_BASE = "base";
    public static final String MESSAGE_UNAUTHORIZED = "[API] Invalid API key or access token (unrecognized login or wrong password)";
    public static final String MESSAGE_ACCESS_DENIED = "[API] This action requires merchant approval for the necessary scope.";
    public static final String MESSAGE_UNPROCESSABLE = "Required parameter missing or invalid";

    private NtsErrorConstants() {
    }
}
