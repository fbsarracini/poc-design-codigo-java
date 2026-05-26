package io.github.fbsarracini.javadesign.account;

import io.github.fbsarracini.javadesign.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class NewAccountController {

    private final CreateNewAccount createNewAccount;

    public NewAccountController(CreateNewAccount createNewAccount) {
        this.createNewAccount = createNewAccount;
    }

    @PostMapping("/api/accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public NewAccountResponse execute(@RequestBody @Valid NewAccountRequest request,
                                      @AuthenticationPrincipal User loggedUser) {
        Account account = createNewAccount.execute(loggedUser, request);
        return NewAccountResponse.of(account);
    }
}
