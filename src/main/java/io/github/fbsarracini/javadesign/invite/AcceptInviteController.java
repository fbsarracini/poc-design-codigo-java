package io.github.fbsarracini.javadesign.invite;

import io.github.fbsarracini.javadesign.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class AcceptInviteController {

    private final AcceptInvite acceptInvite;

    public AcceptInviteController(AcceptInvite acceptInvite) {
        this.acceptInvite = acceptInvite;
    }

    @PostMapping("/api/invites/{token}/accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void execute(@PathVariable String token, @AuthenticationPrincipal User loggedUser) {
        acceptInvite.execute(token, loggedUser);
    }
}
