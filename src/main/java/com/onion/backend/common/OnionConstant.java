package com.onion.backend.common;

import java.time.Duration;

public class OnionConstant {

    public static final String TOKEN_BEARER = "Bearer ";
    public static final String AUTHENTICATION_HEADER = "Authorization";

    public static final String CACHE_KEY_SEPARATOR = "::";
    public static final String AD_CACHE_KEY_PREFIX = "ad";
    public static final Duration AD_CACHE_TTL = Duration.ofDays(1);
}
