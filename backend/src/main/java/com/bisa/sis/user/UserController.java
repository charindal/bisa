package com.bisa.sis.user;

import com.bisa.sis.security.SecurityUser;
import com.bisa.sis.user.dto.CreateUserRequest;
import com.bisa.sis.user.dto.UpdateRoleRequest;
import com.bisa.sis.user.dto.UpdateStatusRequest;
import com.bisa.sis.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * User account management. Restricted to SUPER_ADMIN: this manages who can log in and
 * with what role, which is distinct from ADMIN's day-to-day academic-record access.
 */
@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Page<UserResponse> list(Pageable pageable) {
        return userService.list(pageable).map(UserResponse::from);
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable UUID id) {
        return UserResponse.from(userService.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@AuthenticationPrincipal SecurityUser actor,
                                @Valid @RequestBody CreateUserRequest request) {
        User created = userService.create(actor.getId(), request.email(), request.password(),
                request.firstName(), request.lastName(), request.role());
        return UserResponse.from(created);
    }

    @PatchMapping("/{id}/role")
    public UserResponse updateRole(@AuthenticationPrincipal SecurityUser actor,
                                    @PathVariable UUID id,
                                    @Valid @RequestBody UpdateRoleRequest request) {
        return UserResponse.from(userService.updateRole(actor.getId(), id, request.role()));
    }

    @PatchMapping("/{id}/status")
    public UserResponse updateStatus(@AuthenticationPrincipal SecurityUser actor,
                                      @PathVariable UUID id,
                                      @Valid @RequestBody UpdateStatusRequest request) {
        return UserResponse.from(userService.updateEnabled(actor.getId(), id, request.enabled()));
    }
}
