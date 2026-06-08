package io.github.fbsarracini.javadesign.user;

import org.springframework.security.crypto.password.PasswordEncoder;

public interface NewUserData {
    public String getEmail();
    public User toNewUser(PasswordEncoder passwordEncoder);

}
