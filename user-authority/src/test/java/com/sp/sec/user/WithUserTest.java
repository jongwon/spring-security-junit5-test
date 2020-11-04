package com.sp.sec.user;

import com.sp.sec.user.repository.UserRepository;
import com.sp.sec.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

public class WithUserTest {

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Autowired
    protected UserRepository userRepository;

    protected UserService userService;

    protected UserTestHelper userTestHelper;

    protected void prepareUserService(){
        this.userRepository.deleteAll();
        this.userService = new UserService(mongoTemplate, userRepository);
        this.userTestHelper = new UserTestHelper(userService, NoOpPasswordEncoder.getInstance());
    }
}
