package com.sp.sec.web.service;

import com.sp.sec.user.oauth2.domain.ExtendedUser;
import com.sp.sec.user.oauth2.domain.ProvidedOAuth2User;
import com.sp.sec.user.oauth2.service.ExtendedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class SpOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private ExtendedUserService extendedUserService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // TODO : ExtendedUser 로 바꾼뒤 리턴한다.
        ExtendedUser user = extendedUserService.registerOAuth2User(
                auth == null ? null : (ExtendedUser) auth.getPrincipal(),
                oAuth2User,
                getProvider(userRequest.getClientRegistration().getRegistrationId())
        );

        return user;
    }

    private ProvidedOAuth2User.Provider getProvider(String registrationId) {
        return ProvidedOAuth2User.Provider.valueOf(registrationId);
    }


}
