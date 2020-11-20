package com.sp.sec.user.oauth2.service;


import com.sp.sec.user.oauth2.domain.ProvidedOAuth2User;
import com.sp.sec.user.oauth2.repository.ProvidedOAuth2UserRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProvidedOAuth2UserService {

    private final MongoTemplate mongoTemplate;
    private final ProvidedOAuth2UserRepository repository;

    public ProvidedOAuth2UserService(MongoTemplate mongoTemplate, ProvidedOAuth2UserRepository repository) {
        this.mongoTemplate = mongoTemplate;
        this.repository = repository;
    }

    public ProvidedOAuth2User save(ProvidedOAuth2User providedOAuth2User){
        providedOAuth2User.setRegistered(LocalDateTime.now());
        return repository.save(providedOAuth2User);
    }

    public boolean updateLastLoggedInTime(String oauth2UserId){
        return mongoTemplate.updateFirst(Query.query(Criteria.where("oauth2UserId").is(oauth2UserId)),
                Update.update("lastLoggedIn", LocalDateTime.now()), ProvidedOAuth2User.class).wasAcknowledged();
    }


    public Optional<ProvidedOAuth2User> find(String oauth2UserId) {
        return repository.findById(oauth2UserId);
    }

    public boolean changeUserId(String oauth2UserId, String userId) {
        Update update = Update.update("lastLoggedIn", LocalDateTime.now());
        update.set("userId", userId);
        return mongoTemplate.updateFirst(Query.query(Criteria.where("oauth2UserId").is(oauth2UserId)),
               update , ProvidedOAuth2User.class).wasAcknowledged();
    }

    public Optional<ProvidedOAuth2User> findProvidedOAuth2User(String userId, ProvidedOAuth2User.Provider provider) {
        return Optional.of(mongoTemplate.findOne(
                Query.query(Criteria.where("userId").is(userId).and("provider").is(provider)),
                ProvidedOAuth2User.class));
    }
}
