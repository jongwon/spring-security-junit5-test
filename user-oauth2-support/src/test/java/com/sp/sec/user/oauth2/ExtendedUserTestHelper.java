package com.sp.sec.user.oauth2;


import com.sp.sec.user.domain.Authority;
import com.sp.sec.user.domain.User;
import com.sp.sec.user.oauth2.domain.ExtendedUser;
import com.sp.sec.user.oauth2.service.ExtendedUserService;
import com.sp.sec.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@AllArgsConstructor
public class ExtendedUserTestHelper {

    private final ExtendedUserService userService;

    private final PasswordEncoder passwordEncoder;

    public ExtendedUser createUser(String name) throws DuplicateKeyException {
        ExtendedUser user = ExtendedUser.of(User.builder()
                .name(name)
                .email(name+"@test.com")
                .password(passwordEncoder.encode(name+"123"))
                .enabled(true)
                .build());
        user.setAttributes(Map.of("from", "oauth2_support"));
        return (ExtendedUser) userService.save(user);
    }

    public ExtendedUser createUser(String name, String... authorities){
        ExtendedUser user = createUser(name);
        Stream.of(authorities).forEach(auth->userService.addAuthority(user.getUserId(), auth));
        return user;
    }

    public static void assertUser(ExtendedUser user, String name){
        assertNotNull(user.getUserId());
        assertNotNull(user.getCreated());
        assertNotNull(user.getUpdated());
        assertTrue(user.isEnabled());
        assertEquals(name, user.getName());
        assertEquals(name+"@test.com", user.getEmail());
//        assertEquals(name+"123", user.getPassword());
        assertEquals("oauth2_support", user.getAttribute("from"));
    }

    public static void assertUser(ExtendedUser user, String name, String... authorities){
        assertUser(user, name);
        assertTrue(user.getAuthorities().containsAll(
                Stream.of(authorities).map(auth->new Authority(auth)).collect(Collectors.toList())
        ));
    }

}
