package io.github.fbsarracini.javadesign.user;

public record NewUserResponse(Long id) {

    public static NewUserResponse of(User user) {
        return new NewUserResponse(user.getId());
    }
}
