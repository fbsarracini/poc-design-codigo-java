package io.github.fbsarracini.javadesign.user;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NewUserController {

    private final CreateNewUser createNewUser;

    public NewUserController(CreateNewUser createNewUser) {
        this.createNewUser = createNewUser;
    }

    @PostMapping("/api/users")
    @ResponseStatus(HttpStatus.CREATED)
    public NewUserResponse execute(@RequestBody @Valid NewUserRequest newUserData) {
        User user = createNewUser.execute(newUserData);
        return NewUserResponse.of(user);
    }
}
