package io.github.fbsarracini.javadesign.account;

import io.github.fbsarracini.javadesign.user.User;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
public class ListAccountMembersController {

    private final ListAccountMembers listAccountMembers;

    public ListAccountMembersController(ListAccountMembers listAccountMembers) {
        this.listAccountMembers = listAccountMembers;
    }

    @GetMapping("/api/accounts/{accountId}/members")
    @ResponseStatus(HttpStatus.OK)
    public List<MemberSummaryResponse> execute(@PathVariable @Positive Long accountId,
                                               @AuthenticationPrincipal User loggedUser) {
        return listAccountMembers.execute(accountId, loggedUser);
    }
}
