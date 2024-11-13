package org.security_study.global.security.jwt.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JwtExceptionResponse {
    private String timestamp;
    private int status;
    private String error;
    private String message;
}
