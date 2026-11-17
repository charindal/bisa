package com.bisa.sis.user;

import com.bisa.sis.security.SecurityUser;
import com.bisa.sis.user.dto.UserResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/me")
public class MeController {

    @GetMapping
    public UserResponse me(@AuthenticationPrincipal SecurityUser actor) {
        return UserResponse.from(actor.getUser());
    }
}
