package com.sp.sec.user.oauth2;

import com.sp.sec.user.domain.Authority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;
import java.util.Set;

public class OAuth2UserSample {

    public static OAuth2User googleUser = new DefaultOAuth2User(Set.of(Authority.USER),
            Map.of(
                    "name", "옥탑방개발자",
                    "sub", "113976141374150070219",
                    "picture", "https://lh3.googleusercontent.com/a-/AOh14GgFLv4rMtdDUyBFDgsJggHdCK5IuKSLuOq9OwwLDyc=s96-c",
                    "email", "jongwons.choi@gmail.com"
            ), "sub");


    public static OAuth2User facebookUser = new DefaultOAuth2User(Set.of(Authority.USER),
            Map.of("id", "4000026893357972",
                    "name", "Jongwon Choi",
                    "email", "jongwons.choi@gmail.com"), "id");

    public static OAuth2User naverUser = new DefaultOAuth2User(Set.of(Authority.USER),
            Map.of(
                    "response", Map.of(
                            "id", "18997705",
                            "nickname", "슈타인",
                            "profile_image", "https://phinf.pstatic.net/contact/20180308_276/1520490317846up6kA_PNG/avatar_profile.png",
                            "email", "jongwons.choi@gmail.com",
                            "name", "최종원"
                    )
            ), "response");

    public static OAuth2User kakaoUser = new DefaultOAuth2User(Set.of(Authority.USER),
            Map.of(
                    "id", 1534230750,
                    "kakao_account",  Map.of(
                            "profile", Map.of(
                                    "nickname", "jongwon",
                                    "thumbnail_image_url", "http://k.kakaocdn.net/dn/XQHgC/btqyj3C5jCQ/KjiijMK462WPrRrnkoOtY0/img_110x110.jpg",
                                    "profile_image_url", "http://k.kakaocdn.net/dn/XQHgC/btqyj3C5jCQ/KjiijMK462WPrRrnkoOtY0/img_640x640.jpg"

                            ),
                            "email", "jongwons.choi@kakao.com"
                    )
            ), "id");

}
