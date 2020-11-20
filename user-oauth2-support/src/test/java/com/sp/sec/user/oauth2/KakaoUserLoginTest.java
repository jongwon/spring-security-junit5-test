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
public class KakaoUserLoginTest extends WithExtendedUserTest{

    ExtendedUser user;

    @BeforeEach
    void before(){
        prepareUserService();
        this.user = userService.registerOAuth2User(null, OAuth2UserSample.kakaoUser,
                ProvidedOAuth2User.Provider.kakao);
    }

    @DisplayName("1. 사이트에 가입하지 않은 사용자가 구글 사용자로 로그인 하면 사용자가로 등록 된다.")
    @Test
    void test_1() {
        assertNotNull(user.getUserId());
        assertEquals("jongwon", user.getName());
        assertEquals("http://k.kakaocdn.net/dn/XQHgC/btqyj3C5jCQ/KjiijMK462WPrRrnkoOtY0/img_110x110.jpg", user.getPicUrl());
        assertEquals("jongwons.choi@kakao.com", user.getEmail());

        List<ProvidedOAuth2User> list = providedOAuth2UserRepository.findAll();
        assertEquals(1, list.size());
        ProvidedOAuth2User kakaoUser = list.get(0);
        assertEquals("kakao_1534230750", kakaoUser.getOauth2UserId());
        assertEquals("jongwon", kakaoUser.getName());
        assertEquals("http://k.kakaocdn.net/dn/XQHgC/btqyj3C5jCQ/KjiijMK462WPrRrnkoOtY0/img_110x110.jpg", kakaoUser.getPicUrl());
        assertEquals("jongwons.choi@kakao.com", kakaoUser.getEmail());

        assertNotNull(kakaoUser.getRegistered());
        assertNotNull(kakaoUser.getLastLoggedIn());
        assertEquals(user.getUserId(), kakaoUser.getUserId());
        assertEquals(ProvidedOAuth2User.Provider.kakao, kakaoUser.getProvider());
    }

}
