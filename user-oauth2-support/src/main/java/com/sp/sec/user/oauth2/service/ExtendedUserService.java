package com.sp.sec.user.oauth2.service;

import com.sp.sec.user.domain.Authority;
import com.sp.sec.user.domain.User;
import com.sp.sec.user.oauth2.domain.ExtendedUser;
import com.sp.sec.user.oauth2.domain.ProvidedOAuth2User;
import com.sp.sec.user.oauth2.repository.ExtendedUserRepository;
import com.sp.sec.user.oauth2.repository.ProvidedOAuth2UserRepository;
import com.sp.sec.user.repository.UserRepository;
import com.sp.sec.user.service.UserService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ExtendedUserService extends UserService {

    private final ExtendedUserRepository extendedUserRepository;
    private final ProvidedOAuth2UserService providedOAuth2UserService;

    public ExtendedUserService(MongoTemplate mongoTemplate,
                               UserRepository userRepository,
                               ExtendedUserRepository extendedUserRepository,
                               ProvidedOAuth2UserService providedOAuth2UserService) {
        super(mongoTemplate, userRepository);
        this.extendedUserRepository = extendedUserRepository;
        this.providedOAuth2UserService = providedOAuth2UserService;
    }

    public Optional<ExtendedUser> findExtendedUser(String userId){
        return extendedUserRepository.findById(userId);
    }


    public ExtendedUser registerOAuth2User(final ExtendedUser user, OAuth2User oAuth2User, ProvidedOAuth2User.Provider provider) {

        // 1. ProvidedOAuth2User 를 가져오고 없으면 등록한다.
        ProvidedOAuth2User providedOAuth2User = provider.convert(oAuth2User);
        ProvidedOAuth2User saved = providedOAuth2UserService.find(providedOAuth2User.getOauth2UserId()).orElseGet(()->{
           if(user == null){
               // 2. user 정보가 없으면 user를 새로 등록하고 리턴한다.
               ExtendedUser siteUser = ExtendedUser.of(User.builder()
                       .name(providedOAuth2User.getName())
                       .picUrl(providedOAuth2User.getPicUrl())
                       .email(providedOAuth2User.getEmail())
                       .authorities(Set.of(Authority.USER))
                       .enabled(true)
               .build());
               siteUser = (ExtendedUser) save(siteUser);
               providedOAuth2User.setUserId(siteUser.getUserId());
           }else{
               providedOAuth2User.setUserId(user.getUserId());
           }
           providedOAuth2User.setLastLoggedIn(LocalDateTime.now());
           return providedOAuth2UserService.save(providedOAuth2User);
        });

        if(user == null) return findExtendedUser(saved.getUserId()).get();

        // 3. user 정보가 있고 user와 같으면 lastLogin 값을 업데이트 하고 user 를 리턴한다.
        if(user.getUserId().equals(saved.getUserId())){
            providedOAuth2UserService.updateLastLoggedInTime(saved.getOauth2UserId());
            return user;
        }

        // 4. user 정보가 있고 user와 같지 않으면 ... ??? user 로 userId를 치환하고 lastLogin 을 업데이트 하고 user를 리턴한다.
        // 	* 필요하다면 변경 정보를 별도 DB로 관리한다.
        // 	* 해당 provider로 등록된 계정이 없다면 등록한다.
        if(providedOAuth2UserService.findProvidedOAuth2User(user.getUserId(), provider).isPresent()){
            throw new IllegalArgumentException("이미 등록된 사용자 정보가 있습니다.");
        }

        providedOAuth2UserService.changeUserId(saved.getOauth2UserId(), user.getUserId());
        return user;
    }


    public List<ProvidedOAuth2User> getProvidedOAuth2UserList(String userId){
        return mongoTemplate.find(Query.query(Criteria.where("userId").is(userId)), ProvidedOAuth2User.class);
    }


}
