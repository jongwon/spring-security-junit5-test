package com.sp.sec.web.config;

import com.sp.sec.user.oauth2.domain.ExtendedUser;
import com.sp.sec.user.oauth2.service.ExtendedUserService;
import com.sp.sec.user.oauth2.service.ProvidedOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;

@Component
public class SpOidcUserToSiteUserFilter implements Filter {

    @Autowired
    private ExtendedUserService extendedUserService;

    @Autowired
    private ProvidedOAuth2UserService providedOAuth2UserService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth instanceof OAuth2AuthenticationToken && auth.getPrincipal() instanceof OidcUser){
            OidcUser oidcUser = (OidcUser) ((OAuth2AuthenticationToken)auth).getPrincipal();

            // TODO : 현재는 OIDC 유저가 google 밖에 없다.
            providedOAuth2UserService.find("google_"+oidcUser.getAttribute("sub")).ifPresent(providedOAuth2User -> {
                extendedUserService.findExtendedUser(providedOAuth2User.getUserId()).ifPresent(user->{
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
                    );
                });
            });
        }
        filterChain.doFilter(request, response);
    }

}
