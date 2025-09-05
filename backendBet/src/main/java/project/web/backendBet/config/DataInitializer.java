package project.web.backendBet.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import project.web.backendBet.model.AppUser;
import project.web.backendBet.model.Role;
import project.web.backendBet.repo.UserRepository;

@Configuration
@RequiredArgsConstructor
@Profile("dev")
public class DataInitializer {

    private final PasswordEncoder encoder;

    @Bean
    CommandLineRunner initUsers(UserRepository users) {
        return args -> {
            if (!users.existsByUsername("admin")) {
                users.save(AppUser.builder()
                        .username("admin")
                        .password(encoder.encode("admin123"))
                        .role(Role.ADMIN)
                        .enabled(true)
                        .build());
            }
            if (!users.existsByUsername("user")) {
                users.save(AppUser.builder()
                        .username("user")
                        .password(encoder.encode("user123"))
                        .role(Role.USER)
                        .enabled(true)
                        .build());
            }
        };
    }
}