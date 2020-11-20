package com.sp.sec.user.oauth2;


import com.sp.sec.user.oauth2.domain.ExtendedUser;
import com.sp.sec.user.oauth2.domain.ProvidedOAuth2User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataMongoTest
public class GoogleUserLoginTest extends WithExtendedUserTest{

    ExtendedUser user;

    @BeforeEach
    void before(){
        prepareUserService();
        this.user = userService.registerOAuth2User(null, OAuth2UserSample.googleUser,
                ProvidedOAuth2User.Provider.google);
    }

    @DisplayName("1. 사이트에 가입하지 않은 사용자가 구글 사용자로 로그인 하면 사용자가로 등록 된다.")
    @Test
    void test_1() {
        assertNotNull(user.getUserId());
        assertEquals("옥탑방개발자", user.getName());
        assertEquals("https://lh3.googleusercontent.com/a-/AOh14GgFLv4rMtdDUyBFDgsJggHdCK5IuKSLuOq9OwwLDyc=s96-c", user.getPicUrl());
        assertEquals("jongwons.choi@gmail.com", user.getEmail());

        List<ProvidedOAuth2User> list = providedOAuth2UserRepository.findAll();
        assertEquals(1, list.size());
        ProvidedOAuth2User googleInfo = list.get(0);
        assertEquals("google_113976141374150070219", googleInfo.getOauth2UserId());
        assertEquals("옥탑방개발자", googleInfo.getName());
        assertEquals("https://lh3.googleusercontent.com/a-/AOh14GgFLv4rMtdDUyBFDgsJggHdCK5IuKSLuOq9OwwLDyc=s96-c", googleInfo.getPicUrl());
        assertEquals("jongwons.choi@gmail.com", googleInfo.getEmail());

        assertNotNull(googleInfo.getRegistered());
        assertNotNull(googleInfo.getLastLoggedIn());
        assertEquals(user.getUserId(), googleInfo.getUserId());
        assertEquals(ProvidedOAuth2User.Provider.google, googleInfo.getProvider());
    }

    @DisplayName("2. ProvidedOAuth2User 와 ExtendedUser 가 잘 링크된다.")
    @Test
    void test_2() {
        List<ProvidedOAuth2User> providedOAuth2UserList = userService.getProvidedOAuth2UserList(user.getUserId());
        assertEquals(1, providedOAuth2UserList.size());
        assertEquals(ProvidedOAuth2User.Provider.google, providedOAuth2UserList.get(0).getProvider());
    }

    @DisplayName("3. 다시 로그인을 하더라도 새로운 사용자가 등록되지 않는다.")
    @Test
    void test_3() {
        ExtendedUser loginAgain = userService.registerOAuth2User(null, OAuth2UserSample.googleUser,
                ProvidedOAuth2User.Provider.google);
        assertEquals(user.getUserId(), loginAgain.getUserId());
    }


    @DisplayName("4. 구글로 로그인한 다음, 해당 유저로 네이버, 카카오, 페이스북을 차례로 로그인해 계정을 연결할 수 있다.")
    @Test
    void test_4() {
        userService.registerOAuth2User(user, OAuth2UserSample.facebookUser,
                ProvidedOAuth2User.Provider.facebook);
        assertEquals(1, userRepository.findAll().size());
        assertEquals(2, userService.getProvidedOAuth2UserList(user.getUserId()).size());

        userService.registerOAuth2User(user, OAuth2UserSample.naverUser,
                ProvidedOAuth2User.Provider.naver);
        assertEquals(1, userRepository.findAll().size());
        assertEquals(3, userService.getProvidedOAuth2UserList(user.getUserId()).size());

        userService.registerOAuth2User(user, OAuth2UserSample.kakaoUser,
                ProvidedOAuth2User.Provider.kakao);
        assertEquals(1, userRepository.findAll().size());
        assertEquals(4, userService.getProvidedOAuth2UserList(user.getUserId()).size());

    }


}
