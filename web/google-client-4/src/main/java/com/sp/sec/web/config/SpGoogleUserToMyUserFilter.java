package com.sp.sec.web.config;

import com.sp.sec.user.domain.Authority;
import com.sp.sec.user.domain.User;
import com.sp.sec.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;
import java.util.Set;

@Component
public class SpGoogleUserToMyUserFilter implements Filter {

    @Autowired
    private UserService userService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth instanceof OAuth2AuthenticationToken){
            OidcUser googleUser = (OidcUser)((OAuth2AuthenticationToken) auth).getPrincipal();
            User user = userService.findUser("google_"+googleUser.getSubject())
                    .orElseGet(()->
                       userService.save(User.builder()
                               .userId("google_"+googleUser.getSubject())
                               .email(googleUser.getEmail())
                               .authorities(Set.of(Authority.USER, new Authority("FROM_GOOGLE")))
                               .enabled(true)
                       .build())
                    );
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
            );
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

}
