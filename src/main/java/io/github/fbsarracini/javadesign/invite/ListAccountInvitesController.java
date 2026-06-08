package io.github.fbsarracini.javadesign.invite;

import io.github.fbsarracini.javadesign.user.User;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
public class ListAccountInvitesController {

    private final ListAccountInvites listAccountInvites;

    public ListAccountInvitesController(ListAccountInvites listAccountInvites) {
        this.listAccountInvites = listAccountInvites;
    }

    @GetMapping("/api/accounts/{accountId}/invites")
    @ResponseStatus(HttpStatus.OK)
    public List<InviteSummaryResponse> execute(@PathVariable @Positive Long accountId,
                                               @AuthenticationPrincipal User loggedUser) {
        return listAccountInvites.execute(accountId, loggedUser);
    }
}
