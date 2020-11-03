package com.sp.sec.web;

import com.sp.sec.user.UserTestHelper;
import com.sp.sec.user.domain.Authority;
import com.sp.sec.user.domain.User;
import com.sp.sec.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

public class SpJwtUserAdminIntegrationTest extends SpIntegrationTest{

    @Autowired
    protected UserService userService;
    protected UserTestHelper userTestHelper;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected User USER1;
    protected User ADMIN;

    void prepareUserAdmin(){
        userService.clearUsers();
        userTestHelper = new UserTestHelper(userService, passwordEncoder);
        this.USER1 = userService.save(userTestHelper.makeUser("user1", Authority.USER));
        this.ADMIN = userService.save(userTestHelper.makeUser("admin", Authority.ADMIN));
    }

}
