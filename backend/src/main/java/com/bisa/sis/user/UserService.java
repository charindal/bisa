package com.bisa.sis.user;

import com.bisa.sis.audit.AuditService;
import com.bisa.sis.common.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuditService auditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    public Page<User> list(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No user with id " + id));
    }

    @Transactional
    public User create(UUID actorId, String email, String rawPassword, String firstName, String lastName, Role role) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("A user with this email already exists");
        }
        User user = new User(email, passwordEncoder.encode(rawPassword), firstName, lastName, role);
        userRepository.save(user);
        auditService.record(actorId, "USER_CREATED", "User", user.getId().toString(), "role=" + role);
        return user;
    }

    @Transactional
    public User updateRole(UUID actorId, UUID targetId, Role newRole) {
        if (actorId.equals(targetId) && newRole != Role.SUPER_ADMIN) {
            throw new IllegalArgumentException("You cannot change your own role away from Super Admin");
        }
        User user = getById(targetId);
        Role previousRole = user.getRole();
        user.setRole(newRole);
        auditService.record(actorId, "USER_ROLE_CHANGED", "User", targetId.toString(),
                previousRole + " -> " + newRole);
        return user;
    }

    @Transactional
    public User updateEnabled(UUID actorId, UUID targetId, boolean enabled) {
        if (actorId.equals(targetId) && !enabled) {
            throw new IllegalArgumentException("You cannot deactivate your own account");
        }
        User user = getById(targetId);
        user.setEnabled(enabled);
        auditService.record(actorId, enabled ? "USER_ACTIVATED" : "USER_DEACTIVATED",
                "User", targetId.toString(), null);
        return user;
    }
}
