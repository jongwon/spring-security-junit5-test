package com.sp.sec.web.config;

import com.sp.sec.user.domain.Authority;
import com.sp.sec.user.domain.User;
import com.sp.sec.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DBInit implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        userService.clearUsers();
        User user1 = User.builder().name("user1")
                .email("user1@test.com")
                .password(passwordEncoder.encode("1234"))
                .enabled(true)
                .authorities(Set.of(Authority.USER))
                .build();
        User admin = User.builder().name("admin")
                .email("admin@test.com")
                .password(passwordEncoder.encode("admin"))
                .enabled(true)
                .authorities(Set.of(Authority.ADMIN))
                .build();

        userService.save(user1);
        userService.save(admin);
    }
}
