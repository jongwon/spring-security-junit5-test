package com.sp.sec.web;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class JWTTokenTest {

    static void printClaim(String key, Claim value){
        if(value.isNull()){
            System.out.printf("%s:%s\n", key, "none");
            return;
        }
        if(value.asString() != null){
            System.out.printf("%s:{str}%s\n", key, value.asString());
            return;
        }
        if(value.asLong() != null){
            System.out.printf("%s:{lng}%d\n", key, value.asLong());
            return;
        }
        if(value.asInt() != null ){
            System.out.printf("%s:{int}%d\n", key, value.asInt());
            return;
        }
        if(value.asBoolean() != null){
            System.out.printf("%s:{bol}%b\n", key, value.asBoolean());
            return;
        }
        if(value.asDate() != null){
            System.out.printf("%s:{dte}%s\n", key, value.asDate().toString());
            return;
        }
        if(value.asDouble() != null){
            System.out.printf("%s:{dbl}%f\n", key, value.asDouble());
            return;
        }
        String[] values = value.asArray(String.class);
        if(values != null){
            System.out.printf("%s:{arr}%s\n", key, Stream.of(values).collect(Collectors.joining(",")));
            return;
        }
        Map valueMap = value.asMap();
        if(valueMap != null) {
            System.out.printf("%s:{map}%s\n", key, valueMap);
            return;
        }
        System.out.println("====>> unknown type for :"+key);
    }

    @DisplayName("1. JWT 토큰이 잘 만들어 진다.")
    @Test
    @Disabled
    void test_() throws InterruptedException {

        Algorithm AL = Algorithm.HMAC256("hello");
        String token = JWT.create()
                .withSubject("jongwon")
                .withClaim("exp", Instant.now().getEpochSecond()+2)
                .withArrayClaim("role", new String[]{"ROLE_ADMIN", "ROLE_USER"})
                .sign(AL);
        System.out.println(token);
//        DecodedJWT decode = JWT.decode(token);

        Thread.sleep(1000);

        DecodedJWT decode = JWT.require(AL).build().verify(token);

        printClaim("typ", decode.getHeaderClaim("typ"));
        printClaim("alg", decode.getHeaderClaim("alg"));
        System.out.println("=======");
        decode.getClaims().forEach(JWTTokenTest::printClaim);

        Thread.sleep(2000);

        assertThrows(TokenExpiredException.class, ()->{
            JWT.require(AL).build().verify(token);
        });
    }

}
