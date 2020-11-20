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
public class NaverUserLoginTest extends WithExtendedUserTest {

    ExtendedUser user;

    @BeforeEach
    void before(){
        prepareUserService();
        this.user = userService.registerOAuth2User(null, OAuth2UserSample.naverUser,
                ProvidedOAuth2User.Provider.naver);
    }

    @DisplayName("1. 사이트에 가입하지 않은 사용자가 구글 사용자로 로그인 하면 사용자가로 등록 된다.")
    @Test
    void test_1() {
        assertNotNull(user.getUserId());
        assertEquals("최종원", user.getName());
        assertEquals("https://phinf.pstatic.net/contact/20180308_276/1520490317846up6kA_PNG/avatar_profile.png", user.getPicUrl());
        assertEquals("jongwons.choi@gmail.com", user.getEmail());

        List<ProvidedOAuth2User> list = providedOAuth2UserRepository.findAll();
        assertEquals(1, list.size());
        ProvidedOAuth2User naverUser = list.get(0);
        assertEquals("naver_18997705", naverUser.getOauth2UserId());
        assertEquals("최종원", naverUser.getName());
        assertEquals("https://phinf.pstatic.net/contact/20180308_276/1520490317846up6kA_PNG/avatar_profile.png", naverUser.getPicUrl());
        assertEquals("jongwons.choi@gmail.com", naverUser.getEmail());

        assertNotNull(naverUser.getRegistered());
        assertNotNull(naverUser.getLastLoggedIn());
        assertEquals(user.getUserId(), naverUser.getUserId());
        assertEquals(ProvidedOAuth2User.Provider.naver, naverUser.getProvider());
    }

}
