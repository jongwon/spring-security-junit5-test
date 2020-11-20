package com.sp.sec.user.oauth2.domain;

import com.sp.sec.user.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

@Data
@NoArgsConstructor
public class ExtendedUser extends User implements OAuth2User {

    private Map<String, Object> attributes;

    public ExtendedUser(User user){
        super(user.getUserId(), user.getEmail(), user.getName(), user.getPicUrl(), user.getPassword()
        , user.isEnabled(), user.getAuthorities(), user.getCreated(), user.getUpdated());
    }

    public static ExtendedUser of(User user){
        return new ExtendedUser(user);
    }

    @Override
    public <A> A getAttribute(String name) {
        return attributes == null ? null : (A) attributes.get(name);
    }

}
