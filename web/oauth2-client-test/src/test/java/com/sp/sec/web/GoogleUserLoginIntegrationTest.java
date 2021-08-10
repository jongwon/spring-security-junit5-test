package com.sp.sec.web;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GoogleUserLoginIntegrationTest {

    @MockBean
    OAuth2LoginAuthenticationFilter auth2LoginAuthenticationFilter;

    @DisplayName("1. 구글 로그인을 시도한다.")
    @Test
    void test_1() {



//        when(auth2LoginAuthenticationFilter.attemptAuthentication(any(), any())).thenReturn(new DefaultOidcUser())

    }

}
