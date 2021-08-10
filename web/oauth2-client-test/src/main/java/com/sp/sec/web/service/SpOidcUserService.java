package com.sp.sec.web.service;

import com.sp.sec.user.oauth2.domain.ExtendedUser;
import com.sp.sec.user.oauth2.domain.ProvidedOAuth2User;
import com.sp.sec.user.oauth2.service.ExtendedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class SpOidcUserService extends OidcUserService {

    @Autowired
    private ExtendedUserService extendedUserService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // TODO : ExtendedUser 를 생성을 보장한다.
        ExtendedUser user = extendedUserService.registerOAuth2User(
                auth == null ? null : (ExtendedUser) auth.getPrincipal(),
                oidcUser,
                getProvider(userRequest.getClientRegistration().getRegistrationId())
        );

        return oidcUser;
    }

    private ProvidedOAuth2User.Provider getProvider(String registrationId) {
        return ProvidedOAuth2User.Provider.valueOf(registrationId);
    }

}
