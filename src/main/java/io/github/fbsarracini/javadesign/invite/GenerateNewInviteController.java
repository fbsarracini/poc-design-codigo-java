package io.github.fbsarracini.javadesign.invite;

import io.github.fbsarracini.javadesign.user.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
public class GenerateNewInviteController {

    private final GenerateNewInvite generateNewInvite;

    public GenerateNewInviteController(GenerateNewInvite generateNewInvite) {
        this.generateNewInvite = generateNewInvite;
    }

    @PostMapping("/api/accounts/{accountId}/invites")
    @ResponseStatus(HttpStatus.OK)
    public void execute(@PathVariable @Positive Long accountId,
                        @RequestBody @Valid NewInviteRequest request,
                        @AuthenticationPrincipal User loggedUser) {

        generateNewInvite.execute(accountId, loggedUser, request);
    }
}
