package io.github.fbsarracini.javadesign.auth;

public record LoginResponse(String token) {

    public static LoginResponse of(String token) {
        return new LoginResponse(token);
    }
}
