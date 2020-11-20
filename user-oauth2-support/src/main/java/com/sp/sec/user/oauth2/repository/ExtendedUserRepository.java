package com.sp.sec.user.oauth2.repository;

import com.sp.sec.user.oauth2.domain.ExtendedUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExtendedUserRepository extends MongoRepository<ExtendedUser, String> {

}
