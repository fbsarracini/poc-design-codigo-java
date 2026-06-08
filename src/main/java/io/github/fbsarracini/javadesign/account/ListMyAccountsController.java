package io.github.fbsarracini.javadesign.account;

import io.github.fbsarracini.javadesign.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ListMyAccountsController {

    private final ListMyAccounts listMyAccounts;

    public ListMyAccountsController(ListMyAccounts listMyAccounts) {
        this.listMyAccounts = listMyAccounts;
    }

    @GetMapping("/api/accounts")
    @ResponseStatus(HttpStatus.OK)
    public List<AccountSummaryResponse> execute(@AuthenticationPrincipal User loggedUser) {
        return listMyAccounts.execute(loggedUser);
    }
}
