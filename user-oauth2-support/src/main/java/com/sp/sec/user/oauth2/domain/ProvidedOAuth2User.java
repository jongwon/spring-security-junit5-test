package com.sp.sec.user.oauth2.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDateTime;
import java.util.Map;

import static java.lang.String.format;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "sp_provided_oauth2_user")
public class ProvidedOAuth2User {

    @Id
    private String oauth2UserId;

    private String userId; // refer

    private Provider provider;

    private String name;
    private String email;
    private String picUrl;

    private LocalDateTime registered;
    private LocalDateTime lastLoggedIn;

    public static enum Provider {
        google {
            public ProvidedOAuth2User convert(OAuth2User user){
                return ProvidedOAuth2User.builder()
                        .oauth2UserId(format("%s_%s", name(), user.getAttribute("sub")))
                        .name(user.getAttribute("name"))
                        .email(user.getAttribute("email"))
                        .picUrl(user.getAttribute("picture"))
                        .provider(google)
                        .build();
            }
        },
        facebook{
            public ProvidedOAuth2User convert(OAuth2User user){
                return ProvidedOAuth2User.builder()
                        .oauth2UserId(format("%s_%s", name(), user.getAttribute("id")))
                        .name(user.getAttribute("name"))
                        .email(user.getAttribute("email"))
//                        .picUrl()
                        .provider(facebook)
                        .build();
            }
        },
        naver{
            public ProvidedOAuth2User convert(OAuth2User user){
                Map<String, Object> response = user.getAttribute("response");
                return ProvidedOAuth2User.builder()
                        .oauth2UserId(format("%s_%s", name(), response.get("id")))
                        .name(""+response.get("name"))
                        .email(""+response.get("email"))
                        .picUrl(""+response.get("profile_image"))
                        .provider(naver)
                        .build();
            }
        },
        kakao{
            public ProvidedOAuth2User convert(OAuth2User user){
                Map<String, Object> kakaoAccount = user.getAttribute("kakao_account");
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                return ProvidedOAuth2User.builder()
                        .oauth2UserId(format("%s_%s", name(), user.getAttribute("id")))
                        .name(""+profile.get("nickname"))
                        .email(""+kakaoAccount.get("email"))
                        .picUrl(""+profile.get("thumbnail_image_url"))
                        .provider(kakao)
                        .build();
            }
        }
        ;

        public abstract ProvidedOAuth2User convert(OAuth2User user);
    }

}
