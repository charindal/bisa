package com.bisa.sis.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Creates the first SUPER_ADMIN account on startup if one doesn't exist yet, from
 * SUPER_ADMIN_EMAIL / SUPER_ADMIN_PASSWORD. This is the only way to get a working
 * login on a brand-new environment, since there is no public registration endpoint.
 */
@Component
@EnableConfigurationProperties(SuperAdminBootstrapRunner.BootstrapProperties.class)
public class SuperAdminBootstrapRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SuperAdminBootstrapRunner.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BootstrapProperties properties;

    public SuperAdminBootstrapRunner(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                      BootstrapProperties properties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) {
        boolean anySuperAdminExists = userRepository.findAll().stream()
                .anyMatch(u -> u.getRole() == Role.SUPER_ADMIN);
        if (anySuperAdminExists) {
            return;
        }

        String email = properties.getSuperAdminEmail();
        String password = properties.getSuperAdminPassword();
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            log.warn("No SUPER_ADMIN account exists and SUPER_ADMIN_EMAIL/SUPER_ADMIN_PASSWORD are not set. " +
                    "Set them and restart to bootstrap the first admin account.");
            return;
        }

        User admin = new User(email, passwordEncoder.encode(password), "Super", "Admin", Role.SUPER_ADMIN);
        userRepository.save(admin);
        log.info("Bootstrapped initial SUPER_ADMIN account: {}", email);
    }

    @ConfigurationProperties(prefix = "app.bootstrap")
    public static class BootstrapProperties {
        private String superAdminEmail;
        private String superAdminPassword;

        public String getSuperAdminEmail() {
            return superAdminEmail;
        }

        public void setSuperAdminEmail(String superAdminEmail) {
            this.superAdminEmail = superAdminEmail;
        }

        public String getSuperAdminPassword() {
            return superAdminPassword;
        }

        public void setSuperAdminPassword(String superAdminPassword) {
            this.superAdminPassword = superAdminPassword;
        }
    }
}
