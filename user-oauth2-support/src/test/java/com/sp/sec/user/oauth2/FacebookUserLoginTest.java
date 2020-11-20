package com.sp.sec.user.oauth2;


import com.sp.sec.user.oauth2.domain.ExtendedUser;
import com.sp.sec.user.oauth2.domain.ProvidedOAuth2User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
public class FacebookUserLoginTest extends WithExtendedUserTest{

    ExtendedUser user;

    @BeforeEach
    void before(){
        prepareUserService();
        this.user = userService.registerOAuth2User(null, OAuth2UserSample.facebookUser,
                ProvidedOAuth2User.Provider.facebook);
    }

    @DisplayName("1. 사이트에 가입하지 않은 사용자가 페이스북 사용자로 로그인 하면 사용자가로 등록 된다.")
    @Test
    void test_1() {
        assertNotNull(user.getUserId());
        assertEquals("Jongwon Choi", user.getName());
        assertNull( user.getPicUrl());
        assertEquals("jongwons.choi@gmail.com", user.getEmail());

        List<ProvidedOAuth2User> list = providedOAuth2UserRepository.findAll();
        assertEquals(1, list.size());
        ProvidedOAuth2User facebookUser = list.get(0);
        assertEquals("facebook_4000026893357972", facebookUser.getOauth2UserId());
        assertEquals("Jongwon Choi", facebookUser.getName());
        assertNull(facebookUser.getPicUrl());
        assertEquals("jongwons.choi@gmail.com", facebookUser.getEmail());

        assertNotNull(facebookUser.getRegistered());
        assertNotNull(facebookUser.getLastLoggedIn());
        assertEquals(user.getUserId(), facebookUser.getUserId());
        assertEquals(ProvidedOAuth2User.Provider.facebook, facebookUser.getProvider());
    }
}
