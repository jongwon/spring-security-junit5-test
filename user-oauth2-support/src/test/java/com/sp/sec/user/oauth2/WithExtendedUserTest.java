package com.sp.sec.user.oauth2;

import com.sp.sec.user.oauth2.repository.ExtendedUserRepository;
import com.sp.sec.user.oauth2.repository.ProvidedOAuth2UserRepository;
import com.sp.sec.user.oauth2.service.ExtendedUserService;
import com.sp.sec.user.oauth2.service.ProvidedOAuth2UserService;
import com.sp.sec.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

public class WithExtendedUserTest {

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ExtendedUserRepository extendedUserRepository;

    @Autowired
    protected ProvidedOAuth2UserRepository providedOAuth2UserRepository;

    protected ProvidedOAuth2UserService providedOAuth2UserService;

    protected ExtendedUserService userService;

    protected ExtendedUserTestHelper userTestHelper;

    protected void prepareUserService(){
        this.userRepository.deleteAll();
        this.providedOAuth2UserRepository.deleteAll();
        this.providedOAuth2UserService = new ProvidedOAuth2UserService(mongoTemplate, providedOAuth2UserRepository);
        this.userService = new ExtendedUserService(mongoTemplate, userRepository,
                extendedUserRepository,
                providedOAuth2UserService);
        this.userTestHelper = new ExtendedUserTestHelper(userService, NoOpPasswordEncoder.getInstance());
    }
}
