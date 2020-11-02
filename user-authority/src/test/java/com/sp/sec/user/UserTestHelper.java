package com.sp.sec.user;


import com.sp.sec.user.domain.Authority;
import com.sp.sec.user.domain.User;
import com.sp.sec.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@AllArgsConstructor
public class UserTestHelper {

    private final UserService userService;

    public static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static User makeUser(String name){
        return User.builder()
                .name(name)
                .email(name+"@test.com")
                .password(encoder.encode(name+"123"))
                .enabled(true)
                .build();
    }

    public User createUser(String name) throws DuplicateKeyException {
        User user = makeUser(name);
        return userService.save(user);
    }

    public static User makeUser(String name, Authority... authorities){
        User user = makeUser(name);
        user.setAuthorities(Set.of(authorities));
        return user;
    }

    public User createUser(String name, String... authorities){
        User user = createUser(name);
        Stream.of(authorities).forEach(auth->userService.addAuthority(user.getUserId(), auth));
        return user;
    }

    public void assertUser(User user, String name){
        assertNotNull(user.getUserId());
        assertNotNull(user.getCreated());
        assertNotNull(user.getUpdated());
        assertTrue(user.isEnabled());
        assertEquals(name, user.getName());
        assertEquals(name+"@test.com", user.getEmail());
        assertNotNull(user.getPassword());
    }

    public void assertUser(User user, String name, String... authorities){
        assertUser(user, name);
        assertTrue(user.getAuthorities().containsAll(
                Stream.of(authorities).map(auth->new Authority(auth)).collect(Collectors.toList())
        ));
    }

}
