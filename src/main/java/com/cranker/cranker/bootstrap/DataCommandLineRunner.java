package com.cranker.cranker.bootstrap;

import com.cranker.cranker.role.Role;
import com.cranker.cranker.role.RoleRepository;
import com.cranker.cranker.user.User;
import com.cranker.cranker.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
@AllArgsConstructor
public class DataCommandLineRunner implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0 && userRepository.count() == 0) {
            Role adminRole = new Role();
            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            adminRole.setName("ROLE_ADMIN");
            roleRepository.saveAll(List.of(userRole, adminRole));

            User admin = new User();
            admin.setFirstName("admin");
            admin.setLastName("admin");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(new BCryptPasswordEncoder().encode("!Admin123"));
            admin.setCreatedAt(LocalDateTime.now());
            admin.setIsVerified(true);
            admin.setRoles(Set.of(adminRole, userRole));

            User user = new User();
            user.setFirstName("user");
            user.setLastName("user");
            user.setEmail("user@gmail.com");
            user.setPassword(new BCryptPasswordEncoder().encode("!user123"));
            user.setCreatedAt(LocalDateTime.now());
            user.setIsVerified(true);
            user.setRoles(Set.of(userRole));

            userRepository.saveAll(List.of(admin, user));
        }
    }
}
