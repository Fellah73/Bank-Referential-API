package dz.banquealgerie.referential.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import dz.banquealgerie.referential.domain.entities.UserEntity;
import dz.banquealgerie.referential.domain.enumeration.UserRole;
import dz.banquealgerie.referential.repositories.auth.AuthRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataIntializer implements CommandLineRunner {
    private final AuthRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        UserEntity admin = userRepository.findByUsername("admin_ba")
                .orElseGet(() -> {
                    UserEntity newAdmin = new UserEntity();
                    newAdmin.setUsername("admin_ba");
                    newAdmin.setPassword(passwordEncoder.encode("admin123"));
                    newAdmin.setEmail("admin@bank-of-algeria.dz");
                    newAdmin.setRole(UserRole.ADMIN);
                    return userRepository.save(newAdmin);
                });

        UserEntity agent = userRepository.findByUsername("agent_ba")
                .orElseGet(() -> {
                    UserEntity newAgent = new UserEntity();
                    newAgent.setUsername("agent_ba");
                    newAgent.setPassword(passwordEncoder.encode("admin123"));
                    newAgent.setEmail("agent@bank-of-algeria.dz");
                    newAgent.setRole(UserRole.AGENT);
                    return userRepository.save(newAgent);
                });

    }
}
