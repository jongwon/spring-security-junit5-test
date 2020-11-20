package com.sp.sec.user.oauth2.repository;

import com.sp.sec.user.oauth2.domain.ProvidedOAuth2User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvidedOAuth2UserRepository extends MongoRepository<ProvidedOAuth2User, String> {

}
