package io.github.fbsarracini.javadesign.invite;

import io.github.fbsarracini.javadesign.user.User;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
public class RevokeInviteController {

    private final RevokeInvite revokeInvite;

    public RevokeInviteController(RevokeInvite revokeInvite) {
        this.revokeInvite = revokeInvite;
    }

    @PostMapping("/api/invites/{id}/revoke")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void execute(@PathVariable @Positive Long id, @AuthenticationPrincipal User loggedUser) {
        revokeInvite.execute(id, loggedUser);
    }
}
