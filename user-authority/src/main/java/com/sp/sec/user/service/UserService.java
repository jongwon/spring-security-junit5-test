package com.sp.sec.user.service;


import com.sp.sec.user.domain.Authority;
import com.sp.sec.user.domain.User;
import com.sp.sec.user.repository.UserRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserService implements UserDetailsService {

    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepository;

    public UserService(MongoTemplate mongoTemplate, UserRepository userRepository) {
        this.mongoTemplate = mongoTemplate;
        this.userRepository = userRepository;
        ensureIndex();
    }

    private void ensureIndex() {
        mongoTemplate.indexOps(User.class).
                ensureIndex(new Index().on("email", Sort.Direction.ASC).unique());
    }

    public User save(User user) throws DuplicateKeyException  {
        if(StringUtils.isEmpty(user.getUserId())){
            user.setCreated(LocalDateTime.now());
        }
        user.setUpdated(LocalDateTime.now());
//        user.setEnabled(true);
        return userRepository.save(user);
    }

    public Optional<User> findUser(String userId){
        return userRepository.findById(userId);
    }

    public boolean updateUserName(String userId, String userName){
        Update update = new Update();
        update.set("name", userName);
        update.set("updated", LocalDateTime.now());
        return mongoTemplate.updateFirst(Query.query(Criteria.where("userId").is(userId)),
                update, User.class).wasAcknowledged();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(()->new UsernameNotFoundException(username+"이 존재하지 않음"));
    }

    public boolean addAuthority(String userId, String authority){
        Update update = new Update();
        update.push("authorities", new Authority((authority)));
        update.set("updated", LocalDateTime.now());
        return mongoTemplate.updateFirst(Query.query(Criteria.where("userId").is(userId)),
                update, User.class).wasAcknowledged();
    }

    public boolean removeAuthority(String userId, String authority){
        Update update = new Update();
        update.pull("authorities", new Authority((authority)));
        update.set("updated", LocalDateTime.now());
        return mongoTemplate.updateFirst(Query.query(Criteria.where("userId").is(userId)),
                update, User.class).wasAcknowledged();
    }

    public void clearUsers() {
        userRepository.deleteAll();
    }

    public Page<User> listUsers(Integer page, Integer size) {
        return userRepository.findAll(PageRequest.of(page-1, size));
    }

    public Map<String, User> getUserMap(Collection<String> userIds){
        if(userIds == null || userIds.isEmpty()) return new HashMap<>();
        return StreamSupport.stream(userRepository.findAllById(userIds).spliterator(), false)
                .collect(Collectors.toMap(User::getUserId, Function.identity()));
    }

}
