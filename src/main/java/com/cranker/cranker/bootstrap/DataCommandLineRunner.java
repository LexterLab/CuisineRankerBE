package com.cranker.cranker.bootstrap;

import com.cranker.cranker.role.RoleRepository;
import com.cranker.cranker.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Profile("docker")
public class DataCommandLineRunner implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0 && userRepository.count() == 0) {
            BootstrapHelper.setUp(roleRepository, userRepository);
        }
    }
}
